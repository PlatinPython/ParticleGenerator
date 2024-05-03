package platinpython.vfxgenerator.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.block.VFXGeneratorBlock;
import platinpython.vfxgenerator.util.registries.BlockRegistry;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VFXGenerator.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(BlockRegistry.VFX_GENERATOR.get()).forAllStates(
            state -> ConfiguredModel.builder().modelFile(this.getVFXGeneratorModelForState(state)).build()
        );

        itemModels()
            .withExistingParent(
                BlockRegistry.VFX_GENERATOR.getId().getPath(), modLoc(ModelProvider.BLOCK_FOLDER + "/vfx_generator_off")
            )
            .override()
            .predicate(new ResourceLocation(VFXGenerator.MOD_ID, "inverted"), 1F)
            .model(models().getExistingFile(modLoc(ModelProvider.BLOCK_FOLDER + "/vfx_generator_off_inverted")))
            .end();
    }

    private ModelFile getVFXGeneratorModelForState(BlockState state) {
        return models()
            .getBuilder(
                modLoc(
                    ModelProvider.BLOCK_FOLDER + "/vfx_generator_"
                        + (state.getValue(VFXGeneratorBlock.POWERED) ? "on" : "off")
                        + (state.getValue(VFXGeneratorBlock.INVERTED) ? "_inverted" : "")
                ).toString()
            )
            .parent(
                models().getExistingFile(
                    modLoc(
                        ModelProvider.BLOCK_FOLDER + "/vfx_generator_base"
                            + (state.getValue(VFXGeneratorBlock.POWERED) ? "_on" : "")
                    )
                )
            )
            .texture("frame", mcLoc("block/smooth_stone"))
            .texture("glass", modLoc("block/vfx_generator_glass"))
            .texture(
                "redstone", modLoc("block/redstone_rod_" + (state.getValue(VFXGeneratorBlock.POWERED) ? "on" : "off"))
            )
            .texture(
                "rod",
                modLoc("block/vfx_generator_rod" + (state.getValue(VFXGeneratorBlock.INVERTED) ? "_inverted" : ""))
            );
    }
}
