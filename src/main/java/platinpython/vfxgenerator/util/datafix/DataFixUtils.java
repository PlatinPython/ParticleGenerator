package platinpython.vfxgenerator.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Tag;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;

import java.util.Arrays;
import java.util.stream.Stream;

public class DataFixUtils {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
    }

    // For some reason DFU needs named types for fields
    public static Pair<String, TypeTemplate> whyDFUWhy(String name, TypeTemplate type) {
        return Pair.of(name, DSL.named(name, type));
    }

    public static <T> Tag.TagType<T> findFieldType(String name, Type<?> parent) {
        return DSL.field(name, DataFixUtils.<Type<T>>cast(parent.findFieldType(name)));
    }

    @SafeVarargs
    public static TypeTemplate fields(Pair<String, TypeTemplate>... fields) {
        return DSL.and(
            Stream
                .concat(
                    Arrays.stream(fields).map(entry -> DSL.field(entry.getFirst(), entry.getSecond())),
                    Stream.of(DSL.remainder())
                )
                .toList()
        );
    }
}
