package com.morepillagers.mod.client;

import com.morepillagers.mod.MorePillagers;
import com.morepillagers.mod.client.renderer.AlchemistRenderer;
import com.morepillagers.mod.client.renderer.CrimsonDemonRenderer;
import com.morepillagers.mod.client.renderer.HaybaleStalkerRenderer;
import com.morepillagers.mod.client.renderer.WinterWardenRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MorePillagersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register GeckoLib Entity Renderers
        EntityRendererRegistry.register(MorePillagers.CRIMSON_DEMON, CrimsonDemonRenderer::new);
        EntityRendererRegistry.register(MorePillagers.WINTER_WARDEN, WinterWardenRenderer::new);
        EntityRendererRegistry.register(MorePillagers.ALCHEMIST, AlchemistRenderer::new);
        EntityRendererRegistry.register(MorePillagers.HAYBALE_STALKER, HaybaleStalkerRenderer::new);
    }
}
