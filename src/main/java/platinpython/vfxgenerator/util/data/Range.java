package platinpython.vfxgenerator.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import platinpython.vfxgenerator.util.Util;

public record Range<T extends Comparable<T>>(T start, T end) {
    public Range(T start, T end) {
        if (start.compareTo(end) < 0) {
            this.start = start;
            this.end = end;
        } else {
            this.start = end;
            this.end = start;
        }
    }

    public Range<T> with(T start, T end) {
        if (this.start.compareTo(start) != 0 || this.end.compareTo(end) != 0) {
            return new Range<>(start, end);
        } else {
            return this;
        }
    }

    public Range<T> withStart(T start) {
        return new Range<>(start, this.end);
    }

    public Range<T> withEnd(T end) {
        return new Range<>(this.start, end);
    }

    public Range<T> clamp(T min, T max) {
        T newStart = Util.clamp(this.start, min, max);
        T newEnd = Util.clamp(this.end, min, max);
        if (newStart.compareTo(this.start) != 0 && newEnd.compareTo(this.end) != 0) {
            return new Range<>(newStart, newEnd);
        }
        return this;
    }

    public static <T extends Comparable<T>> Codec<Range<T>> getCodec(Codec<T> baseCodec) {
        return RecordCodecBuilder.create(
            instance -> instance
                .group(
                    baseCodec.fieldOf("start").forGetter(Range::start), baseCodec.fieldOf("end").forGetter(Range::end)
                )
                .apply(instance, Range::new)
        );
    }

    public static <T extends Comparable<T>> StreamCodec<? extends ByteBuf, Range<T>> getStreamCodec(
        StreamCodec<? extends ByteBuf, T> baseCodec
    ) {
        return StreamCodec.composite(baseCodec, Range::start, baseCodec, Range::end, Range::new);
    }
}
