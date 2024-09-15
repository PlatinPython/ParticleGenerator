package platinpython.vfxgenerator.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import platinpython.vfxgenerator.util.datafix.TypeReferences;

import java.util.Map;
import java.util.function.Supplier;

public class V1 extends Schema {
    public V1(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public void registerTypes(
        Schema schema,
        Map<String, Supplier<TypeTemplate>> entityTypes,
        Map<String, Supplier<TypeTemplate>> blockEntityTypes
    ) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        schema.registerType(
            false, TypeReferences.SELECTED, () -> DSL.list(DSL.constType(NamespacedSchema.namespacedString()))
        );
    }
}
