package platinpython.vfxgenerator.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import platinpython.vfxgenerator.VFXGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;

public class Util {
    public static final int MAX_PAYLOAD_SIZE = 1024 * 1024;
    public static final HashFunction HASH_FUNCTION = Hashing.crc32c();

    public static ResourceLocation createNamespacedResourceLocation(String path) {
        return new ResourceLocation(VFXGenerator.MOD_ID, path);
    }

    public static <E> TreeSet<E> createTreeSetFromCollectionWithComparator(
        Collection<? extends E> collection,
        Comparator<? super E> comparator
    ) {
        TreeSet<E> set = new TreeSet<>(comparator);
        set.addAll(collection);
        return set;
    }

    public static <E> TreeSet<E> getThreeRandomElements(
        Collection<? extends E> collection,
        Comparator<? super E> comparator
    ) {
        if (collection.size() <= 3) {
            return createTreeSetFromCollectionWithComparator(collection, comparator);
        }
        ArrayList<? extends E> list = new ArrayList<>(collection);
        Collections.shuffle(list);
        TreeSet<E> set = new TreeSet<>(comparator);
        set.add(list.get(0));
        set.add(list.get(1));
        set.add(list.get(2));
        return set;
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

    @FunctionalInterface
    public interface BooleanSupplier {
        boolean get();
    }

    @FunctionalInterface
    public interface BooleanConsumer {
        void accept(boolean value);

        default BooleanConsumer andThen(BooleanConsumer after) {
            Objects.requireNonNull(after);
            return (boolean t) -> {
                accept(t);
                after.accept(t);
            };
        }
    }

    @FunctionalInterface
    public interface FloatSupplier {
        float get();
    }

    @FunctionalInterface
    public interface FloatConsumer {
        void accept(float value);

        default FloatConsumer andThen(FloatConsumer after) {
            Objects.requireNonNull(after);
            return (float t) -> {
                accept(t);
                after.accept(t);
            };
        }
    }
}
