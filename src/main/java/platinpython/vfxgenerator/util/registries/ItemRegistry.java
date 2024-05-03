package platinpython.vfxgenerator.util.registries;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import platinpython.vfxgenerator.item.VFXGeneratorCoreItem;
import platinpython.vfxgenerator.util.RegistryHandler;

public class ItemRegistry {
    public static final DeferredItem<VFXGeneratorCoreItem> VFX_GENERATOR_CORE = RegistryHandler.ITEMS
        .register("vfx_generator_core", () -> new VFXGeneratorCoreItem(new Properties().rarity(Rarity.RARE)));

    public static void register() {}
}
