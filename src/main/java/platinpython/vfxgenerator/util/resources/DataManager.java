package platinpython.vfxgenerator.util.resources;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import platinpython.vfxgenerator.util.network.packets.SelectableParticlesSyncPayload;
import platinpython.vfxgenerator.util.network.packets.UpdateRequiredImagesPayload;
import platinpython.vfxgenerator.util.particle.ParticleType;
import platinpython.vfxgenerator.util.resources.server.ParticleListLoader;

import java.io.InputStream;

public class DataManager {
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private static ImmutableMap<ResourceLocation, ParticleType> SELECTABLE_PARTICLES = ImmutableMap.of();
    private static ImmutableMap<ResourceLocation, IoSupplier<InputStream>> REQUIRED_IMAGES = ImmutableMap.of();

    public static ImmutableMap<ResourceLocation, ParticleType> selectableParticles() {
        return SELECTABLE_PARTICLES;
    }

    public static void setSelectableParticles(ImmutableMap<ResourceLocation, ParticleType> selectableParticles) {
        if (STACK_WALKER.getCallerClass() != ParticleListLoader.class
            && STACK_WALKER.getCallerClass() != SelectableParticlesSyncPayload.Handler.class) {
            throw new IllegalCallerException("Illegal caller: " + STACK_WALKER.getCallerClass());
        }
        SELECTABLE_PARTICLES = selectableParticles;
    }

    public static ImmutableMap<ResourceLocation, IoSupplier<InputStream>> requiredImages() {
        return REQUIRED_IMAGES;
    }

    public static void setRequiredImages(ImmutableMap<ResourceLocation, IoSupplier<InputStream>> requiredImages) {
        if (STACK_WALKER.getCallerClass() != ParticleListLoader.class
            && STACK_WALKER.getCallerClass() != UpdateRequiredImagesPayload.Handler.class) {
            throw new IllegalCallerException("Illegal caller: " + STACK_WALKER.getCallerClass());
        }
        REQUIRED_IMAGES = requiredImages;
    }
}
