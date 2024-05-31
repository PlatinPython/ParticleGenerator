package platinpython.vfxgenerator.util.data;

import com.mojang.serialization.Codec;
import dev.lukebemish.codecextras.mutable.DataElement;
import dev.lukebemish.codecextras.stream.mutable.StreamDataElementType;
import net.minecraft.network.codec.StreamCodec;
import platinpython.vfxgenerator.util.Util;

import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class BoundedStreamDataElementType<B, D, T extends Comparable<T>> implements StreamDataElementType<B, D, T> {
    private final String name;
    private final Codec<T> codec;
    private final StreamCodec<B, T> streamCodec;
    private final Function<D, DataElement<T>> getter;
    private final T min;
    private final T max;

    public BoundedStreamDataElementType(
        String name,
        Codec<T> codec,
        StreamCodec<B, T> streamCodec,
        Function<D, DataElement<T>> getter,
        T min,
        T max
    ) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.name = name;
        this.codec = codec.xmap(value -> Util.clamp(value, min, max), Function.identity());
        this.streamCodec = streamCodec.map(value -> Util.clamp(value, min, max), Function.identity());
        this.getter = getter;
        this.min = min;
        this.max = max;
    }

    @Override
    public StreamCodec<B, T> streamCodec() {
        return this.streamCodec;
    }

    @Override
    public DataElement<T> from(D data) {
        return this.getter.apply(data);
    }

    @Override
    public Codec<T> codec() {
        return this.codec;
    }

    @Override
    public String name() {
        return this.name;
    }

    public T min() {
        return this.min;
    }

    public T max() {
        return this.max;
    }
}
