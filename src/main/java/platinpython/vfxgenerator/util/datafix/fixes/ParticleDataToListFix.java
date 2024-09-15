package platinpython.vfxgenerator.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import platinpython.vfxgenerator.util.datafix.TypeReferences;

import java.util.stream.Stream;

public class ParticleDataToListFix extends DataFix {
    public ParticleDataToListFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> oldType = this.getInputSchema().getType(TypeReferences.PARTICLE_DATA).findFieldType("selected");
        Type<?> newType = this.getOutputSchema().getType(TypeReferences.PARTICLE_DATA).findFieldType("selected");
        return this.writeFixAndRead(
            "ParticleDataToListFix", oldType, newType, dynamic -> dynamic.createList(Stream.of(dynamic))
        );
    }
}
