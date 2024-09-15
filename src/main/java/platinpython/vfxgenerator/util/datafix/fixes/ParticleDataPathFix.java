package platinpython.vfxgenerator.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import platinpython.vfxgenerator.util.datafix.TypeReferences;

import java.util.Optional;

public class ParticleDataPathFix extends DataFix {
    public ParticleDataPathFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.PARTICLE_DATA).findFieldType("selected");
        return this.writeFixAndRead(
            "ParticleDataPathFix", type, type,
            dynamic -> dynamic.createList(
                dynamic.asStream()
                    .map(Dynamic::asString)
                    .map(DataResult::result)
                    .flatMap(Optional::stream)
                    .map(s -> s.replace(":particle/", ":"))
                    .map(dynamic::createString)
            )
        );
    }
}
