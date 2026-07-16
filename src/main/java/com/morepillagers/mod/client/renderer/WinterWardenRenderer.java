package com.morepillagers.mod.client.renderer;

import com.morepillagers.mod.client.model.WinterWardenModel;
import com.morepillagers.mod.entity.WinterWardenEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WinterWardenRenderer extends GeoEntityRenderer<WinterWardenEntity> {
    public WinterWardenRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WinterWardenModel());
    }
}
