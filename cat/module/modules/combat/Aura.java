package cat.module.modules.combat;

import cat.BlueZenith;
import cat.FriendManager;
import cat.events.EventType;
import cat.events.impl.*;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.ModuleManager;
import cat.module.modules.movement.NoSlowDown;
import cat.module.modules.movement.Speed;
import cat.module.modules.player.Scaffold;
import cat.module.modules.render.ClickGUI;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.IntegerValue;
import cat.module.value.types.ModeValue;
import cat.ui.clickgui.ClickGui;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.*;
import com.google.common.eventbus.Subscribe;
import com.sun.org.apache.xpath.internal.operations.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Aura extends Module {
    private final MillisTimer timer = new MillisTimer();

    public static EntityLivingBase target;

    public static boolean visualBlock() {
        return ModuleManager.getModuleClass(Aura.class).getState() && target != null && !((Aura)ModuleManager.getModuleClass(Aura.class)).blockMode.is("None");
    }

    public static boolean isActuallyBlocking() {
        return ModuleManager.getModuleClass(Aura.class).getState() && ((Aura)ModuleManager.getModuleClass(Aura.class)).blocking;
    }

    public static float yaw, pitch, lastYaw, lastPitch, serverYaw, serverPitch;
    private float randomYaw, randomPitch, derpYaw;
    private float sinWaveTicks;
    private double targetPosX, targetPosY, targetPosZ;
    private Vec3 positionOnPlayer, lastPositionOnPlayer;

    private final double ticks = 0;
    private final long lastFrame = 0;
    private int hitTicks, cps, targetIndex;
    public boolean blocking;
    private final List<C03PacketPlayer.C04PacketPlayerPosition> packetList = new ArrayList<>();
    private boolean targetstrafe;

    // Mode options.
    private final ModeValue mode = new ModeValue("Mode", "Single", true, null, "Single", "Switch", "Multi");
    private final ModeValue rotationMode = new ModeValue("Rotation Mode", "Smooth", true, null, "Custom",
            "Custom Advanced", "Smooth", "Sin Wave", "Down", "Derp", "None");
    public final ModeValue blockMode = new ModeValue("Auto Block", "None", true, null, "None", "Fake", "Vanilla",
            "Bypass", "NCP", "AAC", "Interact", "Hypixel");
    private final ModeValue sortingMode = new ModeValue("Sorting", "Distance", true, null, "Distance", "Health", "Hurttime");

    // General boolean options.
    private final FloatValue range = new FloatValue("Range", 3, 0, 6, 0.1F, true, null);
    private final FloatValue rotationRange = new FloatValue("Rotation Range", 6, 0, 12, 0.1F, true, null);

    private final IntegerValue minCps = new IntegerValue("Min APS", 8, 1, 20, 1, true,
            __ -> { updateMinMax(); return true; });
    private final IntegerValue maxCps = new IntegerValue("Max APS", 8, 1, 20, 1, true, null);
    private final IntegerValue maxTargets = new IntegerValue("Multi Targets", 25, 2, 50, 1, true, __ -> mode.is("Multi"));

    // Bypass options.
    private final FloatValue predict = new FloatValue("Predict", 0, 0, 4, 0.1F, true, null);
    private final FloatValue random = new FloatValue("Random", 0, 0, 18, 0.1F, true, null);
    private final FloatValue maxRotation = new FloatValue("Max Rot", 180, 1, 180, 0.1F, true,
            __ -> rotationMode.is("Custom Simple") || rotationMode.is("Custom"));
    private final FloatValue minRotation = new FloatValue("Min Rot", 180, 1, 180, 0.1F, true,
            __ -> rotationMode.is("Custom Simple") || rotationMode.is("Custom"));
    private final FloatValue minYawRotation = new FloatValue("Min Yaw", 180, 1, 180, 0.1F, true,
            __ -> rotationMode.is("Custom Advanced"));
    private final FloatValue maxYawRotation = new FloatValue("Max Yaw", 180, 1, 180, 0.1F, true,
            __ -> rotationMode.is("Custom Advanced"));
    private final FloatValue minPitchRotation = new FloatValue("Min Pitch", 180, 1, 180, 0.1F, true,
            __ -> rotationMode.is("Custom Advanced"));
    private final FloatValue maxPitchRotation = new FloatValue("Max Pitch", 180, 1, 180, 0.1F, true,
            __ -> rotationMode.is("Custom Advanced"));
    private final FloatValue sinWaveSpeed = new FloatValue("Sin Speed", 180, 1, 180, 0.1F, true,
            __ -> rotationMode.is("Sin Wave"));
    private final IntegerValue derpSpeed = new IntegerValue("Derp Speed", 30, 1, 180, 1, true,
            __ -> rotationMode.is("Derp"));
    private final BooleanValue predictedPosition = new BooleanValue("Predict", false, true,
            __ -> !rotationMode.is("Derp") && !rotationMode.is("None"));
    private final BooleanValue rayTrace = new BooleanValue("Raytrace", false, true, null);
    private final BooleanValue alwaysSwing = new BooleanValue("Realistic Swings", false, true, null);
    private final BooleanValue throughWalls = new BooleanValue("Ignore Walls", true, true, null);
    private final BooleanValue silentRotations = new BooleanValue("Silent Rotation", true, true, null);
    public final BooleanValue keepSprint = new BooleanValue("Keep Sprint", false, true, null);
    private final BooleanValue strafe = new BooleanValue("Correct Strafe", false, true, null);
    private final BooleanValue newCombat = new BooleanValue("1.9 Delay", false, true, null);
    private final BooleanValue newSwing = new BooleanValue("1.9 Swing", false, true, null);
    private final BooleanValue blockFix = new BooleanValue("Block Fix (HYP)", false, true, null);

    // Render options.
    private final BooleanValue targetESP = new BooleanValue("Target ESP", true, true, null);
    private final BooleanValue displayRange = new BooleanValue("Show Reach", false, true, null);

    // Other options.
    private final BooleanValue disableOnWorldChange = new BooleanValue("Auto Disable", true, true, null);
    private final BooleanValue attackWithScaffold = new BooleanValue("Attack while scaffolding", false, true, null);
    private final BooleanValue attackInInterfaces = new BooleanValue("Attack in Inventory", true, true, null);
    private final BooleanValue onClick = new BooleanValue("Click Aura", false, true, null);

    @Override
    public String getTag() {
        return mode.get();
    }

    public Aura() {
        super("Aura", "", ModuleCategory.COMBAT, "killaura");
    }

    void updateMinMax() {
        if(maxCps.get() < minCps.get()) {
            maxCps.set(minCps.get());
        }
        if(rotationRange.get() < range.get()) {
            rotationRange.set(rotationRange.get());
        }
    }

    @Subscribe
    public void onWorldChanged(final WorldEvent event) {
        if (this.disableOnWorldChange.get()) {
            NotificationManager.addNoti("Aura", "Disabled due to world change.", NotificationType.INFO, 1000L);
            this.toggle();
        }
    }

    @Subscribe
    public void onMotion(UpdatePlayerEvent event) {

        if(event.isPre()) {
            ++this.hitTicks;
            if (target != null && blockMode.is("Hypixel")) {
                double playerDistance = mc.thePlayer.getDistanceToEntity(target);
                if (playerDistance <= range.get() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof net.minecraft.item.ItemSword)
                    mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), mc.thePlayer.getCurrentEquippedItem().getMaxItemUseDuration());
            }
            /* Used to determine if targetstrafe is enabled so aura doesn't override movementyaw */
            targetstrafe = ModuleManager.

                    getModuleClass(TargetStrafe.class).

                    getState();

            handle:

            {
                /*
                 * If we do not have a target that means we cannot run the aura, so we
                 * can just break our label here as there is no point in going further.
                 */
                if (target == null) {
                    /*
                     * We want to make sure whilst we do not have a target to attack we
                     * do not break the players strafing, so we reset it every tick in here.
                     */
                    if (!targetstrafe) EntityPlayer.movementYaw = null;

                    /*
                     * Unblocking sword serverside if sword is blocked,
                     * this is to stop movement from flagging on some AntiCheats
                     * because the sword is still blocking and the player still runs at full speed.
                     */
                    unblock();

                    break handle;
                } else {
                    switch (blockMode.get()) {
                        case "NCP":
                        case "Interact":
                            unblock();
                            break;
                    }
                }

                /*
                 * Whilst we have silent rotations enabled we only want the rotations to be seen server sided.
                 * And whilst we have non-silent rotations we can just update our rotations manually.
                 */
                if (this.silentRotations.get() && !rotationMode.is("None")) {
                    event.yaw = (serverYaw);
                    event.pitch = (serverPitch);

                    mc.thePlayer.renderYawOffset = serverYaw;
                    mc.thePlayer.rotationYawHead = serverYaw;
                } else {
                    mc.thePlayer.rotationYaw = serverYaw;
                    mc.thePlayer.rotationPitch = serverPitch;
                }

                /*
                 * Gets position on player to be used for render options
                 */

                final Vec3 rayCast = Objects.requireNonNull(PlayerUtil.getMouseOver(serverYaw, serverPitch, (float) range.get())).hitVec;
                if (rayCast == null) return;
                lastPositionOnPlayer = positionOnPlayer;
                positionOnPlayer = rayCast;
            }
        } else {
            if (target != null && PlayerUtil.isHoldingSword()) {
                switch (blockMode.get()) {
                    case "NCP":
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        break;

                    case "Hypixel":
                        block();
                        break;

                    case "Interact":
                        mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        break;
                }
            }
        }

    }

    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        if (!(!onClick.get() || Mouse.isButtonDown(0))) {
            target = null;
            return;
        }

        if (target == null) {
            /*
             * We want to make sure whilst we do not have a target to attack we
             * do not break the players strafing, so we reset it every tick in here.
             */
            if (!targetstrafe) EntityPlayer.movementYaw = null;

            /*
             * This unblocks the aura when its not in a situation to block, because otherwise you flag movement
             */
            unblock();

            sinWaveTicks = 0;

            return;
        }

        double ping = 250;
        ping /= 50;
        if (predictedPosition.get()) {
            final double deltaX = (target.posX - target.lastTickPosX) * 2;
            final double deltaY = (target.posY - target.lastTickPosY) * 2;
            final double deltaZ = (target.posZ - target.lastTickPosZ) * 2;
            targetPosX = target.posX + deltaX * ping;
            targetPosY = target.posY + deltaY * ping;
            targetPosZ = target.posZ + deltaZ * ping;
        } else {
            targetPosX = target.posX;
            targetPosY = target.posY;
            targetPosZ = target.posZ;
        }

       // if (AutoGap.gap != -37) {
       //     this.unblock();
       //     return;
       // }

        if (!rotationMode.is("Sin Wave"))
            sinWaveTicks = 0;

        if ((ModuleManager.getModuleClass(Scaffold.class).getState() && !attackWithScaffold.get())
                || ((mc.currentScreen != null && !(mc.currentScreen instanceof ClickGui)) && !attackInInterfaces.get())) {
            unblock();
            target = null;
            return;
        }

        /*
         * For our movement to be correctly fixed we are going to have
         * to use rotations the server actually sees instead of the
         * current ones as our rotations update per frame and this
         * will make it so our movement yaw and server yaw will
         * be different which will cause issues.
         */
        serverYaw = yaw;
        serverPitch = pitch;

        /*
         * If we want to correct our movement whilst rotating silently we can update
         * the movementYaw variable which will correct our movement to the given yaw for us.
         */
        if (this.strafe.get() && this.silentRotations.get()) EntityPlayer.movementYaw = serverYaw;
        else if (!targetstrafe) EntityPlayer.movementYaw = null;

        double delayValue = -1;

        /*
         * In the modern versions of Minecraft there is a hit delay which occurs when you hit somebody.
         * On specific items for a certain amount of ticks causes low damage until the time required has passed.
         * We have the delays set in here as an option for people who play on 1.9 and above servers.
         */
        if (this.newCombat.get()) {
            delayValue = 4;

            if (mc.thePlayer.getHeldItem() != null) {
                final Item item = mc.thePlayer.getHeldItem().getItem();

                if (item instanceof ItemSpade || item == Items.golden_axe || item == Items.diamond_axe || item == Items.wooden_hoe || item == Items.golden_hoe)
                    delayValue = 20;

                if (item == Items.wooden_axe || item == Items.stone_axe)
                    delayValue = 25;

                if (item instanceof ItemSword)
                    delayValue = 12;

                if (item instanceof ItemPickaxe)
                    delayValue = 17;

                if (item == Items.iron_axe)
                    delayValue = 22;

                if (item == Items.stone_hoe)
                    delayValue = 10;

                if (item == Items.iron_hoe)
                    delayValue = 7;
            }

            delayValue *= Math.max(1, mc.timer.timerSpeed);
        }

        boolean attack = false;

        /*
         * This is the part we actually calculate the click delay we need in order land another hit.
         * The attack boolean will be true when the time required for another attack passes.
         */
        if (this.timer.hasTimeReached(this.cps)) {
            final int maxValue = (this.minCps.max - this.maxCps.get()) * 20;
            final int minValue = (this.minCps.max - this.minCps.get()) * 20;

            this.cps = (int) (randomBetween(minValue, maxValue) - MathUtil.RANDOM.nextInt(10) + MathUtil.RANDOM.nextInt(10));

            this.timer.reset();

            attack = true;
        } else if (blockMode.is("Bypass"))
            this.unblock();

        /*
         * Updates the Derp Rotation Modes yaw so that it rotates.
         */
        derpYaw += derpSpeed.get() - (((Math.random() - 0.5) * random.get()) / 2);

        if ((!newSwing.get() && attack) || (newSwing.get() && this.hitTicks > delayValue)) {
            final boolean rayCast = PlayerUtil.isMouseOver(serverYaw, serverPitch, target, range.get()) || predictedPosition.get();
            double x = mc.thePlayer.posX;
            double z = mc.thePlayer.posZ;
            final double y = mc.thePlayer.posY;
            final double endPositionX = targetPosX;
            final double endPositionZ = targetPosZ;
            double distanceX = x - endPositionX;
            double distanceZ = z - endPositionZ;
            double distanceY = y - targetPosY;
            double distance = MathHelper.sqrt_double(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ) * 6.5;

            /*
             * Whilst the time required to attack an entity again has passed and our raytrace failed
             * when this setting enabled the client will swing even tho it cannot see it causing more
             * realistic swinging during pvp which could help making the aura less detectable.
             */
            if ((mc.thePlayer.getDistance(targetPosX, targetPosY, targetPosZ) - 0.5657 > (this.range.get()) && !rayCast)
                    || (this.rayTrace.get() && !rayCast)) {
                if (this.alwaysSwing.get()) {
                    PacketUtil.send(new C0APacketAnimation());
                    return;
                }
            }

            /*
             * We want to make sure the target is actually on our attack distance and not only our rotation distance.
             * Plus if raytrace is enabled we shall make sure there is an intersection.
             */
            if (mc.thePlayer.getDistance(targetPosX, targetPosY, targetPosZ) - 0.5657 > (this.range.get())
                    || (this.rayTrace.get() && !rayCast)) return;

            /*
             * If we are not allowed to hit through walls we should not
             * attack the entity by checking if we can see them or not.
             */
            if (!this.throughWalls.get() && !mc.thePlayer.canEntityBeSeen(target)) return;

            /*
             * On the legacy versions of Minecraft the player before sending an interaction
             * packet sends a swing packet. Which is not the case on newer versions.
             */
            if (!this.newSwing.get()) mc.thePlayer.swingItem();

            /*
             * When keep sprint is disabled to keep everything vanilla about
             * movement we can use the games attack method to keep everything vanilla.
             */

            switch (this.blockMode.get()) {
                case "AAC":
                case "Interact": {
                    this.unblock();
                    break;
                }
            }


            switch (mode.get()) {
                case "Single": {
                    /*
                     * Calls attack event so other modules can use information from the entity
                     * When the C02 packet is sent the attack event does not
                     * get called, so we have to manually call it ourselves in here.
                     */
                    final AttackEvent attackEvent = new AttackEvent(target, EventType.PRE);
                    BlueZenith.eventManager.call(attackEvent);

                    if (attackEvent.cancelled)
                        return;

                    if (this.keepSprint.get() && (!mc.thePlayer.onGround)) {
                        PacketUtil.send(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                    } else {
                        mc.playerController.attackEntity(mc.thePlayer, target);
                    }

                    if (mc.thePlayer.fallDistance > 0) mc.thePlayer.onCriticalHit(target);
                    break;
                }

                case "Switch": {
                    final List<EntityLivingBase> entities = getTargets();

                    if (entities.size() >= targetIndex)
                        targetIndex = 0;

                    if (entities.isEmpty()) {
                        targetIndex = 0;
                        return;
                    }

                    final EntityLivingBase entity = entities.get(targetIndex);

                    /*
                     * Calls attack event so other modules can use information from the entity
                     * When the C02 packet is sent the attack event does not
                     * get called, so we have to manually call it ourselves in here.
                     */
                    final AttackEvent attackEvent = new AttackEvent(entity, EventType.PRE);
                    BlueZenith.eventManager.call(attackEvent);

                    if (attackEvent.cancelled)
                        return;

                    if (this.keepSprint.get() && (!mc.thePlayer.onGround)) {
                        PacketUtil.send(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    } else {
                        mc.playerController.attackEntity(mc.thePlayer, entity);
                    }

                    if (mc.thePlayer.fallDistance > 0) mc.thePlayer.onCriticalHit(target);

                    targetIndex++;
                    break;
                }

                case "Multi": {
                    for (final EntityLivingBase entity : getTargets()) {
                        /*
                         * Calls attack event so other modules can use information from the entity
                         * When the C02 packet is sent the attack event does not
                         * get called, so we have to manually call it ourselves in here.
                         */
                        final AttackEvent attackEvent = new AttackEvent(target, EventType.PRE);
                        BlueZenith.eventManager.call(attackEvent);

                        if (attackEvent.cancelled)
                            return;

                        if (this.keepSprint.get() && (!mc.thePlayer.onGround)) {
                            PacketUtil.send(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                        } else {
                            mc.playerController.attackEntity(mc.thePlayer, entity);
                        }

                        if (mc.thePlayer.fallDistance > 0) mc.thePlayer.onCriticalHit(entity);
                    }
                    break;
                }
            }

            /*
             * On the modern versions of Minecraft unlike legacy the player
             * sends an arm swing after they interact with the object.
             */
            if (this.newSwing.get()) mc.thePlayer.swingItem();

            // Resetting the hit ticks
            this.hitTicks = 0;
        }

        if (PlayerUtil.isHoldingSword() && !ModuleManager.getModuleClass(Scaffold.class).getState()) {
            switch (this.blockMode.get()) {
                case "AAC": {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
                        PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    }
                    break;
                }

                case "Bypass":
                case "Vanilla": {
                    PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    break;
                }
            }
        }
    }

    @Subscribe
    public void onRender3DEvent(final Render3DEvent event) {
        this.update();

        if (this.targetESP.get()) {
            switch (mode.get()) {
                case "Single":
                case "Switch": {
                    if (target != null) {
                        drawCircle(target, 0.67, ClickGUI.main_color.getRGB(), true);
                    }
                    break;
                }

                case "Multi": {
                    for (final EntityLivingBase entity : getTargets()) {
                        drawCircle(entity, 0.67, ClickGUI.main_color.getRGB(), true);
                    }
                    break;
                }
            }
        }

        /*
        if (positionOnPlayer != null && lastPositionOnPlayer != null && target != null && targetOnPlayer.get()) {
            Vec3 interpolatedPosition = new Vec3(
                    (positionOnPlayer.xCoord - lastPositionOnPlayer.xCoord) * mc.timer.renderPartialTicks + lastPositionOnPlayer.xCoord,
                    (positionOnPlayer.yCoord - lastPositionOnPlayer.yCoord) * mc.timer.renderPartialTicks + lastPositionOnPlayer.yCoord,
                    (positionOnPlayer.zCoord - lastPositionOnPlayer.zCoord) * mc.timer.renderPartialTicks + lastPositionOnPlayer.zCoord);
            RenderUtil.renderBreadCrumb(interpolatedPosition);
        }
         */

        if (this.displayRange.get()) {
            this.drawCircle(mc.thePlayer, this.range.get() - 1);
        }
    }

    @Override
    public void onEnable() {
        if(mc.thePlayer == null)
            return;

        /*
         * For the first rotation to be properly rounded we can set our last
         * rotations to our current rotations in order to round everything properly.
         */
        lastYaw = mc.thePlayer.rotationYaw;
        lastPitch = mc.thePlayer.rotationPitch;
        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;

        sinWaveTicks = 0;

        /*
         * Sets blocking variable, so we can use this to not send extra blocking packets
         */
        blocking = mc.gameSettings.keyBindUseItem.isKeyDown();
    }

    @Override
    public void onDisable() {
        // We do not want to strafe whilst not using aura.
        if (!targetstrafe) EntityPlayer.movementYaw = null;

        // Resetting the target index for Switch.
        targetIndex = 0;

        // Reset our timer for our attacks.
        timer.reset();

        // Set our target null as we do not want other stuff thinking we are attacking something.
        target = null;

        // This will only unblock if you're already blocking
        unblock();
    }

    /**
     * The update method is used to grab the target we want to attack
     * based on given settings and update the rotations and the last rotations.
     */
    private void update() {
        if ((ModuleManager.getModuleClass(Scaffold.class).getState() && !attackWithScaffold.get())
                || ((mc.currentScreen != null && !(mc.currentScreen instanceof ClickGui)) && !attackInInterfaces.get())) {
            unblock();
            target = null;
            return;
        }

        // Update our target for the aura as we want the entity we want to attack right now.
        this.updateTarget();

        /*
         * If the aura could not find a target on the specified settings
         * we cannot grab rotations or attack anything so we can return.
         */
        if (target == null) {
            lastYaw = mc.thePlayer.rotationYaw;
            lastPitch = mc.thePlayer.rotationPitch;
        } else {
            /*
             * Because we have found a target successfully we can grab the
             * required rotations to look and actually attack this target.
             */
            this.updateRotations();
        }
    }

    private void block() {
        sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
        mc.gameSettings.keyBindUseItem.pressed = true;
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
        blocking = true;
    }

    private void unblock() {
        if (blocking) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            blocking = false;
        }
    }

    public void sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
        if (!(mc.playerController.currentGameType == WorldSettings.GameType.SPECTATOR)) {
            mc.playerController.syncCurrentPlayItem();
            int i = itemStackIn.stackSize;
            ItemStack itemstack = itemStackIn.useItemRightClick(worldIn,
                    playerIn);

            if (itemstack != itemStackIn || itemstack.stackSize != i) {
                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = itemstack;

                if (itemstack.stackSize == 0) {
                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                }

            }
        }
    }

    private void updateRotations() {
        /*
         * Update our last rotations as the current ones as we are updating
         * the current ones soon. We require the last rotations to smooth
         * out the current rotations properly based on the last rotations.
         */
        lastYaw = yaw;
        lastPitch = pitch;

        /*
         * Finally grab the required rotations to actually aim at the target.
         * We do not need to pass in any parameters as the method already grabs the settings for us.
         */
        final float[] rotations = this.getRotations();

        /*
         * We can now update the rotation fields for the aura so the client
         * can send the server the rotations we actually want to apply.
         */
        yaw = rotations[0];
        pitch = rotations[1];

    //    if (rayTrace(lastYaw, lastPitch, rotationRange.get(), target)) {
    //        yaw = lastYaw;
    //        pitch = lastPitch;
    //    }
    }

    private float[] getRotations() {
        final double predictValue = predict.get();

        final double x = (targetPosX - (target.lastTickPosX - targetPosX) * predictValue) + 0.01 - mc.thePlayer.posX;
        final double z = (targetPosZ - (target.lastTickPosZ - targetPosZ) * predictValue) - mc.thePlayer.posZ;

        double minus = (mc.thePlayer.posY - targetPosY);

        if (minus < -1.4) minus = -1.4;
        if (minus > 0.1) minus = 0.1;

        final double y = (targetPosY - (target.lastTickPosY - targetPosY) * predictValue) + 0.4 + target.getEyeHeight() / 1.3 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) + minus;

        final double xzSqrt = MathHelper.sqrt_double(x * x + z * z);

        float yaw = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(z, x)) - 90.0F);
        float pitch = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(-Math.atan2(y, xzSqrt)));

        final double randomAmount = random.get();

        if (randomAmount != 0) {
            randomYaw += ((Math.random() - 0.5) * randomAmount) / 2;
            randomYaw += ((Math.random() - 0.5) * randomAmount) / 2;
            randomPitch += ((Math.random() - 0.5) * randomAmount) / 2;

            if (mc.thePlayer.ticksExisted % 5 == 0) {
                randomYaw = (float) (((Math.random() - 0.5) * randomAmount) / 2);
                randomPitch = (float) (((Math.random() - 0.5) * randomAmount) / 2);
            }

            yaw += randomYaw;
            pitch += randomPitch;
        }

        final int fps = (int) (Minecraft.getDebugFPS() / 20.0F);

        switch (this.rotationMode.get()) {
            case "Custom": {
                if (this.maxRotation.get() != 180.0F && this.minRotation.get() != 180.0F) {
                    final float distance = (float) randomBetween(this.minRotation.get(), this.maxRotation.get());

                    final float deltaYaw = (((yaw - lastYaw) + 540) % 360) - 180;
                    final float deltaPitch = pitch - lastPitch;

                    final float distanceYaw = MathHelper.clamp_float(deltaYaw, -distance, distance) / fps * 4;
                    final float distancePitch = MathHelper.clamp_float(deltaPitch, -distance, distance) / fps * 4;

                    yaw = MathHelper.wrapAngleTo180_float(lastYaw) + distanceYaw;
                    pitch = MathHelper.wrapAngleTo180_float(lastPitch) + distancePitch;
                }
                break;
            }

            case "Custom Simple": {
                final float yawDistance = (float) randomBetween(this.minRotation.get(), this.maxRotation.get());
                final float pitchDistance = (float) randomBetween(this.minRotation.get(), this.maxRotation.get());


                final float deltaYaw = (((yaw - lastYaw) + 540) % 360) - 180;
                final float deltaPitch = pitch - lastPitch;

                final float distanceYaw = MathHelper.clamp_float(deltaYaw, -yawDistance, yawDistance) / fps * 4;
                final float distancePitch = MathHelper.clamp_float(deltaPitch, -pitchDistance, pitchDistance) / fps * 4;

                yaw = lastYaw + distanceYaw;
                pitch = lastPitch + distancePitch;
                break;
            }

            case "Custom Advanced": {
                final float advancedYawDistance = (float) randomBetween(this.minYawRotation.get(), this.maxYawRotation.get());
                final float advancedPitchDistance = (float) randomBetween(this.minPitchRotation.get(), this.maxPitchRotation.get());

                final float advancedDeltaYaw = (((yaw - lastYaw) + 540) % 360) - 180;
                final float advancedDeltaPitch = pitch - lastPitch;

                final float advancedDistanceYaw = MathHelper.clamp_float(advancedDeltaYaw, -advancedYawDistance, advancedYawDistance) / fps * 4;
                final float advancedDistancePitch = MathHelper.clamp_float(advancedDeltaPitch, -advancedPitchDistance, advancedPitchDistance) / fps * 4;

                yaw = lastYaw + advancedDistanceYaw;
                pitch = lastPitch + advancedDistancePitch;
                break;
            }

            case "Smooth": {
                final float yawDelta = (float) (((((yaw - lastYaw) + 540) % 360) - 180) / (fps / 3 * (1 + Math.random())));
                final float pitchDelta = (float) ((pitch - lastPitch) / (fps / 3 * (1 + Math.random())));

                yaw = lastYaw + yawDelta;
                pitch = lastPitch + pitchDelta;

                break;
            }

            case "Down": {
                pitch = RandomUtils.nextFloat(89, 90);
                break;
            }

            case "Derp": {
                pitch = RandomUtils.nextFloat(89, 90);
                yaw = derpYaw;
                break;
            }

            case "Sin Wave": {
                final float halal = (float) (Math.abs(Math.sin((sinWaveTicks + Math.random() * 0.001) / 10)) * sinWaveSpeed.get());

                final float sinWaveYaw = MathHelper.clamp_float((((yaw - lastYaw) + 540) % 360) - 180, -halal, halal) / fps;
                final float sinWavePitch = MathHelper.clamp_float(pitch - lastPitch, -halal, halal) / fps / fps;

                yaw = lastYaw + sinWaveYaw;
                pitch = lastPitch + sinWavePitch;

                sinWaveTicks++;
                break;
            }
        }

        final float[] rotations = new float[]{yaw, pitch};
        final float[] lastRotations = new float[]{Aura.yaw, Aura.pitch};

        final float[] fixedRotations = RotationUtil.getFixedRotation(rotations, lastRotations);

        yaw = fixedRotations[0];
        pitch = fixedRotations[1];

        if (this.rotationMode.is("None")) {
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
        }

        pitch = MathHelper.clamp_float(pitch, -90.0F, 90.0F);

        return new float[]{yaw, pitch};
    }

    private List<EntityLivingBase> getTargets() {
        final List<EntityLivingBase> entities = mc.theWorld.loadedEntityList
                // Stream our entity list.
                .stream()

                // Only get living entities so we don't have to check for items on ground etc.
                .filter(entity -> entity instanceof EntityLivingBase)

                // Map our entities to entity living base as we have filtered out none living entities.
                .map(entity -> ((EntityLivingBase) entity))

                // Only get the entities we can attack.
                .filter(entity -> {
                    if (entity instanceof EntityPlayer && !EntityManager.Targets.PLAYERS.on) return false;

                    if (!(entity instanceof EntityPlayer) && !EntityManager.Targets.MOBS.on) return false;

                    if (entity.isInvisible() && !EntityManager.Targets.INVISIBLE.on) return false;

                    if (PlayerUtil.isOnSameTeam(entity) && !EntityManager.Targets.TEAMS.on) return false;

                    if (entity.isDead && !EntityManager.Targets.DEAD.on) return false;

                    if (entity.deathTime != 0 && !EntityManager.Targets.DEAD.on) return false;

                    if (entity.ticksExisted < 2) return false;

                    if (((AntiBot) ModuleManager.getModuleClass(AntiBot.class)).isBot(entity)) return false;

                    if(FriendManager.is(entity.getName())) return false;

                    if (entity instanceof EntityPlayer) {
                        final EntityPlayer player = ((EntityPlayer) entity);

                        for (final String name : EntityManager.FriendManager.friends) {
                            if (name.equalsIgnoreCase(player.getName()))
                                return false;
                        }
                    }

                    return mc.thePlayer != entity;
                })

                // Do a proper distance calculation to get entities we can reach.
                .filter(entity -> {
                    // DO NOT TOUCH THIS VALUE ITS CALCULATED WITH MATH
                    final double girth = 0.5657;

                    // See if the other entity is in our range.
                    return mc.thePlayer.getDistanceToEntity(entity) - girth < rotationRange.get();
                })

                // Sort out potential targets with the algorithm provided as a setting.
                .sorted(Comparator.comparingDouble(entity -> {
                    switch (sortingMode.get()) {
                        case "Distance":
                            return mc.thePlayer.getDistanceSqToEntity(entity);
                        case "Health":
                            return entity.getHealth();
                        case "Hurttime":
                            return entity.hurtTime;

                        default:
                            return -1;
                    }
                }))

                // Sort out all the specified targets.
                .sorted(Comparator.comparing(entity -> entity instanceof EntityPlayer && !EntityManager.TargetManager.isTarget(entity.getName())))

                // Get the possible targets and put them in a list.
                .collect(Collectors.toList());

        // Removes entities when there are too many targets
        final int maxTargets = (int) Math.round(this.maxTargets.get());

        if (mode.is("Multi") && entities.size() > maxTargets) {
            entities.subList(maxTargets, entities.size()).clear();
        }

        // Returns the list of entities
        return entities;
    }

    private void updateTarget() {
        final List<EntityLivingBase> entities = getTargets();

        // Grab our best option from the list.
        target = entities.size() > 0 ? entities.get(0) : null;
    }

    public double randomBetween(final double min, final double max) {
        return min + (MathUtil.RANDOM.nextDouble() * (max - min));
    }

    private boolean rayTrace(final float yaw, final float pitch, final double reach, final Entity target) {
        final Vec3 vec3 = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        final Vec3 vec31 = mc.thePlayer.getVectorForRotation(MathHelper.clamp_float(pitch, -90.F, 90.F), yaw % 360);
        final Vec3 vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach);

        final MovingObjectPosition objectPosition = target.getEntityBoundingBox().calculateIntercept(vec3, vec32);

        return (objectPosition != null && objectPosition.hitVec != null);
    }

    private void drawCircle(final Entity entity, final double rad, final int color, final boolean shade) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosY) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosZ;

        Color c;

        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64.F) {
            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            c = ClickGUI.main_color;

            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0
                );
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0.85F
                );
            }
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }

    private void drawCircle(final Entity entity, final double rad) {
        GL11.glDisable(3553);
        GL11.glLineWidth(1f);
        GL11.glBegin(3);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY) + mc.thePlayer.getEyeHeight() - 0.7;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        for (int i = 0; i <= 90; ++i) {
            RenderUtil.color(ClickGUI.main_color);

            GL11.glVertex3d(x + rad * Math.cos(i * 6.283185307179586 / 45.0), y, z + rad * Math.sin(i * 6.283185307179586 / 45.0));
        }

        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        RenderUtil.color(Color.WHITE);
    }
}
