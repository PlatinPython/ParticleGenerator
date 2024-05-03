package platinpython.vfxgenerator.util.resources.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber(modid = VFXGenerator.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CacheHandler {
    private static final Path CACHE_DIRECTORY = FMLPaths.GAMEDIR.get().resolve(".cache").resolve(VFXGenerator.MOD_ID);
    private static final byte[] PNG_SIGNATURE = {
        (byte) 137, 80, 78, 71, 13, 10, 26, 10
    };
    private static final HashMap<ResourceLocation, HashCode> HASH_CACHE = new HashMap<>();

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        buildHashCache();
    }

    private static void buildHashCache() {
        HASH_CACHE.clear();
        if (Files.notExists(CACHE_DIRECTORY)) {
            return;
        }
        try (Stream<Path> subpaths = Files.walk(CACHE_DIRECTORY, 128)) {
            subpaths.forEach(path -> {
                if (Files.isDirectory(path) || !path.toString().endsWith(".png")) {
                    return;
                }
                Path relativePath = CACHE_DIRECTORY.relativize(path);
                if (relativePath.getNameCount() < 2) {
                    return;
                }
                String namespace = relativePath.getName(0).toString();
                StringBuilder stringBuilder = new StringBuilder();
                relativePath.subpath(1, relativePath.getNameCount()).forEach(pathComponent -> {
                    stringBuilder.append(pathComponent);
                    stringBuilder.append('/');
                });
                stringBuilder.setLength(stringBuilder.length() - 5);
                ResourceLocation resourceLocation = new ResourceLocation(namespace, stringBuilder.toString());
                try (InputStream file = Files.newInputStream(path)) {
                    byte[] signature = file.readNBytes(8);
                    if (!Arrays.equals(signature, PNG_SIGNATURE)) {
                        VFXGenerator.LOGGER.error("{} is not a valid png file.", path);
                        return;
                    }
                    byte[] bytes = file.readAllBytes();
                    // noinspection UnstableApiUsage
                    HashCode hash = Util.HASH_FUNCTION.newHasher().putBytes(signature).putBytes(bytes).hash();
                    HASH_CACHE.put(resourceLocation, hash);
                } catch (IOException e) {
                    VFXGenerator.LOGGER.error("Failed to hash file {}", path.getFileName(), e);
                }
            });
        } catch (IOException e) {
            VFXGenerator.LOGGER.error("Failed to access cache directory", e);
        }
    }

    public static ImmutableList<ResourceLocation> getMissingTextures(
        ImmutableMap<ResourceLocation, HashCode> requiredTextures
    ) {
        return requiredTextures.entrySet()
            .stream()
            .filter(
                entry -> !HASH_CACHE.containsKey(entry.getKey())
                    || !Objects.equals(HASH_CACHE.get(entry.getKey()), entry.getValue())
            )
            .map(Map.Entry::getKey)
            .collect(ImmutableList.toImmutableList());
    }

    public static void addToCache(ResourceLocation resourceLocation, byte[] image) {
        Path relativePath = Path.of(resourceLocation.getNamespace()).resolve(resourceLocation.getPath() + ".png");
        Path path = CACHE_DIRECTORY.resolve(relativePath);
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            VFXGenerator.LOGGER.error("Failed to create directory {}", path.getParent(), e);
            return;
        }
        try (OutputStream file = Files.newOutputStream(path)) {
            file.write(image);
            HASH_CACHE.put(resourceLocation, Util.HASH_FUNCTION.hashBytes(image));
        } catch (IOException e) {
            VFXGenerator.LOGGER.error("Failed to write image {}", path, e);
        }
    }

    public static Optional<IoSupplier<InputStream>> getIoSupplier(ResourceLocation resourceLocation) {
        if (!HASH_CACHE.containsKey(resourceLocation)) {
            return Optional.empty();
        }
        Path relativePath = Path.of(resourceLocation.getNamespace()).resolve(resourceLocation.getPath() + ".png");
        Path path = CACHE_DIRECTORY.resolve(relativePath);
        return Optional.of(IoSupplier.create(path));
    }
}
