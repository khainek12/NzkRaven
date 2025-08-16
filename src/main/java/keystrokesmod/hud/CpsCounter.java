package keystrokesmod.hud;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

public class CpsCounter {
    private static int leftClicks = 0, rightClicks = 0;
    private static long lastSecond = System.currentTimeMillis();
    private static int leftCps = 0, rightCps = 0;

    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent e) {
        long now = System.currentTimeMillis();
        if (Mouse.getEventButtonState()) { // button pressed
            int btn = Mouse.getEventButton();
            if (btn == 0) leftClicks++;
            if (btn == 1) rightClicks++;
        }
        if (now - lastSecond >= 1000) {
            leftCps = leftClicks;
            rightCps = rightClicks;
            leftClicks = 0;
            rightClicks = 0;
            lastSecond = now;
        }
    }

    public static int getLeftCps() { return leftCps; }
    public static int getRightCps() { return rightCps; }
}
