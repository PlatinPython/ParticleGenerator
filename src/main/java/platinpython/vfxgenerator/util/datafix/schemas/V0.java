package platinpython.vfxgenerator.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
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
            () -> DSL.allWithRemainder(
                field("enabled", DSL.constType(DSL.bool())),
                field("selected", DSL.constType(NamespacedSchema.namespacedString())),
                field("useHSB", DSL.constType(DSL.bool())),
                DSL.and(
                    field("RGBColorBot", DSL.constType(DSL.intType())),
                    field("RGBColorTop", DSL.constType(DSL.intType()))
                ),
                DSL.and(
                    field("hueBot", DSL.constType(DSL.floatType())),
                    field("hueTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("saturationBot", DSL.constType(DSL.floatType())),
                    field("saturationTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("brightnessBot", DSL.constType(DSL.floatType())),
                    field("brightnessTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("lifetimeBot", DSL.constType(DSL.intType())),
                    field("lifetimeTop", DSL.constType(DSL.intType()))
                ),
                DSL.and(
                    field("sizeBot", DSL.constType(DSL.floatType())),
                    field("sizeTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("spawnXBot", DSL.constType(DSL.floatType())),
                    field("spawnXTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("spawnYBot", DSL.constType(DSL.floatType())),
                    field("spawnYTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("spawnZBot", DSL.constType(DSL.floatType())),
                    field("spawnZTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("motionXBot", DSL.constType(DSL.floatType())),
                    field("motionXTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("motionYBot", DSL.constType(DSL.floatType())),
                    field("motionYTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    field("motionZBot", DSL.constType(DSL.floatType())),
                    field("motionZTop", DSL.constType(DSL.floatType()))
                ),
                field("delay", DSL.constType(DSL.intType())),
                field("gravity", DSL.constType(DSL.floatType())),
                field("collision", DSL.constType(DSL.bool()))
            )
            // spotless:on
        );
    }

    static TypeTemplate field(String name, TypeTemplate type) {
        Pair<String, TypeTemplate> pair = DataFixUtils.whyDFUWhy(name, type);
        return DSL.field(pair.getFirst(), pair.getSecond());
    }
}
