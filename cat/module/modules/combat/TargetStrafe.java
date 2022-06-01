package cat.module.modules.combat;

import cat.BlueZenith;
import cat.events.impl.MoveEvent;
import cat.events.impl.Render3DEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.util.*;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

public class TargetStrafe extends Module {
    public TargetStrafe() {
        super("TargetStrafe", "", ModuleCategory.COMBAT);
    }
    private final BooleanValue jumpOnly = new BooleanValue("Jump Only", true, true, null);
    private final BooleanValue drawCircle = new BooleanValue("Draw circle", true, true, null);
    private final float range = 2;
    private float direction = 1;
    private EntityLivingBase target = null;
    //blatantly skidded from czechhek

    @Subscribe
    public void onMove(MoveEvent e){
        if(!jumpOnly.get() || !mc.gameSettings.keyBindJump.pressed)
            return;

        target = Aura.target;
        if(target == null)
            return;

        int fov = 360;
        float speed = MovementUtil.currentSpeed();
        double distance = Math.sqrt(Math.pow(mc.thePlayer.posX - target.posX, 2) + Math.pow(mc.thePlayer.posZ - target.posZ, 2));
        double strafeYaw = Math.atan2(target.posZ - mc.thePlayer.posZ, target.posX - mc.thePlayer.posX);
        float yaw = (float) (strafeYaw - (0.5f * Math.PI));
        double[] predict = new double[]{target.posX + (2 * (target.posX - target.lastTickPosX)), target.posZ + (2 * (target.posZ - target.lastTickPosZ))};

        if ((distance - speed) > range || Math.abs(((((yaw * 180 / Math.PI - mc.thePlayer.rotationYaw) % 360) + 540) % 360) - 180) > fov || isAboveGround(predict[0], target.posY, predict[1])) return;

        double encirclement = distance - range < -speed ? -speed : distance - range;
        double encirclementX = -Math.sin(yaw) * encirclement;
        double encirclementZ = Math.cos(yaw) * encirclement;
        double strafeX = -Math.sin(strafeYaw) * speed * direction;
        double strafeZ = Math.cos(strafeYaw) * speed * direction;
        boolean isFacingPlayer = RotationUtil.isFacingPlayer(target.rotationYaw, target.rotationPitch);

        if((isAboveGround(mc.thePlayer.posX + encirclementX + (2 * strafeX), mc.thePlayer.posY, mc.thePlayer.posZ + encirclementZ + (2 * strafeZ)) || mc.thePlayer.isCollidedHorizontally) || isFacingPlayer){
            direction *= -1;
            strafeX *= -1;
            strafeZ *= -1;
        }

        e.x = encirclementX + strafeX;
        e.z = encirclementZ + strafeZ;
    }
    @Subscribe
    public void onRender3D(Render3DEvent e){
        if(target == null || !drawCircle.get()) return;
        GL11.glPushMatrix();
        //GL11.glDepthMask(true);
        GL11.glTranslated(
                target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ
        );
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glLineWidth(2);
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0, index = 0; i <= 360; i += 30, index++) { // the less the best
            if(index > 255) index = 0;
            RenderUtil.glColor(ColorUtil.rainbow(index, 0.5f));
            GL11.glVertex2d(Math.cos(i * Math.PI / 180) * range, (Math.sin(i * Math.PI / 180) * range));
        }

        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        //GL11.glDepthMask(false);
        GL11.glPopMatrix();
        GlStateManager.resetColor();
    }
    public boolean isAboveGround(double x, double y, double z) {
        for (double i = Math.ceil(y); (y - 5) < i--;) if (!mc.theWorld.isAirBlock(new BlockPos(x, i, z))) return false;
        return true;
    }
}
