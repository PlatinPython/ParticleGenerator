package platinpython.vfxgenerator.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jspecify.annotations.Nullable;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.block.VFXGeneratorBlock;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.client.gui.screen.ParticleOptionsScreen;
import platinpython.vfxgenerator.client.particle.VFXParticle;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.registries.BlockRegistry;
import platinpython.vfxgenerator.util.resources.client.VirtualPack;

@EventBusSubscriber(modid = VFXGenerator.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientUtils {
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(
            () -> ItemProperties.register(
                BlockRegistry.VFX_GENERATOR.get().asItem(),
                Util.createNamespacedResourceLocation(VFXGeneratorBlock.INVERTED_KEY), (stack, world, entity, seed) -> {
                    Boolean inverted = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY)
                        .get(VFXGeneratorBlock.INVERTED);
                    if (inverted == null) {
                        return 0F;
                    }
                    return inverted ? 1F : 0F;
                }
            )
        );
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        event.addRepositorySource(
            infoConsumer -> infoConsumer
                .accept(Pack.readMetaAndCreate(VirtualPack.LOCATION_INFO, new Pack.ResourcesSupplier() {
                    @Override
                    public PackResources openPrimary(PackLocationInfo pLocation) {
                        return VirtualPack.VIRTUAL_PACK;
                    }

                    @Override
                    public PackResources openFull(PackLocationInfo pLocation, Pack.Metadata pMetadata) {
                        return VirtualPack.VIRTUAL_PACK;
                    }
                }, PackType.CLIENT_RESOURCES, new PackSelectionConfig(true, Pack.Position.TOP, false)))
        );
    }

    public static void addParticle(
        @Nullable Level level,
        ParticleType particleType,
        int color,
        int lifetime,
        float size,
        Vec3 pos,
        Vec3 motion,
        float gravity,
        boolean collision,
        boolean fullBright
    ) {
        if (level == null) {
            level = Minecraft.getInstance().level;
        }
        if (!(level instanceof ClientLevel clientLevel)) {
            return;
        }
        VFXParticle particle = new VFXParticle(
            clientLevel, particleType, color, lifetime, size, pos, motion, gravity, collision, fullBright
        );
        Minecraft.getInstance().particleEngine.add(particle);
    }

    public static void openVFXGeneratorScreen(VFXGeneratorBlockEntity tileEntity) {
        Minecraft.getInstance().setScreen(new ParticleOptionsScreen(tileEntity));
    }

    public static MutableComponent getGuiTranslationTextComponent(String suffix) {
        return Component.translatable("gui." + VFXGenerator.MOD_ID + "." + suffix);
    }

    public static TextureAtlasSprite getTextureAtlasSprite(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().particleEngine.textureAtlas.getSprite(resourceLocation);
    }
}
