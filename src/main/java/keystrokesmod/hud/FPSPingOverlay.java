package keystrokesmod.hud;

import keystrokesmod.event.render.Render2DEvent;
import keystrokesmod.eventbus.annotations.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.lwjgl.input.Keyboard;

public class FPSPingOverlay {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int x = 5, y = 5, line = 0;

    @EventListener
    public void onRender2D(Render2DEvent e) {
        if (mc.thePlayer == null || mc.fontRendererObj == null) return;

        line = 0;
        draw("NzkRaven", x, y + 10 * line++);
        draw("FPS: " + Minecraft.getDebugFPS(), x, y + 10 * line++);

        // Ping trong trận (đang vào server)
        try {
            NetworkPlayerInfo info = mc.getNetHandler() != null
                    ? mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID())
                    : null;
            if (info != null) {
                draw("Ping: " + info.getResponseTime() + " ms", x, y + 10 * line++);
            }
        } catch (Throwable ignored) {}

        // Keystrokes đơn giản: WASD + Space (nếu muốn hiển thị cơ bản)
        String keys = (Keyboard.isKeyDown(Keyboard.KEY_W) ? "W" : "_") +
                      (Keyboard.isKeyDown(Keyboard.KEY_A) ? "A" : "_") +
                      (Keyboard.isKeyDown(Keyboard.KEY_S) ? "S" : "_") +
                      (Keyboard.isKeyDown(Keyboard.KEY_D) ? "D" : "_");
        draw("Keys: " + keys + (Keyboard.isKeyDown(Keyboard.KEY_SPACE) ? " [⎵]" : ""), x, y + 10 * line++);
    }

    private void draw(String s, int x, int y) {
        mc.fontRendererObj.drawStringWithShadow(s, x, y, 0xFFFFFFFF);
    }
}
