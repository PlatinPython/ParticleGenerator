package platinpython.vfxgenerator.util.data;

import com.mojang.serialization.Codec;
import dev.lukebemish.codecextras.mutable.DataElement;
import dev.lukebemish.codecextras.stream.mutable.StreamDataElementType;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class BoundedRangeStreamDataElementType<B, D, T extends Comparable<T>>
    implements StreamDataElementType<B, D, Range<T>> {
    private final String name;
    private final Codec<Range<T>> codec;
    private final StreamCodec<B, Range<T>> streamCodec;
    private final Function<D, DataElement<Range<T>>> getter;
    private final T min;
    private final T max;

    public BoundedRangeStreamDataElementType(
        String name,
        Codec<Range<T>> codec,
        StreamCodec<B, Range<T>> streamCodec,
        Function<D, DataElement<Range<T>>> getter,
        T min,
        T max
    ) {
        if (min.compareTo(max) < 0) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        this.name = name;
        this.codec = codec.xmap(range -> range.clamp(min, max), Function.identity());
        this.streamCodec = streamCodec.map(range -> range.clamp(min, max), Function.identity());
        this.getter = getter;
        this.min = min;
        this.max = max;
    }

    @Override
    public StreamCodec<B, Range<T>> streamCodec() {
        return this.streamCodec;
    }

    @Override
    public DataElement<Range<T>> from(D data) {
        return this.getter.apply(data);
    }

    @Override
    public Codec<Range<T>> codec() {
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
