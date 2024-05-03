package platinpython.vfxgenerator.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.util.network.packets.VFXGeneratorDestroyParticlesPayload;
import platinpython.vfxgenerator.util.registries.DataComponentRegistry;

public class VFXGeneratorBlockItem extends BlockItem {
    public VFXGeneratorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @SuppressWarnings("resource")
    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        if (!itemEntity.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntity(
                itemEntity, new VFXGeneratorDestroyParticlesPayload(itemEntity.position())
            );
        }
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(tag -> {
                if (tag.contains(VFXGeneratorBlockEntity.PARTICLE_DATA_KEY, Tag.TAG_COMPOUND)) {
                    stack.set(
                        DataComponentRegistry.PARTICLE_DATA, tag.getCompound(VFXGeneratorBlockEntity.PARTICLE_DATA_KEY)
                    );
                    tag.remove(VFXGeneratorBlockEntity.PARTICLE_DATA_KEY);
                }
            }));
        }
    }
}
