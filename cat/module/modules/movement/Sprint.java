package cat.module.modules.movement;

import cat.BlueZenith;
import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.modules.player.Scaffold;
import cat.module.value.types.ModeValue;
import cat.util.MillisTimer;
import cat.util.MovementUtil;
import com.google.common.eventbus.Subscribe;

@SuppressWarnings("unused")
public class Sprint extends Module {

    public final ModeValue mode = new ModeValue("Mode", "Legit", true, null, "Legit", "Forward", "Omni");

    public Sprint() {
        super("Sprint", "", ModuleCategory.MOVEMENT, "Sprint");
    }

    private final MillisTimer timer = new MillisTimer();

    @Override
    public String getTag() {
        return mode.get();
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {

        if(mode.is("Forward")) {
            if (mc.thePlayer.moveForward > 0.0F) {
                if (!mc.thePlayer.isCollidedHorizontally) {
                    if (!mc.thePlayer.isUsingItem()) {
                        if (!mc.thePlayer.isSneaking()) {
                            if (BlueZenith.moduleManager.getModule(Scaffold.class).getState() ?
                                    ((Scaffold) BlueZenith.moduleManager.getModule(Scaffold.class)).sprint.get() : true) {
                                mc.thePlayer.setSprinting(true);
                            }
                        }
                    }
                }
            }
        }

        if(mode.is("Omni")) {
            if (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) {
                if (!mc.thePlayer.isCollidedHorizontally) {
                    if (!mc.thePlayer.isUsingItem()) {
                        if (!mc.thePlayer.isSneaking()) {
                            if (BlueZenith.moduleManager.getModule(Scaffold.class).getState() ?
                                    ((Scaffold) BlueZenith.moduleManager.getModule(Scaffold.class)).sprint.get() : true) {
                                mc.thePlayer.setSprinting(true);
                            }
                        }
                    }
                }
            }
        }

        if(mode.is("Legit")) {
            if (BlueZenith.moduleManager.getModule(Scaffold.class).getState() ?
                    ((Scaffold) BlueZenith.moduleManager.getModule(Scaffold.class)).sprint.get() : true) {
                mc.gameSettings.keyBindSprint.pressed = true;
            } else {
                mc.gameSettings.keyBindSprint.pressed = false;
            }
        }

    }
}





