package com.morepillagers.mod.client.renderer;

import com.morepillagers.mod.client.model.CrimsonDemonModel;
import com.morepillagers.mod.entity.CrimsonDemonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrimsonDemonRenderer extends GeoEntityRenderer<CrimsonDemonEntity> {
    public CrimsonDemonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CrimsonDemonModel());
    }
}
