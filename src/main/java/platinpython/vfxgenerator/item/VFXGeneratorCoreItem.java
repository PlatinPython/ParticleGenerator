package platinpython.vfxgenerator.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;
import platinpython.vfxgenerator.util.network.packets.VFXGeneratorDestroyParticlesPayload;

public class VFXGeneratorCoreItem extends Item {
    public VFXGeneratorCoreItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        // noinspection resource
        if (!itemEntity.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntity(
                itemEntity, new VFXGeneratorDestroyParticlesPayload(itemEntity.position())
            );
        }
    }
}
