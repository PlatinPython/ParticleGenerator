package platinpython.vfxgenerator.util.datafix;

import com.mojang.datafixers.DSL;
import net.minecraft.util.datafix.fixes.References;

public class TypeReferences {
    public static final DSL.TypeReference PARTICLE_DATA = References.reference("particle_data");
    public static final DSL.TypeReference SELECTED = References.reference("selected");
}
