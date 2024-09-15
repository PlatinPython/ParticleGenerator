package platinpython.vfxgenerator.util.datafix;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import platinpython.vfxgenerator.util.datafix.fixes.ParticleDataPathFix;
import platinpython.vfxgenerator.util.datafix.fixes.ParticleDataToListFix;
import platinpython.vfxgenerator.util.datafix.schemas.V0;
import platinpython.vfxgenerator.util.datafix.schemas.V1;

public class DataFixers {
    public static final int CURRENT_VERSION = 2;

    private static final DataFixerBuilder.Result DATA_FIXER = createFixerUpper();

    private DataFixers() {}

    public static DataFixer getDataFixer() {
        return DATA_FIXER.fixer();
    }

    private static DataFixerBuilder.Result createFixerUpper() {
        DataFixerBuilder datafixerbuilder = new DataFixerBuilder(CURRENT_VERSION);
        addFixers(datafixerbuilder);
        return datafixerbuilder.build();
    }

    private static void addFixers(DataFixerBuilder builder) {
        builder.addSchema(0, V0::new);
        Schema schema = builder.addSchema(1, V1::new);
        builder.addFixer(new ParticleDataToListFix(schema, true));
        Schema schema1 = builder.addSchema(2, Schema::new);
        builder.addFixer(new ParticleDataPathFix(schema1, false));
    }
}
