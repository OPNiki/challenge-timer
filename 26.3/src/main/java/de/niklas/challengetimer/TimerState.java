package de.niklas.challengetimer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Files;
import java.nio.file.Path;

/** Hält den Timer-Zustand und speichert ihn pro Welt. */
public final class TimerState {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Das, was tatsächlich in der JSON landet. */
    public static class Data {
        boolean running = false;
        long elapsedMs = 0L;
        boolean visible = true;
        boolean atTop = false;
        boolean background = false;
        int colorRgb = 0xFFFFFF;
    }

    private static Data data = new Data();
    private static Path file = null;
    private static long lastRealTimeMs = 0L;
    private static int saveCooldown = 0;

    private TimerState() {}

    // ---- Ticking ----------------------------------------------------------

    public static void tick(boolean gamePaused) {
        long now = System.currentTimeMillis();

        if (!data.running || gamePaused) {
            lastRealTimeMs = now;
        } else {
            if (lastRealTimeMs != 0L) {
                long delta = now - lastRealTimeMs;
                // Sprünge (Alt-Tab, Lag-Spike, Systemzeit) abfangen
                if (delta > 0 && delta < 5_000L) {
                    data.elapsedMs += delta;
                }
            }
            lastRealTimeMs = now;
        }

        // alle ~10 Sekunden sichern, damit ein Absturz nicht alles frisst
        if (++saveCooldown >= 200) {
            saveCooldown = 0;
            save();
        }
    }

    // ---- Steuerung --------------------------------------------------------

    public static void start() {
        data.running = true;
        lastRealTimeMs = System.currentTimeMillis();
        save();
    }

    public static void pause() {
        data.running = false;
        save();
    }

    public static void reset() {
        data.elapsedMs = 0L;
        lastRealTimeMs = System.currentTimeMillis();
        save();
    }

    public static void setElapsedSeconds(long seconds) {
        data.elapsedMs = Math.max(0L, seconds) * 1000L;
        lastRealTimeMs = System.currentTimeMillis();
        save();
    }

    public static void setVisible(boolean v)      { data.visible = v; save(); }
    public static void setAtTop(boolean top)      { data.atTop = top; save(); }
    public static void setBackground(boolean b)   { data.background = b; save(); }
    public static void setColorRgb(int rgb)       { data.colorRgb = rgb & 0xFFFFFF; save(); }

    public static boolean isRunning()      { return data.running; }
    public static boolean isVisible()      { return data.visible; }
    public static boolean isAtTop()        { return data.atTop; }
    public static boolean hasBackground()  { return data.background; }
    public static long getElapsedMs()      { return data.elapsedMs; }

    /** Alpha-Kanal draufsetzen — ohne den wäre der Text unsichtbar. */
    public static int getColorArgb() { return 0xFF000000 | (data.colorRgb & 0xFFFFFF); }

    /** Für die Chat-Ausgabe, z. B. "FF55FF". */
    public static String getColorHex() { return String.format("%06X", data.colorRgb & 0xFFFFFF); }

    // ---- Persistenz -------------------------------------------------------

    public static void load(Minecraft client) {
        data = new Data();
        lastRealTimeMs = System.currentTimeMillis();
        file = resolveFile(client);
        if (file == null || !Files.exists(file)) return;
        try {
            Data loaded = GSON.fromJson(Files.readString(file), Data.class);
            if (loaded != null) data = loaded;
        } catch (Exception e) {
            System.err.println("[ChallengeTimer] Konnte Timer nicht laden: " + e);
        }
    }

    public static void unload() {
        save();
        file = null;
    }

    public static void save() {
        if (file == null) return;
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(data));
        } catch (Exception e) {
            System.err.println("[ChallengeTimer] Konnte Timer nicht speichern: " + e);
        }
    }

    /** Speichert im Weltordner, damit der Stand mit der Welt mitwandert. */
    private static Path resolveFile(Minecraft client) {
        try {
            IntegratedServer server = client.getSingleplayerServer();
            if (server != null) {
                return server.getWorldPath(LevelResource.ROOT).resolve("challenge-timer.json");
            }
        } catch (Throwable ignored) {
            // Fallback unten
        }
        return FabricLoader.getInstance().getConfigDir()
                .resolve("challenge-timer").resolve("fallback.json");
    }

    // ---- Formatierung -----------------------------------------------------

    /** Ergibt z. B. "4d 22h 29m 55s". */
    public static String format(long ms) {
        long total = ms / 1000L;
        long d = total / 86400L;
        long h = (total % 86400L) / 3600L;
        long m = (total % 3600L) / 60L;
        long s = total % 60L;

        if (d > 0) return d + "d " + h + "h " + m + "m " + s + "s";
        if (h > 0) return h + "h " + m + "m " + s + "s";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }
}