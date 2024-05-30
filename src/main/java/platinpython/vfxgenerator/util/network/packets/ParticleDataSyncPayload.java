package platinpython.vfxgenerator.util.network.packets;

import dev.lukebemish.codecextras.Asymmetry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.data.ParticleData;

import java.util.function.Consumer;

public record ParticleDataSyncPayload(Asymmetry<Consumer<ParticleData>, ParticleData> asymmetry, BlockPos pos)
    implements CustomPacketPayload {
    public static final Type<ParticleDataSyncPayload> TYPE =
        new Type<>(Util.createNamespacedResourceLocation("particle_data_sync"));
    public static final StreamCodec<FriendlyByteBuf, ParticleDataSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ParticleData.DIFF_STREAM_CODEC, ParticleDataSyncPayload::asymmetry, BlockPos.STREAM_CODEC,
        ParticleDataSyncPayload::pos, ParticleDataSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<ParticleDataSyncPayload> {
        @SuppressWarnings("resource")
        public void handle(ParticleDataSyncPayload message, IPayloadContext context) {
            Player sender = context.player();
            BlockEntity tileEntity = sender.level().getBlockEntity(message.pos);
            if (tileEntity instanceof VFXGeneratorBlockEntity vfxGeneratorBlockEntity) {
                message.asymmetry.decoding()
                    .resultOrPartial(VFXGenerator.LOGGER::error)
                    .ifPresent(consumer -> consumer.accept(vfxGeneratorBlockEntity.getParticleData()));
            }
            sender.level()
                .sendBlockUpdated(
                    message.pos, sender.level().getBlockState(message.pos), sender.level().getBlockState(message.pos),
                    Block.UPDATE_ALL
                );
        }
    }
}
