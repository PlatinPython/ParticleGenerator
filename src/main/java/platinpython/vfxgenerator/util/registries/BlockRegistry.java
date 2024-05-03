package platinpython.vfxgenerator.util.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import platinpython.vfxgenerator.block.VFXGeneratorBlock;
import platinpython.vfxgenerator.item.VFXGeneratorBlockItem;
import platinpython.vfxgenerator.util.RegistryHandler;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredBlock<VFXGeneratorBlock> VFX_GENERATOR =
        register("vfx_generator", VFXGeneratorBlock::new);

    public static void register() {}

    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block) {
        DeferredBlock<T> ret = registerNoItem(name, block);
        RegistryHandler.ITEMS
            .register(name, () -> new VFXGeneratorBlockItem(ret.get(), new Item.Properties().rarity(Rarity.RARE)));
        return ret;
    }

    private static <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<T> block) {
        return RegistryHandler.BLOCKS.register(name, block);
    }
}
