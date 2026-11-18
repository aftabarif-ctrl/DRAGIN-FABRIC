package com.dragonlords;

import net.fabricmc.api.ModInitializer;

public class DragonLords implements ModInitializer {
    public static final String MOD_ID = "dragonlords";

    @Override
    public void onInitialize() {
        ModEntities.register();
        DragonActionPayload.register();
    }
}
