package com.morepillagers.mod;

import com.morepillagers.mod.entity.AlchemistEntity;
import com.morepillagers.mod.entity.CrimsonDemonEntity;
import com.morepillagers.mod.entity.HaybaleStalkerEntity;
import com.morepillagers.mod.entity.WinterWardenEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MorePillagers implements ModInitializer {
    public static final String MOD_ID = "morepillagers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<CrimsonDemonEntity> CRIMSON_DEMON = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "crimson_demon"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, CrimsonDemonEntity::new)
                    .dimensions(EntityDimensions.fixed(1.2f, 2.8f))
                    .build()
    );

    public static final EntityType<WinterWardenEntity> WINTER_WARDEN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "winter_warden"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, WinterWardenEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2.0f))
                    .build()
    );

    public static final EntityType<AlchemistEntity> ALCHEMIST = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "alchemist"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AlchemistEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build()
    );

    public static final EntityType<HaybaleStalkerEntity> HAYBALE_STALKER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "haybale_stalker"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, HaybaleStalkerEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.5f))
                    .build()
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing More Pillagers Update Mod - Adding Custom Entities and Bosses!");

        // Register Entity Attributes
        FabricDefaultAttributeRegistry.register(CRIMSON_DEMON, CrimsonDemonEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(WINTER_WARDEN, WinterWardenEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ALCHEMIST, AlchemistEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(HAYBALE_STALKER, HaybaleStalkerEntity.createAttributes());
    }
}
