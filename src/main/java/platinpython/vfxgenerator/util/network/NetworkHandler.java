package platinpython.vfxgenerator.util.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import platinpython.vfxgenerator.util.network.packets.MissingImagesDataPayload;
import platinpython.vfxgenerator.util.network.packets.MissingImagesPayload;
import platinpython.vfxgenerator.util.network.packets.RequiredImageHashesPayload;
import platinpython.vfxgenerator.util.network.packets.SelectableParticlesSyncPayload;
import platinpython.vfxgenerator.util.network.packets.UpdateRequiredImagesPayload;
import platinpython.vfxgenerator.util.network.packets.VFXGeneratorDataSyncPayload;
import platinpython.vfxgenerator.util.network.packets.VFXGeneratorDestroyParticlesPayload;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "2";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(
            VFXGeneratorDataSyncPayload.TYPE, VFXGeneratorDataSyncPayload.STREAM_CODEC,
            new VFXGeneratorDataSyncPayload.Handler()
        );
        registrar.playToClient(
            VFXGeneratorDestroyParticlesPayload.TYPE, VFXGeneratorDestroyParticlesPayload.STREAM_CODEC,
            new VFXGeneratorDestroyParticlesPayload.Handler()
        );
        // registrar.configurationToClient(
        registrar.playToClient(
            SelectableParticlesSyncPayload.TYPE, SelectableParticlesSyncPayload.STREAM_CODEC,
            new SelectableParticlesSyncPayload.Handler()
        );
        // registrar.configurationToClient(
        registrar.playToClient(
            RequiredImageHashesPayload.TYPE, RequiredImageHashesPayload.STREAM_CODEC,
            new RequiredImageHashesPayload.Handler()
        );
        // registrar.configurationToServer(
        registrar.playToServer(
            MissingImagesPayload.TYPE, MissingImagesPayload.STREAM_CODEC, new MissingImagesPayload.Handler()
        );
        // registrar.configurationToClient(
        registrar.playToClient(
            MissingImagesDataPayload.TYPE, MissingImagesDataPayload.STREAM_CODEC, new MissingImagesDataPayload.Handler()
        );
        // registrar.configurationToClient(
        registrar.playToClient(
            UpdateRequiredImagesPayload.TYPE, UpdateRequiredImagesPayload.STREAM_CODEC,
            new UpdateRequiredImagesPayload.Handler()
        );
    }
}
