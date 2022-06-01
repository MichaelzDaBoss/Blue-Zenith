package cat.module.modules.movement;

import cat.events.impl.BlockBBEvent;
import cat.events.impl.MoveEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;
import cat.util.MillisTimer;
import cat.util.MovementUtil;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class Flight extends Module {

    private final ModeValue mode = new ModeValue("Mode", "Vanilla", true, null, "Vanilla", "OldVerus", "Watchdoggo");

    private final FloatValue speed = new FloatValue("Speed", 2f, 0f, 5f, 0.1f, true, __ -> mode.get().equals("Vanilla"));

    public Flight() {
        super("Flight", "", ModuleCategory.MOVEMENT, Keyboard.KEY_F);
    }

    public final float[] movementSpeed = new float[]{0, 0, 0};
    private final MillisTimer verusTimer = new MillisTimer();

    ArrayList<BlockPos> positions = new ArrayList<>();

    @Subscribe
    public void onUpdate(UpdatePlayerEvent e) {

        switch (mode.get()) {
            case "Vanilla":
                if (MovementUtil.areMovementKeysPressed()) {
                    MovementUtil.setSpeed(speed.get());
                } else {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                }
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = speed.get();
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = -speed.get();
                } else {
                    mc.thePlayer.motionY = 0;
                }
                break;

            case "OldVerus":
                if(MovementUtil.areMovementKeysPressed() && movementSpeed[2] > f){
                    float mv = movementSpeed[0] / 0.1536f;
                    if(mv <= 2.213541 && movementSpeed[1] != 1){
                        movementSpeed[0] += 0.01f;
                    }else if(mv >= 0.1536f){
                        movementSpeed[1] = 1;
                        movementSpeed[0] -= 0.03;
                    }

                    MovementUtil.setSpeed(mv);
                }else if(movementSpeed[2] <= f){
                    if(movementSpeed[2] <= f - 1){
                        e.onGround = false;
                        if(verusTimer.hasTicksPassed(12)){
                            mc.thePlayer.jump();
                            verusTimer.reset();
                            movementSpeed[2]++;
                        }
                    }else if(verusTimer.hasTicksPassed(12)){
                        e.onGround = true;
                        movementSpeed[2]++;
                        verusTimer.reset();
                        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY) + 0.42, mc.thePlayer.posZ);
                    }
                    if(mc.thePlayer.hurtTime == 9){
                        movementSpeed[2] = f + 1;
                    }
                }else{
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                }
                break;

            case "Watchdoggo":
                if (MovementUtil.areMovementKeysPressed()) {
                    MovementUtil.setSpeed((float) MovementUtil.getBaseMoveSpeed()*0.9F);
                }

                if(!positions.contains(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ))) {
                    BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ);
                    if(mc.thePlayer.ticksExisted % 3 == 0) {
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX(), pos.getY()+1.05, pos.getZ(), true));
                    }
                    positions.add(pos);
                }

        }
    }

    @Subscribe
    public void onMove(MoveEvent e){
        if(movementSpeed[2] <= f && this.mode.get().equals("OldVerus")){
            e.x = 0;
            e.z = 0;
        }
    }

    @Subscribe
    public void onBlockBB(BlockBBEvent e){
        assert mc.thePlayer != null;
        if(!e.block.getMaterial().isSolid() && e.pos.getY() < mc.thePlayer.posY){
            if((mode.get().equals("OldVerus") || mode.is("Watchdog")) && movementSpeed[2] > f){
                e.blockBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 0.42, 5).offset(e.pos.getX(), e.pos.getY(), e.pos.getZ());
            }
        }
    }

    public int f = 3;

    @Override
    public void onEnable(){
        if (mode.get().equals("OldVerus")) {
            movementSpeed[0] = 0.24f;
            movementSpeed[1] = 0;
            movementSpeed[2] = 0;
        }
        if(mode.get().equals("Watchdog")){
            for(int i = 0; i < 10; i++){
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - (1*0.25), mc.thePlayer.posZ, true));
            }
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ, true));
        }
    }
    @Override
    public void onDisable(){
        if (mode.get().equals("OldVerus")) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        }
        positions.clear();
    }
    @Override
    public String getTag(){
        return this.mode.get();
    }
}
