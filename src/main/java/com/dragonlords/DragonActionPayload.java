package com.dragonlords;

import com.dragonlords.entity.DragonEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Client -> server: 0 = breath (held), 1 = charge. */
public record DragonActionPayload(int action) implements CustomPacketPayload {
    public static final Type<DragonActionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DragonLords.MOD_ID, "action"));
    public static final StreamCodec<io.netty.buffer.ByteBuf, DragonActionPayload> CODEC =
            ByteBufCodecs.VAR_INT.map(DragonActionPayload::new, DragonActionPayload::action);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(TYPE, CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, ctx) -> {
            ServerPlayer pl = ctx.player();
            if (pl.getVehicle() instanceof DragonEntity d && d.getControllingPassenger() == pl) d.performAction(payload.action());
        });
    }
}
