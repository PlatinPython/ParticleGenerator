package platinpython.vfxgenerator.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.util.registries.BlockRegistry;
import platinpython.vfxgenerator.util.registries.ItemRegistry;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, VFXGenerator.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(VFXGenerator.MOD_ID + ".pack_title", "VFX Generator Textures");
        add(VFXGenerator.MOD_ID + ".pack_description", "Textures to make VFX Generator work correctly");

        add(ItemRegistry.VFX_GENERATOR_CORE.get(), "VFX Generator Core");

        add(BlockRegistry.VFX_GENERATOR.get(), "VFX Generator");

        addGui("dataSaved", "Contents Saved.");

        addGui("selectTypes", "Select Types/Textures");

        addGui("particle", "Particle");

        addGui("enabled", "Enabled");
        addGui("disabled", "Disabled");

        addGui("rgb", "RGB");
        addGui("hsb", "HSB");

        addGui("red", "Red");
        addGui("green", "Green");
        addGui("blue", "Blue");

        addGui("hue", "Hue");
        addGui("saturation", "Saturation");
        addGui("brightness", "Brightness");

        addGui("spawnX", "X Position");
        addGui("spawnY", "Y Position");
        addGui("spawnZ", "Z Position");

        addGui("motionX", "X Motion");
        addGui("motionY", "Y Motion");
        addGui("motionZ", "Z Motion");

        addGui("lifetime", "Lifetime");

        addGui("size", "Size");

        addGui("delay", "Delay");

        addGui("gravity", "Gravity");

        addGui("collision", "Collision");

        addGui("fullbright", "Fullbright");

        addGui("ticks", "Ticks");

        addGui("areaBox", "Show working area");
    }

    private void addGui(String suffix, String text) {
        add("gui." + VFXGenerator.MOD_ID + "." + suffix, text);
    }
}
