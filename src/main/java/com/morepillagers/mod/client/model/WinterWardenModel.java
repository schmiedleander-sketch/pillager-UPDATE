package com.morepillagers.mod.client.model;

import com.morepillagers.mod.MorePillagers;
import com.morepillagers.mod.entity.WinterWardenEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WinterWardenModel extends GeoModel<WinterWardenEntity> {
    @Override
    public Identifier getModelResource(WinterWardenEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "geo/winter_warden.geo.json");
    }

    @Override
    public Identifier getTextureResource(WinterWardenEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "textures/entity/winter_warden.png");
    }

    @Override
    public Identifier getAnimationResource(WinterWardenEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "animations/winter_warden.animation.json");
    }
}
