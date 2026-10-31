package de.niklas.challengetimer;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public final class TimerCommands {

    private TimerCommands() {}

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommands.literal("timer")
                        .then(ClientCommands.literal("start").executes(ctx -> {
                            TimerState.start();
                            return feedback(ctx.getSource(), "§aTimer läuft.");
                        }))
                        .then(ClientCommands.literal("pause").executes(ctx -> {
                            TimerState.pause();
                            return feedback(ctx.getSource(), "§eTimer pausiert bei §f" + current());
                        }))
                        .then(ClientCommands.literal("resume").executes(ctx -> {
                            TimerState.start();
                            return feedback(ctx.getSource(), "§aTimer läuft weiter.");
                        }))
                        .then(ClientCommands.literal("reset").executes(ctx -> {
                            TimerState.reset();
                            return feedback(ctx.getSource(), "§cTimer auf 0 zurückgesetzt.");
                        }))
                        .then(ClientCommands.literal("set")
                                .then(ClientCommands.argument("sekunden", LongArgumentType.longArg(0))
                                        .executes(ctx -> {
                                            long sec = LongArgumentType.getLong(ctx, "sekunden");
                                            TimerState.setElapsedSeconds(sec);
                                            return feedback(ctx.getSource(), "§aTimer gesetzt auf §f" + current());
                                        })))
                        .then(ClientCommands.literal("color")
                                .then(ClientCommands.argument("hex", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String raw = StringArgumentType.getString(ctx, "hex")
                                                    .replace("#", "").trim();
                                            if (raw.length() != 6) {
                                                return feedback(ctx.getSource(),
                                                        "§cBitte 6 Hex-Stellen angeben, z. B. §f/timer color FF55FF");
                                            }
                                            try {
                                                TimerState.setColorRgb(Integer.parseInt(raw, 16));
                                                return feedback(ctx.getSource(),
                                                        "§aFarbe gesetzt auf §f#" + TimerState.getColorHex());
                                            } catch (NumberFormatException e) {
                                                return feedback(ctx.getSource(),
                                                        "§cKein gültiger Hexwert. Erlaubt sind 0-9 und A-F.");
                                            }
                                        })))
                        .then(ClientCommands.literal("bg")
                                .then(ClientCommands.literal("on").executes(ctx -> {
                                    TimerState.setBackground(true);
                                    return feedback(ctx.getSource(), "§aHintergrund an.");
                                }))
                                .then(ClientCommands.literal("off").executes(ctx -> {
                                    TimerState.setBackground(false);
                                    return feedback(ctx.getSource(), "§aHintergrund aus.");
                                })))
                        .then(ClientCommands.literal("hide").executes(ctx -> {
                            TimerState.setVisible(false);
                            return feedback(ctx.getSource(), "§7Timer ausgeblendet.");
                        }))
                        .then(ClientCommands.literal("show").executes(ctx -> {
                            TimerState.setVisible(true);
                            return feedback(ctx.getSource(), "§aTimer eingeblendet.");
                        }))
                        .then(ClientCommands.literal("pos")
                                .then(ClientCommands.literal("top").executes(ctx -> {
                                    TimerState.setAtTop(true);
                                    return feedback(ctx.getSource(), "§aTimer jetzt oben.");
                                }))
                                .then(ClientCommands.literal("bottom").executes(ctx -> {
                                    TimerState.setAtTop(false);
                                    return feedback(ctx.getSource(), "§aTimer jetzt über der Hotbar.");
                                })))
                        .executes(ctx -> feedback(ctx.getSource(),
                                "§bTimer: §f" + current() + (TimerState.isRunning() ? " §a(läuft)" : " §e(pausiert)")))
                )
        );
    }

    private static String current() {
        return TimerState.format(TimerState.getElapsedMs());
    }

    private static int feedback(FabricClientCommandSource source, String msg) {
        source.sendFeedback(Component.literal(msg));
        return 1;
    }
}