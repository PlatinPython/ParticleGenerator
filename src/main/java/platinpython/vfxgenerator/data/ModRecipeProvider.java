package platinpython.vfxgenerator.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import platinpython.vfxgenerator.util.registries.BlockRegistry;
import platinpython.vfxgenerator.util.registries.ItemRegistry;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.VFX_GENERATOR_CORE.get())
            .define('E', Blocks.END_ROD)
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .define('G', Tags.Items.GLASS_BLOCKS)
            .pattern("ERE")
            .pattern("RGR")
            .pattern("ERE")
            .unlockedBy("has_end_rod", has(Blocks.END_ROD))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.VFX_GENERATOR.get())
            .define('C', ItemRegistry.VFX_GENERATOR_CORE.get())
            .define('S', Blocks.SMOOTH_STONE)
            .pattern(" S ")
            .pattern("SCS")
            .pattern(" S ")
            .unlockedBy("has_vfx_generator_core", has(ItemRegistry.VFX_GENERATOR_CORE.get()))
            .save(output);
    }
}
