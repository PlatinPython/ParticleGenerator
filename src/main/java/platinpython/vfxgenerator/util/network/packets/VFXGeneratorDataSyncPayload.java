package platinpython.vfxgenerator.util.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.util.Util;

public record VFXGeneratorDataSyncPayload(CompoundTag tag, BlockPos pos) implements CustomPacketPayload {
    public static final Type<VFXGeneratorDataSyncPayload> TYPE =
        new Type<>(Util.createNamespacedResourceLocation("vfx_generator_data_sync"));
    public static final StreamCodec<ByteBuf, VFXGeneratorDataSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG, VFXGeneratorDataSyncPayload::tag, BlockPos.STREAM_CODEC,
        VFXGeneratorDataSyncPayload::pos, VFXGeneratorDataSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<VFXGeneratorDataSyncPayload> {
        @SuppressWarnings("resource")
        public void handle(VFXGeneratorDataSyncPayload message, IPayloadContext context) {
            Player sender = context.player();
            BlockEntity tileEntity = sender.level().getBlockEntity(message.pos);
            if (tileEntity instanceof VFXGeneratorBlockEntity vfxGeneratorBlockEntity) {
                vfxGeneratorBlockEntity.loadFromTag(message.tag);
                tileEntity.setChanged();
            }
            sender.level()
                .sendBlockUpdated(
                    message.pos, sender.level().getBlockState(message.pos), sender.level().getBlockState(message.pos),
                    Block.UPDATE_ALL
                );
        }
    }
}
