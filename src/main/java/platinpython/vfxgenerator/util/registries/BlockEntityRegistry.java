package platinpython.vfxgenerator.util.registries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.util.RegistryHandler;

public class BlockEntityRegistry {
    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VFXGeneratorBlockEntity>> VFX_GENERATOR =
        RegistryHandler.BLOCK_ENTITY_TYPES.register(
            "vfx_generator",
            () -> BlockEntityType.Builder.of(VFXGeneratorBlockEntity::new, BlockRegistry.VFX_GENERATOR.get())
                .build(null)
        );

    public static void register() {}
}
