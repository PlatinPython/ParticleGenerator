package platinpython.vfxgenerator.util.network.packets;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.resources.client.CacheHandler;

import java.util.HashMap;

public record RequiredImageHashesPayload(ImmutableMap<ResourceLocation, HashCode> map) implements CustomPacketPayload {
    public static final Type<RequiredImageHashesPayload> TYPE =
        new Type<>(Util.createNamespacedResourceLocation("required_image_hashes"));
    public static final StreamCodec<ByteBuf, RequiredImageHashesPayload> STREAM_CODEC =
        ByteBufCodecs
            .map(
                HashMap::new, ResourceLocation.STREAM_CODEC,
                ByteBufCodecs.BYTE_ARRAY.map(HashCode::fromBytes, HashCode::asBytes)
            )
            .map(ImmutableMap::copyOf, HashMap::new)
            .map(RequiredImageHashesPayload::new, RequiredImageHashesPayload::map);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<RequiredImageHashesPayload> {
        public void handle(RequiredImageHashesPayload message, IPayloadContext context) {
            PacketDistributor.sendToServer(new MissingImagesPayload(CacheHandler.getMissingTextures(message.map)));
        }
    }
}
