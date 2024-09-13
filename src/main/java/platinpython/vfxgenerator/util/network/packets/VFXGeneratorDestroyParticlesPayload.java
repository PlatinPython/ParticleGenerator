package platinpython.vfxgenerator.util.network.packets;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import platinpython.vfxgenerator.util.ClientUtils;
import platinpython.vfxgenerator.util.Color;
import platinpython.vfxgenerator.util.Util;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.particle.types.SingleParticle;

import java.util.concurrent.ThreadLocalRandom;

public record VFXGeneratorDestroyParticlesPayload(Vec3 pos) implements CustomPacketPayload {
    public static final Type<VFXGeneratorDestroyParticlesPayload> TYPE =
        new Type<>(Util.createNamespacedResourceLocation("vfx_generator_destroy_particles"));
    public static final StreamCodec<ByteBuf, VFXGeneratorDestroyParticlesPayload> STREAM_CODEC =
        ByteBufCodecs.VECTOR3F.map(Vec3::new, Vec3::toVector3f)
            .map(VFXGeneratorDestroyParticlesPayload::new, VFXGeneratorDestroyParticlesPayload::pos);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler implements IPayloadHandler<VFXGeneratorDestroyParticlesPayload> {
        private static final ImmutableList<ParticleType> LIST = ImmutableList.of(
            new SingleParticle(Util.createNamespacedResourceLocation("spark_small"), true),
            new SingleParticle(Util.createNamespacedResourceLocation("spark_mid"), true),
            new SingleParticle(Util.createNamespacedResourceLocation("spark_big"), true)
        );

        public void handle(VFXGeneratorDestroyParticlesPayload message, IPayloadContext context) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < 100; i++) {
                double motionX = random.nextFloat(-.1F, Math.nextUp(.1F));
                double motionY = random.nextFloat(-.1F, Math.nextUp(.1F));
                double motionZ = random.nextFloat(-.1F, Math.nextUp(.1F));
                Vec3 motion = new Vec3(motionX, motionY, motionZ);
                ClientUtils.addParticle(
                    null, LIST.get(random.nextInt(LIST.size())),
                    Color.HSBtoRGB(random.nextFloat(0F, Math.nextUp(1F)), 1F, 1F), random.nextInt(5, 16), .3F,
                    message.pos, motion, 0F, false, false
                );
            }
        }
    }
}
