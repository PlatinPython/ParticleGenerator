package platinpython.vfxgenerator.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.util.registries.BlockEntityRegistry;
import platinpython.vfxgenerator.util.registries.BlockRegistry;
import platinpython.vfxgenerator.util.registries.DataComponentRegistry;
import platinpython.vfxgenerator.util.registries.ItemRegistry;

public class RegistryHandler {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VFXGenerator.MOD_ID);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VFXGenerator.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VFXGenerator.MOD_ID);

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
        DeferredRegister.createDataComponents(VFXGenerator.MOD_ID);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        DATA_COMPONENTS.register(bus);

        BlockRegistry.register();
        ItemRegistry.register();
        BlockEntityRegistry.register();
        DataComponentRegistry.register();
    }
}
