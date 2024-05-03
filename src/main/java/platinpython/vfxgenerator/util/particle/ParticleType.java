package platinpython.vfxgenerator.util.particle;

import com.mojang.serialization.Decoder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

public abstract class ParticleType {
    public static final Decoder<ParticleType> FILE_DECODER =
        ParticleTypes.CODEC.dispatch(ParticleType::type, ParticleTypes::fileDecoder);

    public static final StreamCodec<FriendlyByteBuf, ParticleType> STREAM_CODEC =
        ParticleTypes.STREAM_CODEC.dispatch(ParticleType::type, ParticleTypes::streamCodec);

    private final boolean supportsColor;

    protected ParticleType(boolean supportsColor) {
        this.supportsColor = supportsColor;
    }

    public boolean supportsColor() {
        return supportsColor;
    }

    public abstract ParticleTypes type();

    public abstract Stream<ResourceLocation> images();
}
