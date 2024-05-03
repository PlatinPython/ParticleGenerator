package platinpython.vfxgenerator.util.resources.client;

import com.google.common.collect.ImmutableSet;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.util.resources.DataManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class VirtualPack extends AbstractPackResources {
    public static final PackLocationInfo LOCATION_INFO = new PackLocationInfo(
        VFXGenerator.MOD_ID + "_virtual_pack", Component.translatable(VFXGenerator.MOD_ID + ".pack_title"),
        PackSource.BUILT_IN, Optional.empty()
    );
    public static final VirtualPack VIRTUAL_PACK = new VirtualPack();

    private static final FileToIdConverter FILE_TO_ID_CONVERTER = new FileToIdConverter("textures/particle", ".png");

    private final PackMetadataSection packInfo;
    private final Map<ResourceLocation, IoSupplier<InputStream>> resources = new HashMap<>();
    private final Set<String> namespaces = new HashSet<>();

    public VirtualPack() {
        super(LOCATION_INFO);
        this.packInfo = new PackMetadataSection(
            Component.translatable(VFXGenerator.MOD_ID + ".pack_description"),
            SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
        );
    }

    public void reload() {
        this.resources.clear();
        this.namespaces.clear();
        DataManager.requiredImages().forEach((resourceLocation, resource) -> {
            this.namespaces.add(resourceLocation.getNamespace());
            this.resources.put(FILE_TO_ID_CONVERTER.idToFile(resourceLocation), resource);
        });
        Minecraft.getInstance().reloadResourcePacks();
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        for (String name : elements) {
            if (!name.equals("pack.png")) {
                continue;
            }
            return IoSupplier
                .create(ModList.get().getModFileById(VFXGenerator.MOD_ID).getFile().findResource("logo.png"));
        }
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation resourceLocation) {
        if (packType != PackType.CLIENT_RESOURCES) {
            return null;
        }
        return this.resources.get(resourceLocation);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        this.resources.forEach((resourceLocation, supplier) -> {
            if (resourceLocation.getNamespace().equals(namespace) && resourceLocation.getPath().startsWith(path)) {
                resourceOutput.accept(resourceLocation, getResource(packType, resourceLocation));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return ImmutableSet.copyOf(namespaces);
    }

    @Override
    public void close() {}

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        return deserializer == PackMetadataSection.TYPE ? (T) this.packInfo : null;
    }
}
