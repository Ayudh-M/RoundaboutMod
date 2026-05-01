package net.hydra.jojomod.client.models.stand.renderers;

import net.hydra.jojomod.Roundabout;
import net.hydra.jojomod.client.models.layers.ModEntityRendererClient;
import net.hydra.jojomod.client.models.stand.WeatherReportModel;
import net.hydra.jojomod.entity.stand.WeatherReportEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.Nullable;

public class WeatherReportRenderer extends StandRenderer<WeatherReportEntity> {
    private static final ResourceLocation DEFAULT_SKIN =
            new ResourceLocation(Roundabout.MOD_ID, "textures/stand/weather_report/anime.png");

    public WeatherReportRenderer(EntityRendererProvider.Context context) {
        super(context, new WeatherReportModel<>(context.bakeLayer(ModEntityRendererClient.WEATHER_REPORT_LAYER)), 0f);
    }

    @Override
    public ResourceLocation getTextureLocation(WeatherReportEntity entity) {
        return DEFAULT_SKIN;
    }

    @Override
    public void render(WeatherReportEntity mobEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        float factor = 0.5F + (mobEntity.getSizePercent() / 2);
        if (mobEntity.isBaby()) {
            matrixStack.scale(0.5f * factor, 0.5f * factor, 0.5f * factor);
        } else {
            matrixStack.scale(0.87f * factor, 0.87f * factor, 0.87f * factor);
        }
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(WeatherReportEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return super.getRenderType(entity, showBody, true, showOutline);
    }
}