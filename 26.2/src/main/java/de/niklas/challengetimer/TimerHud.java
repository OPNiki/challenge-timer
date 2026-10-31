package de.niklas.challengetimer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class TimerHud {

    private TimerHud() {}

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !TimerState.isVisible()) return;

        Font font = mc.font;
        String text = TimerState.format(TimerState.getElapsedMs());

        int centerX = graphics.guiWidth() / 2;
        // unten: knapp über Hotbar + Statusleisten. oben: unter dem Bildschirmrand.
        int y = TimerState.isAtTop() ? 6 : graphics.guiHeight() - 75;

        if (TimerState.hasBackground()) {
            int width = font.width(text);
            graphics.fill(centerX - width / 2 - 4, y - 3, centerX + width / 2 + 4, y + 12, 0x66000000);
        }

        graphics.centeredText(font, text, centerX, y, TimerState.getColorArgb());

        if (!TimerState.isRunning()) {
            graphics.centeredText(font, "|| pausiert", centerX, y + 12, 0xFFFFAA00);
        }
    }
}