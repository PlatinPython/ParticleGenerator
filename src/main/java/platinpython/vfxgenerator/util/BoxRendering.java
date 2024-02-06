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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import platinpython.vfxgenerator.VFXGenerator;
import platinpython.vfxgenerator.block.entity.VFXGeneratorBlockEntity;
import platinpython.vfxgenerator.util.data.ParticleData;

@Mod.EventBusSubscriber(modid = VFXGenerator.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BoxRendering {
    public static BlockPos currentRenderPos;

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        if (currentRenderPos == null) {
            return;
        }
        //noinspection DataFlowIssue
        BlockEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(currentRenderPos);
        if (!(tileEntity instanceof VFXGeneratorBlockEntity)) {
            return;
        }
        ParticleData particleData = ((VFXGeneratorBlockEntity) tileEntity).getParticleData();

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        poseStack.pushPose();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        Matrix4f matrix = poseStack.last().pose();

        Vector3f pos = Vec3.atCenterOf(currentRenderPos).toVector3f();

        VertexConsumer builder = buffer.getBuffer(BoxRenderType.TRANSLUCENT_NO_CULL);
        renderBoxSides(
            builder, matrix, pos.x() + particleData.getSpawnXBot(), pos.y() + particleData.getSpawnYBot(),
            pos.z() + particleData.getSpawnZBot(), pos.x() + particleData.getSpawnXTop(),
            pos.y() + particleData.getSpawnYTop(), pos.z() + particleData.getSpawnZTop()
        );
        buffer.endBatch(BoxRenderType.TRANSLUCENT_NO_CULL);

        builder = buffer.getBuffer(BoxRenderType.LINES);
        renderBoxEdges(
            builder, matrix, pos.x() + particleData.getSpawnXBot(), pos.y() + particleData.getSpawnYBot(),
            pos.z() + particleData.getSpawnZBot(), pos.x() + particleData.getSpawnXTop(),
            pos.y() + particleData.getSpawnYTop(), pos.z() + particleData.getSpawnZTop()
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
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();

        // Bottom side
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        // North side
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        // East side
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        // South side
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        // West side
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
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
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();

        // East side
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        builder.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).endVertex();

        builder.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        builder.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        builder.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
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
        builder.vertex(matrix, minX, minY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, minX, minY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        builder.vertex(matrix, minX, minY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, minX, maxY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        builder.vertex(matrix, minX, maxY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, minX, maxY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        builder.vertex(matrix, minX, maxY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, minX, minY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        // East side
        builder.vertex(matrix, maxX, minY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, maxX, minY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        builder.vertex(matrix, maxX, minY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, maxX, maxY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        builder.vertex(matrix, maxX, maxY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, maxX, maxY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        builder.vertex(matrix, maxX, maxY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, maxX, minY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        builder.vertex(matrix, maxX, minY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, minX, minY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        builder.vertex(matrix, minX, maxY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, maxX, maxY, minZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        builder.vertex(matrix, minX, minY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, maxX, minY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();

        builder.vertex(matrix, maxX, maxY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
        builder.vertex(matrix, minX, maxY, maxZ)
            .color(red, green, blue, alpha)
            .uv2(LightTexture.pack(15, 15))
            .endVertex();
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
            VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
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
            VertexFormat.Mode.DEBUG_LINES, 256, false, false, RenderType.CompositeState.builder()
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
        public static RenderType LINES_LIGHTMAP = RenderType.create(
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