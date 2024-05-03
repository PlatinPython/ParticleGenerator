package platinpython.vfxgenerator.util.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.resources.client.CacheHandler;

import java.util.ArrayList;

public record MissingImagesDataPayload(boolean last, byte[] data) implements CustomPacketPayload {
    public static final Type<MissingImagesDataPayload> TYPE =
        new Type<>(Util.createNamespacedResourceLocation("missing_images_data"));
    public static final StreamCodec<ByteBuf, MissingImagesDataPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, MissingImagesDataPayload::last, ByteBufCodecs.BYTE_ARRAY, MissingImagesDataPayload::data,
        MissingImagesDataPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<MissingImagesDataPayload> {
        private final ArrayList<MissingImagesDataPayload> MESSAGES = new ArrayList<>();

        public void handle(MissingImagesDataPayload message, IPayloadContext context) {
            MESSAGES.add(message);
            if (!message.last) {
                return;
            }
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            MESSAGES.forEach(packet -> buffer.writeBytes(packet.data));
            int numberOfElements = buffer.readInt();
            for (int i = 0; i < numberOfElements; i++) {
                CacheHandler.addToCache(buffer.readResourceLocation(), buffer.readByteArray());
            }
            MESSAGES.clear();
        }
    }
}
