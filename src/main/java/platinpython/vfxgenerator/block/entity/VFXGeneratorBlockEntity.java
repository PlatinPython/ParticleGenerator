package platinpython.vfxgenerator.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import platinpython.vfxgenerator.block.VFXGeneratorBlock;
import platinpython.vfxgenerator.util.ClientUtils;
import platinpython.vfxgenerator.util.Color;
import platinpython.vfxgenerator.util.data.ParticleData;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.registries.BlockEntityRegistry;
import platinpython.vfxgenerator.util.registries.DataComponentRegistry;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.util.concurrent.ThreadLocalRandom;

public class VFXGeneratorBlockEntity extends BlockEntity {
    public static final String PARTICLE_DATA_KEY = "particleData";

    private final ParticleData particleData = new ParticleData(this);

    public VFXGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.VFX_GENERATOR.get(), pos, state);
    }

    public ParticleData getParticleData() {
        return particleData;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(blockEntity instanceof VFXGeneratorBlockEntity generatorBlockEntity)) {
            return;
        }
        if ((state.getValue(VFXGeneratorBlock.INVERTED) && !state.getValue(VFXGeneratorBlock.POWERED))
            || (!state.getValue(VFXGeneratorBlock.INVERTED) && state.getValue(VFXGeneratorBlock.POWERED))) {
            if (generatorBlockEntity.particleData.isEnabled()) {
                if (level.getGameTime() % generatorBlockEntity.particleData.getDelay() == 0) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    if (generatorBlockEntity.particleData.getSelected().isEmpty()) {
                        return;
                    }
                    ParticleType particleType =
                        DataManager.selectableParticles().get(generatorBlockEntity.particleData.getRandomSelected());
                    if (particleType == null) {
                        return;
                    }

                    int color;
                    if (generatorBlockEntity.particleData.useHSB()) {
                        color = Color.getRandomHSBColor(random, new float[]{
                            generatorBlockEntity.particleData.getHueBot(),
                            generatorBlockEntity.particleData.getSaturationBot(),
                            generatorBlockEntity.particleData.getBrightnessBot()
                        }, new float[]{
                            generatorBlockEntity.particleData.getHueTop(),
                            generatorBlockEntity.particleData.getSaturationTop(),
                            generatorBlockEntity.particleData.getBrightnessTop()
                        });
                    } else {
                        color = Color.getRandomRGBColor(
                            random, generatorBlockEntity.particleData.getRGBColorBot(),
                            generatorBlockEntity.particleData.getRGBColorTop()
                        );
                    }

                    int lifetime = Math.round(
                        (generatorBlockEntity.particleData.getLifetimeBot()
                            + (random.nextFloat() * (generatorBlockEntity.particleData.getLifetimeTop()
                                - generatorBlockEntity.particleData.getLifetimeBot())))
                    );

                    float size = generatorBlockEntity.particleData.getSizeBot()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.getSizeTop()
                            - generatorBlockEntity.particleData.getSizeBot()));

                    Vec3 center = Vec3.atCenterOf(pos);
                    double spawnX = center.x + generatorBlockEntity.particleData.getSpawnXBot()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.getSpawnXTop()
                            - generatorBlockEntity.particleData.getSpawnXBot()));
                    double spawnY = center.y + generatorBlockEntity.particleData.getSpawnYBot()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.getSpawnYTop()
                            - generatorBlockEntity.particleData.getSpawnYBot()));
                    double spawnZ = center.z + generatorBlockEntity.particleData.getSpawnZBot()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.getSpawnZTop()
                            - generatorBlockEntity.particleData.getSpawnZBot()));
                    center = new Vec3(spawnX, spawnY, spawnZ);

                    double motionX = generatorBlockEntity.particleData.getMotionXBot()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.getMotionXTop()
                            - generatorBlockEntity.particleData.getMotionXBot()));
                    double motionY = generatorBlockEntity.particleData.getMotionYBot()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.getMotionYTop()
                            - generatorBlockEntity.particleData.getMotionYBot()));
                    double motionZ = generatorBlockEntity.particleData.getMotionZBot()
                        + (random.nextFloat() * (generatorBlockEntity.particleData.getMotionZTop()
                            - generatorBlockEntity.particleData.getMotionZBot()));
                    Vec3 motion = new Vec3(motionX, motionY, motionZ);

                    ClientUtils.addParticle(
                        level, particleType, color, lifetime, size, center, motion,
                        generatorBlockEntity.particleData.getGravity(),
                        generatorBlockEntity.particleData.hasCollision(),
                        generatorBlockEntity.particleData.isFullBright()
                    );
                }
            }
        }
    }

    public CompoundTag saveToTag(CompoundTag tag) {
        tag.put(PARTICLE_DATA_KEY, this.particleData.saveToTag());
        return tag;
    }

    public void loadFromTag(CompoundTag tag) {
        this.particleData.loadFromTag(tag.getCompound(PARTICLE_DATA_KEY));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        CustomData particleData = componentInput.get(DataComponentRegistry.PARTICLE_DATA);
        if (particleData != null) {
            this.particleData.loadFromTag(particleData.copyTag());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentRegistry.PARTICLE_DATA, CustomData.of(this.particleData.saveToTag()));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        saveToTag(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        loadFromTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveToTag(super.getUpdateTag(provider));
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        loadFromTag(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        loadFromTag(pkt.getTag());
    }
}
