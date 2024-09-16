package platinpython.vfxgenerator.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import platinpython.vfxgenerator.util.datafix.DataFixUtils;
import platinpython.vfxgenerator.util.datafix.TypeReferences;

import java.util.Map;
import java.util.function.Supplier;

public class V3 extends Schema {
    public V3(int versionKey, Schema parent) {
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
            false, TypeReferences.RANGE,
            () -> DSL.and(DSL.field("start", DSL.remainder()), DSL.field("end", DSL.remainder()))
        );
        schema.registerType(
            false, TypeReferences.PARTICLE_DATA,
            // spotless:off
            () -> DSL.optionalFields(
                DataFixUtils.whyDFUWhy("enabled", DSL.constType(DSL.bool())),
                DataFixUtils.whyDFUWhy("all_selected", DSL.list(DSL.constType(NamespacedSchema.namespacedString()))),
                DataFixUtils.whyDFUWhy("use_hsb", DSL.constType(DSL.bool())),
                DataFixUtils.whyDFUWhy("rgb_color", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("hue", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("saturation", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("brightness", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("lifetime", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("size", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("spawn_x", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("spawn_y", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("spawn_z", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("motion_x", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("motion_y", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("motion_z", TypeReferences.RANGE.in(schema)),
                DataFixUtils.whyDFUWhy("delay", DSL.constType(DSL.intType())),
                DataFixUtils.whyDFUWhy("gravity", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("collision", DSL.constType(DSL.bool())),
                DataFixUtils.whyDFUWhy("full_bright", DSL.constType(DSL.bool()))
            )
            // spotless:on
        );
    }
}
