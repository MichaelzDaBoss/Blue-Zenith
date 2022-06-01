
package cat.module.modules.player;

import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.IntegerValue;
import cat.util.ClientUtils;
import cat.util.MillisTimer;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

public final class ChestStealer extends Module {
    private final IntegerValue maxDelay = new IntegerValue("MaxDelay", 200, 0, 600, 10, true, (old_, new_) -> {updateDelay(); return new_ < whenth().get() ? old_ : new_;}, null);
    private final IntegerValue minDelay = new IntegerValue("MinDelay", 200, 0, 600, 10, true, (old_, new_) -> {updateDelay(); return new_ > maxDelay.get() ? old_ : new_;}, null);
    private IntegerValue whenth(){
        return minDelay;
    }
    private final IntegerValue maxCloseDelay = new IntegerValue("MaxCloseDelay", 500, 0, 1000, 10, true, (old_, new_) -> {updateDelay(); return new_ < hmmmmm().get() ? old_ : new_;}, null);
    private final IntegerValue minCloseDelay = new IntegerValue("MinCloseDelay", 500, 0, 1000, 10, true, (old_, new_) -> {updateDelay(); return new_ > maxCloseDelay.get() ? old_ : new_;}, null);
    private IntegerValue hmmmmm(){
        return minCloseDelay;
    }
    private final MillisTimer timer = new MillisTimer();
    private long wait = 0;
    private long closeWait = 0;
    public ChestStealer() {
        super("ChestStealer", "", ModuleCategory.PLAYER, "stealer");
    }

    @Subscribe
    public void onUpdatePlayer(UpdatePlayerEvent e) {
        if(!(mc.currentScreen instanceof GuiChest))
            return;

        final GuiChest chest = (GuiChest) mc.currentScreen;
        if(mc.thePlayer == null){
            return;
        }
        if (this.emptyChestCheck(chest)) {
            if(timer.hasTimeReached(closeWait)){
                Minecraft.getMinecraft().thePlayer.closeScreen();
                timer.reset();
            }
            return;
        }
        if((this.mc.thePlayer.openContainer != null) && ((this.mc.thePlayer.openContainer instanceof ContainerChest))) {
            ContainerChest chest1 = (ContainerChest) this.mc.thePlayer.openContainer;
            for(int i = 0; i < chest1.getLowerChestInventory().getSizeInventory(); i++) {
                if((chest1.getLowerChestInventory().getStackInSlot(i) != null && timer.hasTimeReached(wait))) {
                    this.mc.playerController.windowClick(chest1.windowId, i, 1, 1, this.mc.thePlayer);
                    updateDelay();
                    timer.reset();
                }
            }
        }
    }

    public boolean emptyChestCheck(GuiChest guiChest) {
        for (int i = 0; i < guiChest.lowerChestInventory.getSizeInventory(); i++) {
            ItemStack itemStack = guiChest.lowerChestInventory.getStackInSlot(i);
            if (itemStack != null) {
                return false;
            }
        }
        return true;
    }
    public void updateDelay(){
        wait = ClientUtils.getRandomInt(minDelay.get(), maxDelay.get());
        closeWait = ClientUtils.getRandomInt(minCloseDelay.get(), maxCloseDelay.get());
    }
    @Override
    public String getTag() {
        return wait + ":"+closeWait;
    }
}
