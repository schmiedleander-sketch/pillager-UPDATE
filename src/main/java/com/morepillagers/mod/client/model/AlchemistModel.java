package com.morepillagers.mod.client.model;

import com.morepillagers.mod.MorePillagers;
import com.morepillagers.mod.entity.AlchemistEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AlchemistModel extends GeoModel<AlchemistEntity> {
    @Override
    public Identifier getModelResource(AlchemistEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "geo/alchemist.geo.json");
    }

    @Override
    public Identifier getTextureResource(AlchemistEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "textures/entity/alchemist.png");
    }

    @Override
    public Identifier getAnimationResource(AlchemistEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "animations/alchemist.animation.json");
    }
}
