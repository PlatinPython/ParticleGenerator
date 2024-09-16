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
            false, TypeReferences.PARTICLE_DATA,
            // spotless:off
            () -> DSL.allWithRemainder(
                V0.field("enabled", DSL.constType(DSL.bool())),
                V0.field("selected", DSL.list(DSL.constType(NamespacedSchema.namespacedString()))),
                V0.field("useHSB", DSL.constType(DSL.bool())),
                DSL.and(
                    V0.field("RGBColorBot", DSL.constType(DSL.intType())),
                    V0.field("RGBColorTop", DSL.constType(DSL.intType()))
                ),
                DSL.and(
                    V0.field("hueBot", DSL.constType(DSL.floatType())),
                    V0.field("hueTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("saturationBot", DSL.constType(DSL.floatType())),
                    V0.field("saturationTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("brightnessBot", DSL.constType(DSL.floatType())),
                    V0.field("brightnessTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("lifetimeBot", DSL.constType(DSL.intType())),
                    V0.field("lifetimeTop", DSL.constType(DSL.intType()))
                ),
                DSL.and(
                    V0.field("sizeBot", DSL.constType(DSL.floatType())),
                    V0.field("sizeTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("spawnXBot", DSL.constType(DSL.floatType())),
                    V0.field("spawnXTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("spawnYBot", DSL.constType(DSL.floatType())),
                    V0.field("spawnYTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("spawnZBot", DSL.constType(DSL.floatType())),
                    V0.field("spawnZTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("motionXBot", DSL.constType(DSL.floatType())),
                    V0.field("motionXTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("motionYBot", DSL.constType(DSL.floatType())),
                    V0.field("motionYTop", DSL.constType(DSL.floatType()))
                ),
                DSL.and(
                    V0.field("motionZBot", DSL.constType(DSL.floatType())),
                    V0.field("motionZTop", DSL.constType(DSL.floatType()))
                ),
                V0.field("delay", DSL.constType(DSL.intType())),
                V0.field("gravity", DSL.constType(DSL.floatType())),
                V0.field("collision", DSL.constType(DSL.bool())),
                V0.field("fullbright", DSL.constType(DSL.bool()))
            )
            // spotless:on
        );
    }
}
