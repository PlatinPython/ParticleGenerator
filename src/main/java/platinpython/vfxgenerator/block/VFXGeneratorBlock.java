package platinpython.vfxgenerator.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.util.ClientUtils;
import platinpython.vfxgenerator.util.network.packets.VFXGeneratorDestroyParticlesPayload;
import platinpython.vfxgenerator.util.registries.BlockEntityRegistry;
import platinpython.vfxgenerator.util.registries.DataComponentRegistry;

import java.util.List;

public class VFXGeneratorBlock extends BaseEntityBlock {
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final String INVERTED_KEY = INVERTED.getName();

    public VFXGeneratorBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion());
        this.registerDefaultState(
            this.stateDefinition.any().setValue(INVERTED, Boolean.FALSE).setValue(POWERED, Boolean.FALSE)
        );
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(INVERTED, POWERED);
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        Item.TooltipContext context,
        List<Component> tooltip,
        TooltipFlag flag
    ) {
        if (stack.has(DataComponentRegistry.PARTICLE_DATA)) {
            tooltip.add(ClientUtils.getGuiTranslationTextComponent("dataSaved"));
        }
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
            .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.VFX_GENERATOR.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> blockEntityType
    ) {
        return level.isClientSide
            ? createTickerHelper(
                blockEntityType, BlockEntityRegistry.VFX_GENERATOR.get(), VFXGeneratorBlockEntity::tick
            )
            : null;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(VFXGeneratorBlock::new);
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block block,
        BlockPos fromPos,
        boolean isMoving
    ) {
        if (!level.isClientSide) {
            if (state.getValue(POWERED) != level.hasNeighborSignal(pos)) {
                level.setBlock(pos, state.cycle(POWERED), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(POWERED), 2);
        }
    }

    @Override
    public InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (player.getMainHandItem().isEmpty()) {
            if (player.isShiftKeyDown() && player.getOffhandItem().isEmpty()) {
                level.setBlock(pos, state.cycle(INVERTED), 2);
                return InteractionResult.SUCCESS;
            } else {
                if (level.isClientSide) {
                    BlockEntity tileEntity = level.getBlockEntity(pos);
                    if (tileEntity instanceof VFXGeneratorBlockEntity) {
                        ClientUtils.openVFXGeneratorScreen((VFXGeneratorBlockEntity) tileEntity);
                        return InteractionResult.CONSUME;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(
        BlockState state,
        HitResult target,
        LevelReader level,
        BlockPos pos,
        Player player
    ) {
        ItemStack stack = new ItemStack(this);
        stack.update(
            DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY,
            blockItemStateProperties -> blockItemStateProperties.with(INVERTED, state.getValue(INVERTED))
        );
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VFXGeneratorBlockEntity vfxGeneratorBlockEntity) {
            stack.applyComponents(vfxGeneratorBlockEntity.collectComponents());
        }
        return stack;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level instanceof ServerLevel serverLevel && !state.is(newState.getBlock())) {
            PacketDistributor.sendToPlayersTrackingChunk(
                serverLevel, new ChunkPos(pos), new VFXGeneratorDestroyParticlesPayload(Vec3.atCenterOf(pos))
            );
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
