package cat.module.modules.misc;

import cat.events.impl.LightningEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.ClientUtils;
import com.google.common.eventbus.Subscribe;

public class LightningDetector extends Module {

    public final BooleanValue notification = new BooleanValue("Notification", true, true, null);

    public LightningDetector() {
        super("LightningDetector", "", ModuleCategory.MISC);
    }

    @Subscribe
    public void onLightning(LightningEvent e) {
        String lightningdetect = "Lightning detected at %x %y %z!".replaceAll("%x", e.x + "").replaceAll("%y", e.x + "").replaceAll("%z", e.z + "");
        ClientUtils.fancyMessage(lightningdetect);

        if(notification.get()) {
            NotificationManager.addNoti("Lightning Detector", lightningdetect, NotificationType.INFO, 15000L);
        }
    }

}
