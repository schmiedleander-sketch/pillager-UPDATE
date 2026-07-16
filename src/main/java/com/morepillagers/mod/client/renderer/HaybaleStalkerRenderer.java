package com.morepillagers.mod.client.renderer;

import com.morepillagers.mod.client.model.HaybaleStalkerModel;
import com.morepillagers.mod.entity.HaybaleStalkerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HaybaleStalkerRenderer extends GeoEntityRenderer<HaybaleStalkerEntity> {
    public HaybaleStalkerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HaybaleStalkerModel());
    }
}
