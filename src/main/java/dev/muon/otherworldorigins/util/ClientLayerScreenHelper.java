package dev.muon.otherworldorigins.util;

import dev.muon.otherworldorigins.OtherworldOrigins;
import io.github.apace100.origins.screen.ChooseOriginScreen;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientLayerScreenHelper {
    private static int validationAttempts = 0;
    private static final int MAX_VALIDATION_ATTEMPTS = 20;

    @OnlyIn(Dist.CLIENT)
    public static void handleValidatedLayers(List<ResourceLocation> validMissingLayers) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            validationAttempts++;

            if (validationAttempts >= MAX_VALIDATION_ATTEMPTS) {
                resetValidationAttempts();
                minecraft.execute(() -> {
                    minecraft.player.connection.getConnection().disconnect(
                            Component.translatable("otherworldorigins.disconnect.validation_failed")
                    );
                });
                return;
            }

            Registry<OriginLayer> layerRegistry = OriginsAPI.getLayersRegistry(null);
            List<Holder<OriginLayer>> missingOriginLayers = new ArrayList<>();

            for (ResourceLocation layerId : validMissingLayers) {
                OriginLayer layer = layerRegistry.get(layerId);
                if (layer != null) {
                    missingOriginLayers.add(layerRegistry.getHolderOrThrow(layerRegistry.getResourceKey(layer).orElseThrow()));
                }
            }

            if (!missingOriginLayers.isEmpty()) {
                OtherworldOrigins.LOGGER.info("Reopening selection screen for validated layers:");
                for (Holder<OriginLayer> layerHolder : missingOriginLayers) {
                    OtherworldOrigins.LOGGER.info("- " + layerHolder.value().name().getString());
                }
                minecraft.execute(() -> {
                    ChooseOriginScreen newScreen = new ChooseOriginScreen(missingOriginLayers, 0, false);
                    minecraft.setScreen(newScreen);
                });
            }
        }
    }
    public static void resetValidationAttempts() {
        validationAttempts = 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleFeatLayers(List<ResourceLocation> validLayerIds) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            Registry<OriginLayer> layerRegistry = OriginsAPI.getLayersRegistry(null);
            List<Holder<OriginLayer>> featOriginLayers = new ArrayList<>();

            for (ResourceLocation layerId : validLayerIds) {
                OriginLayer layer = layerRegistry.get(layerId);
                if (layer != null) {
                    featOriginLayers.add(layerRegistry.getHolderOrThrow(layerRegistry.getResourceKey(layer).orElseThrow()));
                }
            }

            if (!featOriginLayers.isEmpty()) {
                OtherworldOrigins.LOGGER.debug("Opening selection screen for feat layers:");
                for (Holder<OriginLayer> layerHolder : featOriginLayers) {
                    OtherworldOrigins.LOGGER.debug("- " + layerHolder.value().name().getString());
                }
                minecraft.execute(() -> {
                    ChooseOriginScreen newScreen = new ChooseOriginScreen(featOriginLayers, 0, false);
                    minecraft.setScreen(newScreen);
                });
            }
        }
    }
}