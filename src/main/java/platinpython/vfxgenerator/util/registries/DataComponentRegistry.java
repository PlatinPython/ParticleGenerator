package platinpython.vfxgenerator.util.registries;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.vfxgenerator.util.RegistryHandler;

public class DataComponentRegistry {
    @SuppressWarnings("deprecation")
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>> PARTICLE_DATA =
        RegistryHandler.DATA_COMPONENTS.registerComponentType(
            "particle_data",
            builder -> builder.persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC)
        );

    public static void register() {}
}
