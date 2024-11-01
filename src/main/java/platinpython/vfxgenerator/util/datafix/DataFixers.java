package platinpython.vfxgenerator.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import platinpython.vfxgenerator.util.datafix.fixes.ParticleDataCodecExtrasFix;
import platinpython.vfxgenerator.util.datafix.fixes.ParticleDataPathFix;
import platinpython.vfxgenerator.util.datafix.fixes.ParticleDataToListFix;
import platinpython.vfxgenerator.util.datafix.schemas.V0;
import platinpython.vfxgenerator.util.datafix.schemas.V1;
import platinpython.vfxgenerator.util.datafix.schemas.V3;

import java.util.function.ToIntFunction;

public class DataFixers {
    public static final String DATA_VERSION_FIELD = "data_version";
    public static final int CURRENT_VERSION = 3;

    private static final DataFixerBuilder.Result DATA_FIXER = createFixerUpper();

    private DataFixers() {}

    public static DataFixer getDataFixer() {
        return DATA_FIXER.fixer();
    }

    public static <A> Codec<A> wrapCodec(
        Codec<A> codec,
        DSL.TypeReference type,
        ToIntFunction<Dynamic<?>> dataVersionFunction
    ) {
        return new Codec<>() {
            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return codec.encode(input, ops, prefix)
                    .flatMap(
                        map -> ops.mergeToMap(map, ops.createString(DATA_VERSION_FIELD), ops.createInt(CURRENT_VERSION))
                    );
            }

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                Dynamic<T> original = new Dynamic<>(ops, input);
                int originalVersion = original.get(DATA_VERSION_FIELD)
                    .flatMap(Dynamic::asNumber)
                    .map(Number::intValue)
                    .result()
                    .orElse(dataVersionFunction.applyAsInt(original));
                Dynamic<T> updated = DataFixers.getDataFixer()
                    .update(type, original, originalVersion, CURRENT_VERSION)
                    .remove(DATA_VERSION_FIELD);
                return codec.decode(updated);
            }
        };
    }

    private static DataFixerBuilder.Result createFixerUpper() {
        DataFixerBuilder datafixerbuilder = new DataFixerBuilder(CURRENT_VERSION);
        addFixers(datafixerbuilder);
        return datafixerbuilder.build();
    }

    private static void addFixers(DataFixerBuilder builder) {
        builder.addSchema(0, V0::new);
        Schema schema = builder.addSchema(1, V1::new);
        builder.addFixer(new ParticleDataToListFix(schema, true));
        Schema schema1 = builder.addSchema(2, Schema::new);
        builder.addFixer(new ParticleDataPathFix(schema1, false));
        Schema schema2 = builder.addSchema(3, V3::new);
        builder.addFixer(new ParticleDataCodecExtrasFix(schema2, true));
    }
}
