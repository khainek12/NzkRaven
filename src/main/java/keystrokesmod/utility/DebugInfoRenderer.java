package keystrokesmod.utility;

import keystrokesmod.Client;
import keystrokesmod.event.player.PreMotionEvent;
import keystrokesmod.event.network.SendPacketEvent;
import keystrokesmod.event.render.Render2DEvent;
import keystrokesmod.module.impl.other.anticheats.utils.world.PlayerMove;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.font.CenterMode;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C03PacketPlayer;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static keystrokesmod.utility.Utils.mc;

public class DebugInfoRenderer extends net.minecraft.client.gui.Gui {
    private static final Queue<Double> speedFromJump = new ConcurrentLinkedQueue<>();
    private static double avgSpeedFromJump = -1;
    private static Vec3 lastServerPos = Vec3.ZERO;
    private static Vec3 curServerPos = Vec3.ZERO;

    @EventListener
    public void onPreMotion(PreMotionEvent event) {
        if (!Client.debugger || !Utils.nullCheck()) {
            speedFromJump.clear();
            avgSpeedFromJump = -1;
            return;
        }

        if (mc.thePlayer.onGround) {
            if (!speedFromJump.isEmpty()) {
                avgSpeedFromJump = 0;
                for (double speed : speedFromJump) {
                    avgSpeedFromJump += speed;
                }
                avgSpeedFromJump /= speedFromJump.size();
            }
            speedFromJump.clear();
        }
        speedFromJump.add(PlayerMove.getXzSecSpeed(
                new Vec3(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ),
                new Vec3(event.getPosX(), event.getPosY(), event.getPosZ()))
        );
    }

    @EventListener
    public void onRenderTick(Render2DEvent ev) {
        if (!Client.debugger || !Utils.nullCheck()) {
            return;
        }

        if (mc.currentScreen == null) {
            int fps = mc.getDebugFPS();
            int ping = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();

            // Hiển thị FPS + Ping + Speed
            RenderUtils.renderBPS(
                String.format("FPS: %d  Ping: %dms  Server speed: %.2fbps", 
                    fps, ping, PlayerMove.getXzSecSpeed(lastServerPos, curServerPos)
                ), 
                true, true
            );

            // Hiển thị speed từ jump
            if (avgSpeedFromJump != -1) {
                ScaledResolution scaledResolution = new ScaledResolution(Client.mc);

                FontManager.getMinecraft().drawString(
                        String.format("Speed from jump: %.2f", avgSpeedFromJump),
                        (float)(scaledResolution.getScaledWidth() / 2),
                        (float)(scaledResolution.getScaledHeight() / 2 + 30),
                        CenterMode.X,
                        false,
                        new Color(255, 255, 255).getRGB()
                );
            }

            // Hiển thị keystrokes (WASD + SPACE + LMB + RMB)
            int x = 20;
            int y = 40;
            drawKey("W", Keyboard.isKeyDown(Keyboard.KEY_W), x + 20, y);
            drawKey("A", Keyboard.isKeyDown(Keyboard.KEY_A), x, y + 20);
            drawKey("S", Keyboard.isKeyDown(Keyboard.KEY_S), x + 20, y + 20);
            drawKey("D", Keyboard.isKeyDown(Keyboard.KEY_D), x + 40, y + 20);
            drawKey("SPACE", Keyboard.isKeyDown(Keyboard.KEY_SPACE), x + 10, y + 40, 50, 15);
            drawKey("LMB", mc.gameSettings.keyBindAttack.isKeyDown(), x, y + 60, 30, 15);
            drawKey("RMB", mc.gameSettings.keyBindUseItem.isKeyDown(), x + 35, y + 60, 30, 15);
        }
    }

    private void drawKey(String text, boolean pressed, int x, int y) {
        drawKey(text, pressed, x, y, 20, 20);
    }

    private void drawKey(String text, boolean pressed, int x, int y, int w, int h) {
        int color = pressed ? new Color(100, 200, 100, 200).getRGB() : new Color(0, 0, 0, 120).getRGB();
        drawRect(x, y, x + w, y + h, color);
        FontManager.getMinecraft().drawCenteredString(text, x + w / 2f, y + h / 2f - 3, 0xFFFFFF);
    }

    @EventListener
    public void onSendPacket(@NotNull SendPacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
            C03PacketPlayer.C04PacketPlayerPosition packet = (C03PacketPlayer.C04PacketPlayerPosition) event.getPacket();
            lastServerPos = curServerPos;
            curServerPos = new Vec3(
                    packet.getPositionX(),
                    packet.getPositionY(),
                    packet.getPositionZ()
            );
        } else if (event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            C03PacketPlayer.C06PacketPlayerPosLook packet = (C03PacketPlayer.C06PacketPlayerPosLook) event.getPacket();
            lastServerPos = curServerPos;
            curServerPos = new Vec3(
                    packet.getPositionX(),
                    packet.getPositionY(),
                    packet.getPositionZ()
            );
        }
    }
}
