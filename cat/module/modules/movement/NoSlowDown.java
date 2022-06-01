package cat.module.modules.movement;

import cat.events.impl.SlowdownEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;
import cat.util.MillisTimer;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public class NoSlowDown extends Module {

    public ModeValue mode = new ModeValue("Mode", "Vanilla", true, null, "Vanilla", "NCP", "NCP Something", "Intave", "Delay", "Hypixel");

    public FloatValue itemMulti = new FloatValue("Reduce by", 1f, 0f, 1f, 0.1f, true, null);

    public BooleanValue ignoreFood = new BooleanValue("Ignore food", false, true, null);
    public BooleanValue ignoreBow = new BooleanValue("Ignore bow", false, true, null);

    private final MillisTimer timer = new MillisTimer().reset();
    private boolean aBoolean, blocking, intaveFunnyBoolean;
    private long delay;
    private int ticks;

    @Override
    public void onEnable() {
        ticks = 0;
        blocking = false;
    }

    public NoSlowDown() {
        super("NoSlowDown", "", ModuleCategory.MOVEMENT, "noslow");
    }

    @Subscribe
    public void onSlowdown(SlowdownEvent event) {
        if(ignoreFood.get() && (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood))
            return;

        if(ignoreBow.get() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow))
            return;

        if(itemMulti.get() == 0F) event.cancel();
        event.reducer = itemMulti.get();
    }

    @Subscribe
    public void onMotion(UpdatePlayerEvent event) {
        if(event.isPre()) {
            switch (mode.get()) {
                case "NCP": {
                    if (mc.thePlayer.isBlocking()) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                    break;
                }

                case "Hypixel": {
                    if (mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                    break;
                }

                case "Delay": {
                    if (!mc.thePlayer.isBlocking()) aBoolean = false;

                    if (mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 5 == 0 && aBoolean) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));

                        aBoolean = false;
                    }

                    if (mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 5 == 1 && !aBoolean) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));

                        aBoolean = true;
                    }
                    break;
                }

                case "NCP Something": {
                    if (mc.thePlayer.isBlocking()) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                    }

                    break;
                }

                case "Intave":
                    if (mc.thePlayer.isBlocking() && timer.hasTimeReached(delay)) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                    break;
            }
        } else {
            switch (mode.get()) {
                case "NCP": {
                    if (mc.thePlayer.isBlocking()) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                    }
                    break;
                }

                case "Hypixel": {
                    if (mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                    }
                    break;
                }

                case "NCP Something": {
                    if (mc.thePlayer.isBlocking()) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }

                    break;
                }

                case "Intave":
                    if (mc.thePlayer.isBlocking() && timer.hasTimeReached(delay)) {
                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        delay = 200;
                        if (intaveFunnyBoolean) {
                            delay = 100;
                            intaveFunnyBoolean = false;
                        } else
                            intaveFunnyBoolean = true;
                        timer.reset();
                    }
                    break;
            }
        }
    }

}
