package platinpython.vfxgenerator.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import platinpython.vfxgenerator.util.datafix.DataFixUtils;
import platinpython.vfxgenerator.util.datafix.TypeReferences;

import java.util.List;

public class ParticleDataToListFix extends DataFix {
    public ParticleDataToListFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> inputType = this.getInputSchema().getType(TypeReferences.PARTICLE_DATA);
        Type<?> outputType = this.getOutputSchema().getType(TypeReferences.PARTICLE_DATA);
        OpticFinder<Pair<String, String>> selectedFinder = DataFixUtils.cast(inputType.findField("selected"));
        Type<Pair<String, List<String>>> selectedType = DataFixUtils.cast(outputType.findFieldType("selected"));
        Type<Pair<String, Boolean>> otherType = DataFixUtils.findFieldType("fullbright", outputType);
        return this.fixTypeEverywhereTyped("ParticleDataToListFix", inputType, outputType, typed -> {
            OpticFinder<Pair<Pair<String, Boolean>, Dynamic<?>>> finder =
                DSL.typeFinder(DSL.and(DataFixUtils.findFieldType("collision", inputType), DSL.remainderType()));
            Type<Pair<Pair<String, Boolean>, Pair<Pair<String, Boolean>, Dynamic<?>>>> type =
                DSL.and(DataFixUtils.findFieldType("collision", outputType), otherType, DSL.remainderType());
            return typed.update(selectedFinder, selectedType, pair -> pair.mapSecond(List::of))
                .update(
                    finder, type, pair -> pair.mapSecond(dynamic -> Pair.of(Pair.of("fullbright", false), dynamic))
                );
        });
    }
}
