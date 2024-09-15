package platinpython.vfxgenerator.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import dev.lukebemish.codecextras.RootSchema;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import platinpython.vfxgenerator.util.datafix.TypeReferences;

import java.util.Map;
import java.util.function.Supplier;

public class V0 extends RootSchema {
    public V0(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public void registerTypes(
        Schema schema,
        Map<String, Supplier<TypeTemplate>> entityTypes,
        Map<String, Supplier<TypeTemplate>> blockEntityTypes
    ) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        schema.registerType(true, () -> "recursive_because_dfu_is_stupid", DSL::emptyPart);
        schema.registerType(false, TypeReferences.SELECTED, () -> DSL.constType(NamespacedSchema.namespacedString()));
        schema.registerType(
            false, TypeReferences.PARTICLE_DATA, () -> DSL.fields("selected", TypeReferences.SELECTED.in(schema))
        );
    }
}
