package com.dragonlords.client;

import com.dragonlords.DragonActionPayload;
import com.dragonlords.DragonType;
import com.dragonlords.ModEntities;
import com.dragonlords.entity.DragonEntity;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class DragonLordsClient implements ClientModInitializer {
    public static final KeyMapping BREATH = new KeyMapping("key.dragonlords.breath", InputConstants.KEY_R, "key.categories.dragonlords");
    public static final KeyMapping CHARGE = new KeyMapping("key.dragonlords.charge", InputConstants.KEY_V, "key.categories.dragonlords");
    private static int ticks;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(BREATH);
        KeyBindingHelper.registerKeyBinding(CHARGE);
        EntityModelLayerRegistry.registerModelLayer(DragonModel.LAYER, DragonModel::createBodyLayer);
        for (DragonType t : DragonType.values())
            EntityRendererRegistry.register(ModEntities.get(t), ctx -> new DragonRenderer(ctx, t));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null || !(mc.player.getVehicle() instanceof DragonEntity)) return;
            ticks++;
            if (BREATH.isDown() && ticks % 4 == 0) ClientPlayNetworking.send(new DragonActionPayload(0));
            while (CHARGE.consumeClick()) ClientPlayNetworking.send(new DragonActionPayload(1));
        });
    }
}
