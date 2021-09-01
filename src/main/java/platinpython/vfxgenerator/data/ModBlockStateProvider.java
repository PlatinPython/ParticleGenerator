package platinpython.vfxgenerator.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.block.VFXGeneratorBlock;
import platinpython.vfxgenerator.util.registries.BlockRegistry;

public class ModBlockStateProvider extends BlockStateProvider {
	public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, VFXGenerator.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		getVariantBuilder(BlockRegistry.VFX_GENERATOR.get()).forAllStates((state) -> {
			return ConfiguredModel.builder().modelFile(models().getBuilder(modLoc(ModelProvider.BLOCK_FOLDER + "/vfx_generator_" + (state.getValue(VFXGeneratorBlock.POWERED) ? "on" : "off") + (state.getValue(VFXGeneratorBlock.INVERTED) ? "_inverted" : "")).toString()).parent(models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/vfx_generator"))).texture("frame", mcLoc("block/smooth_stone")).texture("glass", modLoc("block/vfx_generator_glass")).texture("redstone", modLoc("block/redstone_rod_" + (state.getValue(VFXGeneratorBlock.POWERED) ? "on" : "off"))).texture("rod", modLoc("block/vfx_generator_rod" + (state.getValue(VFXGeneratorBlock.INVERTED) ? "_inverted" : "")))).build();
		});

		itemModels().withExistingParent(BlockRegistry.VFX_GENERATOR.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/vfx_generator_off")).override().predicate(new ResourceLocation(VFXGenerator.MOD_ID, "inverted"), 1F).model(models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/vfx_generator_off_inverted"))).end();
	}
}
