package platinpython.vfxgenerator.util.particle.types;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.particle.ParticleTypes;
import platinpython.vfxgenerator.util.resources.ResourceCodec;
import platinpython.vfxgenerator.util.resources.ResourceUtil;

import java.util.stream.Stream;

public class SingleParticle extends ParticleType {
    public static final MapCodec<SingleParticle> FILE_DECODER = RecordCodecBuilder.mapCodec(
        instance -> instance
            .group(
                ResourceLocation.CODEC.fieldOf("value").forGetter(SingleParticle::value),
                new ResourceCodec()
                    .flatXmap(ResourceUtil::supportsColor, i -> DataResult.error(() -> "Serializing not supported"))
                    .fieldOf("value")
                    .forGetter(i -> null)
            )
            .apply(instance, SingleParticle::new)
    );

    public static final StreamCodec<ByteBuf, SingleParticle> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, SingleParticle::value, ByteBufCodecs.BOOL, SingleParticle::supportsColor,
        SingleParticle::new
    );

    private final ResourceLocation value;

    public SingleParticle(ResourceLocation value, boolean supportsColor) {
        super(supportsColor);
        this.value = value;
    }

    public ResourceLocation value() {
        return this.value;
    }

    @Override
    public ParticleTypes type() {
        return ParticleTypes.SINGLE;
    }

    @Override
    public Stream<ResourceLocation> images() {
        return Stream.of(this.value());
    }
}
