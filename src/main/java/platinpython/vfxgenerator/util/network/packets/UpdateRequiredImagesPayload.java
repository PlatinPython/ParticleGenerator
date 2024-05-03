package platinpython.vfxgenerator.util.network.packets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.resources.DataManager;
import platinpython.vfxgenerator.util.resources.client.CacheHandler;
import platinpython.vfxgenerator.util.resources.client.VirtualPack;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;

public record UpdateRequiredImagesPayload(ImmutableSet<ResourceLocation> set) implements CustomPacketPayload {
    public static final Type<UpdateRequiredImagesPayload> TYPE =
        new Type<>(Util.createNamespacedResourceLocation("update_required_images"));
    public static final StreamCodec<ByteBuf, UpdateRequiredImagesPayload> STREAM_CODEC =
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new))
            .map(ImmutableSet::copyOf, HashSet::new)
            .map(UpdateRequiredImagesPayload::new, UpdateRequiredImagesPayload::set);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<UpdateRequiredImagesPayload> {
        public void handle(UpdateRequiredImagesPayload message, IPayloadContext context) {
            // noinspection OptionalGetWithoutIsPresent
            DataManager.setRequiredImages(
                message.set.stream()
                    .map(resourceLocation -> Pair.of(resourceLocation, CacheHandler.getIoSupplier(resourceLocation)))
                    .filter(pair -> {
                        Optional<IoSupplier<InputStream>> second = pair.getSecond();
                        if (second.isEmpty()) {
                            VFXGenerator.LOGGER
                                .warn("Missing texture {} in cache, check earlier log for errors.", pair.getFirst());
                        }
                        return second.isPresent();
                    })
                    .map(pair -> Pair.of(pair.getFirst(), pair.getSecond().get()))
                    .collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond))
            );
            VirtualPack.VIRTUAL_PACK.reload();
        }
    }
}
