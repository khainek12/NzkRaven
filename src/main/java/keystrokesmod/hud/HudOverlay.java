package keystrokesmod.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class HudOverlay {
    private final Minecraft mc = Minecraft.getMinecraft();

    // keystrokes + cps sẽ lấy từ lớp khác
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text e) {
        if (mc.thePlayer == null || mc.fontRendererObj == null) return;

        // Vẽ text gọn ở cột trái debug (ít đụng code sẵn có)
        e.left.add("NzkRaven");                                   // watermark
        e.left.add("FPS: " + Minecraft.getDebugFPS());            // FPS

        // Ping (chỉ có khi vào server)
        try {
            if (mc.getCurrentServerData() != null) {
                e.left.add("Ping: " + mc.getCurrentServerData().pingToServer + " ms");
            }
        } catch (Throwable ignored) {}

        // Keystrokes đơn giản: WASD + Space
        String keys = (Keyboard.isKeyDown(Keyboard.KEY_W) ? "W" : "_") +
                      (Keyboard.isKeyDown(Keyboard.KEY_A) ? "A" : "_") +
                      (Keyboard.isKeyDown(Keyboard.KEY_S) ? "S" : "_") +
                      (Keyboard.isKeyDown(Keyboard.KEY_D) ? "D" : "_");
        e.left.add("Keys: " + keys + (Keyboard.isKeyDown(Keyboard.KEY_SPACE) ? " [⎵]" : ""));

        // CPS (lấy từ CpsCounter)
        e.left.add("CPS L/R: " + CpsCounter.getLeftCps() + " / " + CpsCounter.getRightCps());
    }
}
