package platinpython.vfxgenerator.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import platinpython.vfxgenerator.util.datafix.DataFixUtils;
import platinpython.vfxgenerator.util.datafix.TypeReferences;

public class ParticleDataCodecExtrasFix extends DataFix {
    public ParticleDataCodecExtrasFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> inputType = this.getInputSchema().getType(TypeReferences.PARTICLE_DATA);
        Type<?> outputType = this.getOutputSchema().getType(TypeReferences.PARTICLE_DATA);
        return this.fixTypeEverywhereTyped("ParticleDataCodecExtrasFix", inputType, outputType, typed -> {
            typed = makeOptional(typed, inputType, outputType, "enabled");
            typed = renameField(typed, inputType, outputType, "selected", "all_selected");
            typed = renameField(typed, inputType, outputType, "useHSB", "use_hsb");
            typed = fieldsToRange(typed, inputType, outputType, "RGBColorBot", "RGBColorTop", "rgb_color");
            typed = fieldsToRange(typed, inputType, outputType, "hueBot", "hueTop", "hue");
            typed = fieldsToRange(typed, inputType, outputType, "saturationBot", "saturationTop", "saturation");
            typed = fieldsToRange(typed, inputType, outputType, "brightnessBot", "brightnessTop", "brightness");
            typed = fieldsToRange(typed, inputType, outputType, "lifetimeBot", "lifetimeTop", "lifetime");
            typed = fieldsToRange(typed, inputType, outputType, "sizeBot", "sizeTop", "size");
            typed = fieldsToRange(typed, inputType, outputType, "spawnXBot", "spawnXTop", "spawn_x");
            typed = fieldsToRange(typed, inputType, outputType, "spawnYBot", "spawnYTop", "spawn_y");
            typed = fieldsToRange(typed, inputType, outputType, "spawnZBot", "spawnZTop", "spawn_z");
            typed = fieldsToRange(typed, inputType, outputType, "motionXBot", "motionXTop", "motion_x");
            typed = fieldsToRange(typed, inputType, outputType, "motionYBot", "motionYTop", "motion_y");
            typed = fieldsToRange(typed, inputType, outputType, "motionZBot", "motionZTop", "motion_z");
            typed = makeOptional(typed, inputType, outputType, "delay");
            typed = makeOptional(typed, inputType, outputType, "gravity");
            typed = makeOptional(typed, inputType, outputType, "collision");
            typed = renameField(typed, inputType, outputType, "fullbright", "full_bright");
            return typed;
        });
    }

    private <T> Typed<?> makeOptional(Typed<?> typed, Type<?> inputType, Type<?> outputType, String name) {
        return typed
            .update(
                DSL.typeFinder(DataFixUtils.<Pair<String, T>>findFieldType(name, inputType)),
                DSL.optional(DataFixUtils.findFieldType(name, outputType)), Either::left
            )
            .update(DSL.remainderFinder(), dynamic -> dynamic.remove(name));
    }

    private <T> Typed<?> renameField(
        Typed<?> typed,
        Type<?> inputType,
        Type<?> outputType,
        String oldName,
        String newName
    ) {
        return typed
            .update(
                DSL.typeFinder(DataFixUtils.<Pair<String, T>>findFieldType(oldName, inputType)),
                DSL.optional(DataFixUtils.findFieldType(newName, outputType)),
                pair -> Either.left(pair.mapFirst(s -> newName))
            )
            .update(DSL.remainderFinder(), dynamic -> dynamic.remove(oldName));
    }

    private Typed<?> fieldsToRange(
        Typed<?> typed,
        Type<?> inputType,
        Type<?> outputType,
        String oldStart,
        String oldEnd,
        String range
    ) {
        OpticFinder<Pair<Pair<String, ? extends Number>, Pair<String, ? extends Number>>> finder = DSL.typeFinder(
            DSL.and(DataFixUtils.findFieldType(oldStart, inputType), DataFixUtils.findFieldType(oldEnd, inputType))
        );
        Type<Either<Pair<String, Pair<String, Pair<Dynamic<?>, Dynamic<?>>>>, Unit>> type =
            DSL.optional(DataFixUtils.findFieldType(range, outputType));
        return typed
            .update(
                finder, type,
                pair -> Either.left(
                    Pair.of(
                        range,
                        Pair.of(
                            "range",
                            Pair.of(
                                new Dynamic<>(
                                    JsonOps.INSTANCE, JsonOps.INSTANCE.createNumeric(pair.getFirst().getSecond())
                                ).convert(typed.getOps()),
                                new Dynamic<>(
                                    JsonOps.INSTANCE, JsonOps.INSTANCE.createNumeric(pair.getSecond().getSecond())
                                ).convert(typed.getOps())
                            )
                        )
                    )
                )
            )
            .update(DSL.remainderFinder(), dynamic -> dynamic.remove(oldStart).remove(oldEnd));
    }
}
