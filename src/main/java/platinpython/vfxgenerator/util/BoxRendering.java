package platinpython.vfxgenerator.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.util.data.ParticleData;
import platinpython.vfxgenerator.util.data.Range;

@EventBusSubscriber(modid = VFXGenerator.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class BoxRendering {
    @Nullable
    public static BlockPos currentRenderPos;

    @SuppressWarnings("UnstableApiUsage")
    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        if (currentRenderPos == null) {
            return;
        }
        // noinspection DataFlowIssue
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(currentRenderPos);
        if (!(blockEntity instanceof VFXGeneratorBlockEntity vfxGeneratorBlockEntity)) {
            return;
        }
        ParticleData particleData = vfxGeneratorBlockEntity.getParticleData();
        Range<Float> spawnX = particleData.spawnX.get();
        Range<Float> spawnY = particleData.spawnY.get();
        Range<Float> spawnZ = particleData.spawnZ.get();

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        poseStack.pushPose();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        Matrix4f matrix = poseStack.last().pose();

        Vector3f pos = Vec3.atCenterOf(currentRenderPos).toVector3f();

        VertexConsumer builder = buffer.getBuffer(BoxRenderType.TRANSLUCENT_NO_CULL);
        renderBoxSides(
            builder, matrix, pos.x() + spawnX.start(), pos.y() + spawnY.start(), pos.z() + spawnZ.start(),
            pos.x() + spawnX.end(), pos.y() + spawnY.end(), pos.z() + spawnZ.end()
        );
        buffer.endBatch(BoxRenderType.TRANSLUCENT_NO_CULL);

        builder = buffer.getBuffer(BoxRenderType.LINES);
        renderBoxEdges(
            builder, matrix, pos.x() + spawnX.start(), pos.y() + spawnY.start(), pos.z() + spawnZ.start(),
            pos.x() + spawnX.end(), pos.y() + spawnY.end(), pos.z() + spawnZ.end()
        );
        buffer.endBatch(BoxRenderType.LINES);

        builder = buffer.getBuffer(BoxRenderType.LINES_LIGHTMAP);
        renderBoxEdgesFullbright(builder, matrix, pos);
        buffer.endBatch(BoxRenderType.LINES_LIGHTMAP);

        poseStack.popPose();
    }

    private static void renderBoxSides(
        VertexConsumer builder,
        Matrix4f matrix,
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ
    ) {
        int red = 0;
        int green = 128;
        int blue = 128;
        int alpha = 128;

        // Top side
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);

        // Bottom side
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // North side
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

        // East side
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        // South side
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // West side
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
    }

    private static void renderBoxEdges(
        VertexConsumer builder,
        Matrix4f matrix,
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ
    ) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 255;

        // West side
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);

        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);

        // East side
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);

        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        builder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);

        builder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        builder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);

        builder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
    }

    private static void renderBoxEdgesFullbright(VertexConsumer builder, Matrix4f matrix, Vector3f center) {
        float minX = center.x() - 0.5001F;
        float minY = center.y() - 0.5001F;
        float minZ = center.z() - 0.5001F;
        float maxX = center.x() + 0.5001F;
        float maxY = center.y() + 0.5001F;
        float maxZ = center.z() + 0.5001F;

        int red = 255;
        int green = 0;
        int blue = 0;
        int alpha = 255;

        // West side
        builder.addVertex(matrix, minX, minY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, minX, minY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        builder.addVertex(matrix, minX, minY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, minX, maxY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        builder.addVertex(matrix, minX, maxY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, minX, maxY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        builder.addVertex(matrix, minX, maxY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, minX, minY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        // East side
        builder.addVertex(matrix, maxX, minY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, maxX, minY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        builder.addVertex(matrix, maxX, minY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, maxX, maxY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        builder.addVertex(matrix, maxX, maxY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, maxX, maxY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        builder.addVertex(matrix, maxX, maxY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, maxX, minY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        builder.addVertex(matrix, maxX, minY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, minX, minY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        builder.addVertex(matrix, minX, maxY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, maxX, maxY, minZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        builder.addVertex(matrix, minX, minY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, maxX, minY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);

        builder.addVertex(matrix, maxX, maxY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
        builder.addVertex(matrix, minX, maxY, maxZ)
            .setColor(red, green, blue, alpha)
            .setLight(LightTexture.FULL_BRIGHT);
    }

    private static class BoxRenderType extends RenderType {
        public BoxRenderType(
            String name,
            VertexFormat format,
            VertexFormat.Mode mode,
            int bufferSize,
            boolean affectsCrumbling,
            boolean sortOnUpload,
            Runnable setupState,
            Runnable clearState
        ) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }

        public static final RenderType TRANSLUCENT_NO_CULL = RenderType.create(
            Util.createNamespacedResourceLocation("translucent_no_cull").toString(), DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 256, false, true,
            RenderType.CompositeState.builder()
                .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                .setLayeringState(LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setTextureState(RenderStateShard.NO_TEXTURE)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .createCompositeState(false)
        );
        public static final RenderType LINES = RenderType.create(
            Util.createNamespacedResourceLocation("lines").toString(), DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.DEBUG_LINES, 256, false, false,
            RenderType.CompositeState.builder()
                .setOutputState(RenderStateShard.MAIN_TARGET)
                .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                .setLineState(LineStateShard.DEFAULT_LINE)
                .setLayeringState(LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
                .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                .setTextureState(RenderStateShard.NO_TEXTURE)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setCullState(RenderStateShard.CULL)
                .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .createCompositeState(false)
        );
        public static final RenderType LINES_LIGHTMAP = RenderType.create(
            Util.createNamespacedResourceLocation("lines_lightmap").toString(),
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.DEBUG_LINES, 256, false, false,
            RenderType.CompositeState.builder()
                .setOutputState(RenderStateShard.MAIN_TARGET)
                .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                .setLineState(LineStateShard.DEFAULT_LINE)
                .setLayeringState(LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
                .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                .setTextureState(RenderStateShard.NO_TEXTURE)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setCullState(RenderStateShard.CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .createCompositeState(false)
        );
    }
}
