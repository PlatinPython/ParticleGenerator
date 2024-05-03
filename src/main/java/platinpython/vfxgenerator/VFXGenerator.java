package platinpython.vfxgenerator;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import platinpython.vfxgenerator.data.DataGatherer;
import platinpython.vfxgenerator.util.RegistryHandler;
import platinpython.vfxgenerator.util.network.NetworkHandler;
import platinpython.vfxgenerator.util.registries.BlockRegistry;
import platinpython.vfxgenerator.util.registries.ItemRegistry;

@Mod(VFXGenerator.MOD_ID)
public class VFXGenerator {
    public static final String MOD_ID = "vfxgenerator";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public VFXGenerator(IEventBus bus) {
        bus.addListener(DataGatherer::onGatherData);
        bus.addListener(VFXGenerator::addItemsToTab);
        bus.addListener(NetworkHandler::register);

        RegistryHandler.register(bus);
    }

    public static void addItemsToTab(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
            return;
        }
        event.accept(BlockRegistry.VFX_GENERATOR);
        event.accept(ItemRegistry.VFX_GENERATOR_CORE);
    }
}
