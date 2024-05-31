package platinpython.vfxgenerator.util;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import dev.lukebemish.codecextras.mutable.DataElementType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import platinpython.vfxgenerator.VFXGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class Util {
    public static final int MAX_PAYLOAD_SIZE = 1024 * 1024;
    public static final HashFunction HASH_FUNCTION = Hashing.crc32c();

    public static ResourceLocation createNamespacedResourceLocation(String path) {
        return new ResourceLocation(VFXGenerator.MOD_ID, path);
    }

    public static <E> ImmutableSortedSet<E> getThreeRandomElements(Collection<E> collection, Comparator<E> comparator) {
        if (collection.size() <= 3) {
            return ImmutableSortedSet.orderedBy(comparator).addAll(collection).build();
        }
        ArrayList<E> list = new ArrayList<>(collection);
        Collections.shuffle(list);
        return ImmutableSortedSet.orderedBy(comparator).add(list.get(0)).add(list.get(1)).add(list.get(2)).build();
    }

    public static <E> E randomElement(List<? extends E> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public static double toValue(double value, double minValue, double maxValue, float stepSize) {
        return clamp(Mth.lerp(Mth.clamp(value, 0.0D, 1.0D), minValue, maxValue), minValue, maxValue, stepSize);
    }

    public static double clamp(double value, double minValue, double maxValue, float stepSize) {
        if (stepSize > 0.0F) {
            value = (stepSize * Math.round(value / stepSize));
        }

        return map(Mth.clamp(value, minValue, maxValue), minValue, maxValue, 0D, 1D);
    }

    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    public static <T extends Comparable<T>> T clamp(T value, T min, T max) {
        return min(max(value, min), max);
    }

    public static <T extends Comparable<T>> T min(T a, T b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    public static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    @SuppressWarnings("UnstableApiUsage")
    @SafeVarargs
    public static <D> Predicate<D> anyDirty(DataElementType<D, ?>... types) {
        List<DataElementType<D, ?>> list = List.of(types);
        return anyDirty(list);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <D> Predicate<D> anyDirty(List<? extends DataElementType<D, ?>> types) {
        return data -> {
            for (var type : types) {
                if (type.from(data).dirty()) {
                    return true;
                }
            }
            return false;
        };
    }
}
