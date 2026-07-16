package com.morepillagers.mod.client.model;

import com.morepillagers.mod.MorePillagers;
import com.morepillagers.mod.entity.CrimsonDemonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CrimsonDemonModel extends GeoModel<CrimsonDemonEntity> {
    @Override
    public Identifier getModelResource(CrimsonDemonEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "geo/crimson_demon.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrimsonDemonEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "textures/entity/crimson_demon.png");
    }

    @Override
    public Identifier getAnimationResource(CrimsonDemonEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "animations/crimson_demon.animation.json");
    }
}
