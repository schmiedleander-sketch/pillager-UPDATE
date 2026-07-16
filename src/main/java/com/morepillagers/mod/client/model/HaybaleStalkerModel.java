package com.morepillagers.mod.client.model;

import com.morepillagers.mod.MorePillagers;
import com.morepillagers.mod.entity.HaybaleStalkerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HaybaleStalkerModel extends GeoModel<HaybaleStalkerEntity> {
    @Override
    public Identifier getModelResource(HaybaleStalkerEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "geo/haybale_stalker.geo.json");
    }

    @Override
    public Identifier getTextureResource(HaybaleStalkerEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "textures/entity/haybale_stalker.png");
    }

    @Override
    public Identifier getAnimationResource(HaybaleStalkerEntity animatable) {
        return new Identifier(MorePillagers.MOD_ID, "animations/haybale_stalker.animation.json");
    }
}
