package platinpython.vfxgenerator.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import dev.lukebemish.codecextras.RootSchema;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import platinpython.vfxgenerator.util.datafix.DataFixUtils;
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
        // See https://github.com/Mojang/DataFixerUpper/issues/45
        schema.registerType(true, () -> "recursive_type_because_dfu_is_stupid", DSL::emptyPart);
        schema.registerType(
            false, TypeReferences.PARTICLE_DATA,
            // spotless:off
            () -> DataFixUtils.fields(
                DataFixUtils.whyDFUWhy("enabled", DSL.constType(DSL.bool())),
                DataFixUtils.whyDFUWhy("selected", DSL.constType(NamespacedSchema.namespacedString())),
                DataFixUtils.whyDFUWhy("useHSB", DSL.constType(DSL.bool())),
                DataFixUtils.whyDFUWhy("RGBColorBot", DSL.constType(DSL.intType())),
                DataFixUtils.whyDFUWhy("RGBColorTop", DSL.constType(DSL.intType())),
                DataFixUtils.whyDFUWhy("hueBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("saturationBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("brightnessBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("hueTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("saturationTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("brightnessTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("lifetimeBot", DSL.constType(DSL.intType())),
                DataFixUtils.whyDFUWhy("lifetimeTop", DSL.constType(DSL.intType())),
                DataFixUtils.whyDFUWhy("sizeBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("sizeTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("spawnXBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("spawnXTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("spawnYBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("spawnYTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("spawnZBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("spawnZTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("motionXBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("motionXTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("motionYBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("motionYTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("motionZBot", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("motionZTop", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("delay", DSL.constType(DSL.intType())),
                DataFixUtils.whyDFUWhy("gravity", DSL.constType(DSL.floatType())),
                DataFixUtils.whyDFUWhy("collision", DSL.constType(DSL.bool()))
            )
            // spotless:on
        );
    }
}
