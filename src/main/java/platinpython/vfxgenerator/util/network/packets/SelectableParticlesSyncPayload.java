package platinpython.vfxgenerator.util.network.packets;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.util.HashMap;

public record SelectableParticlesSyncPayload(ImmutableMap<ResourceLocation, ParticleType> map)
    implements CustomPacketPayload {
    public static final Type<SelectableParticlesSyncPayload> TYPE =
        new Type<>(Util.createNamespacedResourceLocation("selectable_particles_sync"));
    public static final StreamCodec<FriendlyByteBuf, SelectableParticlesSyncPayload> STREAM_CODEC =
        ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ParticleType.STREAM_CODEC)
            .map(ImmutableMap::copyOf, HashMap::new)
            .map(SelectableParticlesSyncPayload::new, SelectableParticlesSyncPayload::map);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<SelectableParticlesSyncPayload> {
        public void handle(SelectableParticlesSyncPayload message, IPayloadContext context) {
            DataManager.setSelectableParticles(message.map);
        }
    }
}
