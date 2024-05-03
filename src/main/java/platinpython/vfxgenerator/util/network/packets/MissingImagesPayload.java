package platinpython.vfxgenerator.util.network.packets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record MissingImagesPayload(ImmutableList<ResourceLocation> list) implements CustomPacketPayload {
    public static final Type<MissingImagesPayload> TYPE =
        new Type<>(Util.createNamespacedResourceLocation("missing_images"));
    public static final StreamCodec<ByteBuf, MissingImagesPayload> STREAM_CODEC =
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list())
            .map(ImmutableList::copyOf, Function.identity())
            .map(MissingImagesPayload::new, MissingImagesPayload::list);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<MissingImagesPayload> {
        public void handle(MissingImagesPayload message, IPayloadContext context) {
            // TODO look into improving memory footprint
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            ImmutableMap<ResourceLocation, IoSupplier<InputStream>> requiredImages = DataManager.requiredImages();
            List<Pair<ResourceLocation, byte[]>> list = message.list.stream()
                .filter(key -> requiredImages.get(key) != null)
                .map(key -> Pair.of(key, requiredImages.get(key)))
                .map(pair -> {
                    try (InputStream image = pair.getSecond().get()) {
                        return Optional.of(Pair.of(pair.getFirst(), image.readAllBytes()));
                    } catch (IOException e) {
                        VFXGenerator.LOGGER.error("Failed to open resource", e);
                        return Optional.<Pair<ResourceLocation, byte[]>>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
            buffer.writeInt(list.size());
            list.forEach(pair -> {
                buffer.writeResourceLocation(pair.getFirst());
                buffer.writeByteArray(pair.getSecond());
            });
            while (buffer.readableBytes() > Util.MAX_PAYLOAD_SIZE - 6) {
                byte[] data = new byte[Util.MAX_PAYLOAD_SIZE - 6];
                buffer.readBytes(data);
                PacketDistributor
                    .sendToPlayer((ServerPlayer) context.player(), new MissingImagesDataPayload(false, data));
            }
            byte[] data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);
            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new MissingImagesDataPayload(true, data));
            PacketDistributor.sendToPlayer(
                (ServerPlayer) context.player(), new UpdateRequiredImagesPayload(DataManager.requiredImages().keySet())
            );
        }
    }
}
