package platinpython.vfxgenerator.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import platinpython.vfxgenerator.util.datafix.DataFixUtils;
import platinpython.vfxgenerator.util.datafix.TypeReferences;

import java.util.List;

public class ParticleDataPathFix extends DataFix {
    public ParticleDataPathFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        OpticFinder<Pair<String, List<String>>> selected =
            DataFixUtils.cast(this.getInputSchema().getType(TypeReferences.PARTICLE_DATA).findField("selected"));
        return this.fixTypeEverywhereTyped(
            "ParticleDataPathFix", this.getInputSchema().getType(TypeReferences.PARTICLE_DATA),
            typed -> typed.update(
                selected,
                pair -> pair.mapSecond(
                    list -> list.stream().map(s -> s.replace("vfxgenerator:particle/", "vfxgenerator:")).toList()
                )
            )
        );
    }
}
