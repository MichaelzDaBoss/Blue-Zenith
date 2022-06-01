package cat.module.modules.misc;

import cat.events.impl.PacketEvent;
import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

@SuppressWarnings("unused")
public class InvMove extends Module {
    public InvMove() {
        super("InvMove", "", ModuleCategory.MISC, "InventoryMove");
    }

    private final BooleanValue sneak = new BooleanValue("Sneak", false, true, null);
    private final BooleanValue hypixle = new BooleanValue("Hypixel", false, true, null);

    @Subscribe
    public void onUpdate(UpdateEvent event){
        if(mc.currentScreen instanceof GuiChat || mc.currentScreen == null)
            return;
        set(mc.gameSettings.keyBindForward);
        set(mc.gameSettings.keyBindBack);
        set(mc.gameSettings.keyBindRight);
        set(mc.gameSettings.keyBindLeft);
        set(mc.gameSettings.keyBindJump);
        set(mc.gameSettings.keyBindSprint);
        if(sneak.get())
            set(mc.gameSettings.keyBindSneak);
    }
    private void set(KeyBinding key){
        key.pressed = GameSettings.isKeyDown(key);
    }

    @Subscribe
    public void packet(PacketEvent e) {
        if(hypixle.get()) {
            if (e.packet instanceof C0DPacketCloseWindow)
                e.cancel();

            if (e.packet instanceof C0EPacketClickWindow) {
                e.cancel();

                PacketUtil.sendSilent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                PacketUtil.sendSilent(e.packet);
                PacketUtil.sendSilent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            }

            if (e.packet instanceof C16PacketClientStatus) {
                final C16PacketClientStatus packetClientStatus = (C16PacketClientStatus) e.packet;
                if (packetClientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)
                    e.cancel();
            }
        }
    }
}
