package platinpython.vfxgenerator.util.registries;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.vfxgenerator.util.RegistryHandler;

public class DataComponentRegistry {
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> PARTICLE_DATA =
        RegistryHandler.DATA_COMPONENTS.registerComponentType(
            "particle_data",
            builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
        );

    public static void register() {}
}
