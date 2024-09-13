package platinpython.vfxgenerator.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import platinpython.vfxgenerator.block.VFXGeneratorBlock;
import platinpython.vfxgenerator.util.RegistryHandler;
import platinpython.vfxgenerator.util.registries.BlockRegistry;
import platinpython.vfxgenerator.util.registries.DataComponentRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(
            output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)),
            lookupProvider
        );
    }

    public static class Blocks extends BlockLootSubProvider {
        protected Blocks(HolderLookup.Provider lookupProvider) {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate() {
            this.add(
                BlockRegistry.VFX_GENERATOR.get(),
                createSingleItemTable(BlockRegistry.VFX_GENERATOR.get())
                    .apply(CopyBlockState.copyState(BlockRegistry.VFX_GENERATOR.get()).copy(VFXGeneratorBlock.INVERTED))
                    .apply(
                        CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                            .include(DataComponentRegistry.PARTICLE_DATA.get())
                    )
            );
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RegistryHandler.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
        }
    }
}
