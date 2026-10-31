package de.niklas.challengetimer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class ChallengeTimerClient implements ClientModInitializer {
    public static final String MOD_ID = "challengetimer";

    @Override
    public void onInitializeClient() {
        TimerCommands.register();

        // HUD-Element direkt vor dem Chat einhängen; Fabric kümmert sich um das Z-Spacing.
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "timer"),
                TimerHud::render
        );

        // Timer läuft in Echtzeit, pausiert aber im Pausenmenü.
        ClientTickEvents.END_CLIENT_TICK.register(client -> TimerState.tick(client.isPaused()));

        // Pro Welt eigener Timer-Stand.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> TimerState.load(client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> TimerState.unload());
    }
}