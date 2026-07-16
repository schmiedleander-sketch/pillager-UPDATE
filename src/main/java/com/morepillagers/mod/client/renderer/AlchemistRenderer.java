package com.morepillagers.mod.client.renderer;

import com.morepillagers.mod.client.model.AlchemistModel;
import com.morepillagers.mod.entity.AlchemistEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AlchemistRenderer extends GeoEntityRenderer<AlchemistEntity> {
    public AlchemistRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AlchemistModel());
    }
}
