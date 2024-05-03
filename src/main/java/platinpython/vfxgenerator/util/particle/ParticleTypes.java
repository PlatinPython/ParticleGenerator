package platinpython.vfxgenerator.util.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import platinpython.vfxgenerator.util.particle.types.SingleParticle;

import java.util.Locale;
import java.util.function.Supplier;

public enum ParticleTypes implements StringRepresentable {
    SINGLE(() -> SingleParticle.FILE_DECODER, () -> SingleParticle.STREAM_CODEC);

    public static final Codec<ParticleTypes> CODEC = StringRepresentable.fromEnum(ParticleTypes::values);
    public static final StreamCodec<FriendlyByteBuf, ParticleTypes> STREAM_CODEC =
        NeoForgeStreamCodecs.enumCodec(ParticleTypes.class);

    private final Supplier<MapCodec<? extends ParticleType>> fileDecoder;
    private final Supplier<StreamCodec<? super FriendlyByteBuf, ? extends ParticleType>> streamCodec;

    ParticleTypes(
        Supplier<MapCodec<? extends ParticleType>> fileDecoder,
        Supplier<StreamCodec<? super FriendlyByteBuf, ? extends ParticleType>> streamCodec
    ) {
        this.fileDecoder = fileDecoder;
        this.streamCodec = streamCodec;
    }

    public MapCodec<? extends ParticleType> fileDecoder() {
        return this.fileDecoder.get();
    }

    public StreamCodec<? super FriendlyByteBuf, ? extends ParticleType> streamCodec() {
        return this.streamCodec.get();
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
