package cat.module.modules.misc;

import cat.BlueZenith;
import cat.events.impl.PacketEvent;
import cat.events.impl.Render2DEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.events.impl.WorldEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.ModuleManager;
import cat.module.modules.movement.Fly;
import cat.module.modules.movement.Speed;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.IntegerValue;
import cat.module.value.types.ModeValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.BypassUtil;
import cat.util.ClientUtils;
import cat.util.MillisTimer;
import cat.util.PacketUtil;
import cat.util.font.sigma.FontUtil;
import cat.util.font.sigma.TFontRenderer;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.optifine.util.MathUtils;

import java.util.LinkedList;
import java.util.Random;

public class Disabler extends Module {

    int left = 0;

    public static boolean desyncRots() {
        return false;
    }

    public final ModeValue mode = new ModeValue("Mode", "BlocksMC", true, null, "BlocksMC", "AC.GAY", "Hycraft", "Why Does This Work???", "BMC New",
            "Vulcan't", "Minemora", "Ghostly", "MEME", "PULSIVEMEME");

    public final IntegerValue memeDelay = new IntegerValue("Ping Spoof", 50, 10, 200, 1, true, __ -> mode.is("MEME"));
    public final IntegerValue memeMinBalance = new IntegerValue("Min Balance", 1000, 100, 20000, 10, true, __ -> mode.is("MEME"));
    public final FloatValue memeBalanceTimer = new FloatValue("Balance Timer", 0.25F, 0.05F, 1F, 0.05F, true, __ -> mode.is("MEME"));
    public final IntegerValue memeThing = new IntegerValue("Rebalance", 90000, 60000, 120000, 500, true, __ -> mode.is("MEME"));

    public final BooleanValue voidtp = new BooleanValue("Void TP", false, true, __ -> mode.is("BlocksMC") || mode.is("Why Does This Work???"));
    public final BooleanValue smooth = new BooleanValue("Smooth", true, true, __ -> mode.is("BlocksMC") || mode.is("Why Does This Work???"));
    public final BooleanValue smoothNoDs = new BooleanValue("Don't DeSync", false, true, __ -> (mode.is("BlocksMC") || mode.is("Why Does This Work???")) && smooth.get());
    public final BooleanValue noSSFlags = new BooleanValue("$$$ Rise Client $$$", false, true, __ -> (mode.is("BlocksMC") || mode.is("Why Does This Work???")) && smooth.get());

    public final IntegerValue ticks = new IntegerValue("Ticks", 40, 30, 50, 1, true, __ -> mode.is("BMC New"));

    public final BooleanValue debug = new BooleanValue("Debug", false, true, null);

    public Disabler() {
        super("Disabler", "", ModuleCategory.MISC);
    }

    public boolean expectedTeleport;
    int toCancel;
    int packetLossCounter;
    LinkedList<C0FPacketConfirmTransaction> transactions = new LinkedList<>();
    LinkedList<Packet<?>> packets = new LinkedList<>();

    MillisTimer notifTimer = new MillisTimer().reset();
    MillisTimer bufferTimer = new MillisTimer().reset();
    MillisTimer transactionTimer = new MillisTimer().reset();

    MillisTimer vulcanTimer = new MillisTimer().reset();
    MillisTimer vulcanTimer2 = new MillisTimer().reset();

    MillisTimer rebalanceTimer = new MillisTimer().reset();

    long balance;
    long lastPreMotion;
    public float targetTimer;
    float actualTargetTimer;
    int keepalives;
    int lastKAKey;

    @Override
    public String getTag() {
        return mode.get();
    }

    @Override
    public void onEnable() {
        expectedTeleport = false;
    }

    @Override
    public void onDisable() {

        if(mode.is("MEME")) {
            mc.timer.timerSpeed = 1.0F;
            balance = 0;
        }

    }

    @Subscribe
    public void render2d(Render2DEvent e) {

        if(mode.is("MEME")) {
            TFontRenderer fr = FontUtil.fontOpenSansRegular36;
            ScaledResolution sr = new ScaledResolution(mc);
            fr.drawStringWithShadow("Balance: " + balance, sr.getScaledWidth()/2f-(fr.getStringWidth("Balance: " + balance)/2f),
                    sr.getScaledHeight()/2f-40, -1);
            fr.drawStringWithShadow("Timer: " + actualTargetTimer, sr.getScaledWidth()/2f-(fr.getStringWidth("Timer: " + actualTargetTimer)/2f),
                    sr.getScaledHeight()/2f-30, -1);
        }

    }

    @Subscribe
    public void world(WorldEvent event) {

        if(mode.is("BMC New")) {
            if(notifTimer.hasTimeReached(1000)) {
                NotificationManager.addNoti("Disabler", "Please wait 10 seconds before flying!",
                        NotificationType.WARNING, 10000L);
            }
            notifTimer.reset();
            toCancel = 0;
        }

        if(mode.is("Vulcan't")) {
            packetLossCounter = 0;
            toCancel--;
        }

        if(mode.is("MEME")) {
            balance = 0;
            lastPreMotion = 0;
            rebalanceTimer.reset();
        }

    }

    @Subscribe
    public void onUpdate(UpdatePlayerEvent e) {

        if(mode.is("MEME")) {
            if(e.isPre()) {
                if(balance > -memeMinBalance.get()) {
                    actualTargetTimer = memeBalanceTimer.get();
                    rebalanceTimer.reset();
                } else {
                    actualTargetTimer = targetTimer == 0? 1.0F : targetTimer;
                }
            }

            // balance timer
            if(e.isPre()) {
                final long lastPreMotion = this.lastPreMotion;
                this.lastPreMotion = System.currentTimeMillis();

                if (lastPreMotion != 0) {
                    final long difference = System.currentTimeMillis() - lastPreMotion;

                    balance += (rebalanceTimer.hasTimeReached(memeThing.get())? 125 : 50);
                    balance -= difference;
                }

                if (balance < -1000) {
                    //Allow timer :)

                    mc.timer.timerSpeed = actualTargetTimer;

                } else {
                    mc.timer.timerSpeed = 1;
                }

                if (actualTargetTimer < 1) {
                    mc.timer.timerSpeed = actualTargetTimer;
                }
            }
        }

        if(mode.is("AC.GAY")) {
            if(BlueZenith.moduleManager.getModule(Fly.class).getState() || mc.thePlayer.isSpectator()) {
                PlayerCapabilities playerCapabilities = new PlayerCapabilities();
                playerCapabilities.isFlying = true;
                playerCapabilities.allowFlying = true;
                playerCapabilities.setFlySpeed((float) MathUtils.randomNumber(0.1D, 9.0D));
                PacketUtil.sendSilent(new C13PacketPlayerAbilities(playerCapabilities));
            }
        }

        if(mode.is("BMC New")) {
            if(shouldNotRun()) {
                bufferTimer.reset();
                transactions.clear();
                toCancel = 0;
                return;
            }

            if(bufferTimer.hasTimeReached(500)) {
                while(!transactions.isEmpty()) {
                    PacketUtil.sendSilent(transactions.poll());
                }
            }
        }

        if(mode.is("Vulcan't")) {

            if(vulcanTimer.hasTimeReached((long) (5000 + (Math.random() * 1000)))) {
                vulcanTimer.reset();
                transactions.forEach(PacketUtil::sendSilent);
                transactions.clear();
            }

            if(mc.thePlayer.ticksExisted % 20 == 0) {
                e.setY(e.getY() - (Math.random()/100f));
            }

        }

    }

    @Subscribe
    public void onPacket(PacketEvent e) {

        if(mode.is("MEME")) {

            if (e.packet instanceof S00PacketDisconnect) {
                balance = 0;
                lastPreMotion = 0;
            }

            if(e.packet instanceof S08PacketPlayerPosLook) {
                ClientUtils.fancyMessage("sexed at " + balance);
            }

        }

        if(mode.is("Ghostly")) {
            if (e.packet instanceof C00PacketKeepAlive) {
                e.cancel();
            }

            if (e.packet instanceof C0BPacketEntityAction) {
                C0BPacketEntityAction packetEntityAction = (C0BPacketEntityAction) e.packet;
                if (packetEntityAction.getAction() == C0BPacketEntityAction.Action.START_SPRINTING || packetEntityAction.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    e.cancel();
                }
            }

            if (e.packet instanceof C03PacketPlayer) {
                PacketUtil.sendSilent(new C0CPacketInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, mc.thePlayer.movementInput.jump, mc.thePlayer.movementInput.sneak));
                final C03PacketPlayer packetPlayer = (C03PacketPlayer) e.packet;
                double x = mc.thePlayer.posX;
                double y = mc.thePlayer.posY;
                double z = mc.thePlayer.posZ;
                float yaw = mc.thePlayer.rotationYaw;
                float pitch = mc.thePlayer.rotationPitch;
                final boolean ground = packetPlayer.onGround;

                if (packetPlayer.getRotating()) {
                    yaw = packetPlayer.getYaw();
                    pitch = packetPlayer.getPitch();
                }

                if (packetPlayer.isMoving()) {
                    x = packetPlayer.getPositionX();
                    y = packetPlayer.getPositionY();
                    z = packetPlayer.getPositionZ();
                }

                e.packet = (new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, ground));
            }
        }

        if(mode.is("BlocksMC") || mode.is("Why Does This Work???")) {
            if(shouldNotRun())
                return;

            if (e.packet instanceof S08PacketPlayerPosLook && expectedTeleport) {
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.packet;
                expectedTeleport = false;
                PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                if(smooth.get())
                    e.cancel();
            } else if(e.packet instanceof S08PacketPlayerPosLook) {
                if(moveDif() < 0.1D) {
                    sendPredictPacket();
                }
                if(smoothNoDs.get()) {
                    if(moveDif() < 0.1D) {
                        if(smooth.get())
                            e.cancel();
                    }
                } else {
                    if(smooth.get())
                        e.cancel();
                }
            }

            if (e.packet instanceof C0BPacketEntityAction) {
                e.cancel();
            }

            if (e.packet instanceof C13PacketPlayerAbilities) {
                PacketUtil.sendSilent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
                ((C13PacketPlayerAbilities) e.packet).setFlying(true);
                ((C13PacketPlayerAbilities) e.packet).setCreativeMode(true);
                ((C13PacketPlayerAbilities) e.packet).setAllowFlying(true);
                ((C13PacketPlayerAbilities) e.packet).setInvulnerable(true);
                ((C13PacketPlayerAbilities) e.packet).setWalkSpeed(Float.NaN);
                ((C13PacketPlayerAbilities) e.packet).setFlySpeed(Float.NaN);
            }

            if(e.packet instanceof C03PacketPlayer) {

                if(mc.thePlayer.ticksExisted % 40 == 0) {
                    if(!expectedTeleport) {
                        expectedTeleport = true;
                        e.cancel();
                        return;
                    }

                    if (noSSFlags.get()) {
                        if (mc.thePlayer.isMoving()) {
                            if (voidtp.get()) {
                                ((C03PacketPlayer) e.packet).y = -6.9420;
                            } else {
                                ((C03PacketPlayer) e.packet).y = ((C03PacketPlayer) e.packet).y - 6.9420;
                            }
                        } else {
                            if (voidtp.get()) {
                                ((C03PacketPlayer) e.packet).y = -6.9420;
                                PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, Math.abs(-mc.thePlayer.posY + 6.9420), mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                            } else {
                                ((C03PacketPlayer) e.packet).y = ((C03PacketPlayer) e.packet).y - 6.9420;
                            }
                        }
                    } else {
                        if (voidtp.get()) {
                            ((C03PacketPlayer) e.packet).y = -6.9420;
                        } else {
                            ((C03PacketPlayer) e.packet).y = ((C03PacketPlayer) e.packet).y - 6.9420;
                        }
                    }


                    ((C03PacketPlayer) e.packet).onGround = false;
                    ((C03PacketPlayer) e.packet).moving = false;
                    ((C03PacketPlayer) e.packet).rotating = false;
                }

                if(mc.thePlayer.ticksExisted % 5 == 0) {
                    PacketUtil.sendSilent(new C0CPacketInput());
                }
            }

            if (e.packet instanceof C13PacketPlayerAbilities) {
                PacketUtil.sendSilent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
                ((C13PacketPlayerAbilities) e.packet).setAllowFlying(false);
                ((C13PacketPlayerAbilities) e.packet).setCreativeMode(false);
                ((C13PacketPlayerAbilities) e.packet).setInvulnerable(false);
                ((C13PacketPlayerAbilities) e.packet).setFlying(false);
            }
        }

        if(mode.is("AC.GAY")) {
            if (e.packet instanceof C0FPacketConfirmTransaction) {
                C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction)e.packet;
                PacketUtil.sendSilent(new C0FPacketConfirmTransaction(Integer.MAX_VALUE, packetConfirmTransaction.getUid(), false));
                e.cancel();
            }

            if (e.packet instanceof C00PacketKeepAlive) {
                PacketUtil.sendSilent(new C00PacketKeepAlive(Integer.MIN_VALUE + (new Random()).nextInt(100)));
                e.cancel();
            }
        }

        if(mode.is("BMC New")) {
            if(shouldNotRun() || mc.isSingleplayer()) {
                bufferTimer.reset();
                transactions.clear();
                toCancel = 0;
                return;
            }

            if (e.packet instanceof C03PacketPlayer) {
                if(toCancel > 0) {
                    e.cancel();
                    toCancel--;
                    return;
                }
            }

            if (e.packet instanceof C0BPacketEntityAction) {
                e.cancel();
            }

            if (e.packet instanceof C13PacketPlayerAbilities) {
                PacketUtil.sendSilent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
                ((C13PacketPlayerAbilities) e.packet).setFlying(true);
                ((C13PacketPlayerAbilities) e.packet).setCreativeMode(true);
                ((C13PacketPlayerAbilities) e.packet).setAllowFlying(true);
                ((C13PacketPlayerAbilities) e.packet).setInvulnerable(true);
            }

            if(e.packet instanceof S08PacketPlayerPosLook) {
                if(expectedTeleport) {
                    expectedTeleport = false;
                    PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) e.packet).getX(), ((S08PacketPlayerPosLook) e.packet).getY(),
                            ((S08PacketPlayerPosLook) e.packet).getZ(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                    toCancel++;
                } else if(moveDif() < 0.275) {
                    e.cancel();
                }
          //      if(!expectedTeleport) {
          //          sendPredictPacket();
          //      }
            }

            if(e.packet instanceof C03PacketPlayer) {

                if(mc.thePlayer.ticksExisted % ticks.get() == 0) {
                    if(!expectedTeleport) {
                        expectedTeleport = true;
                        e.cancel();
                    }

                    ((C03PacketPlayer) e.packet).moving = false;
                    ((C03PacketPlayer) e.packet).rotating = false;
                    ((C03PacketPlayer) e.packet).onGround = false;
                    ((C03PacketPlayer) e.packet).y = -(((C03PacketPlayer) e.packet).y*((C03PacketPlayer) e.packet).y);

                    expectedTeleport = true;
                    toCancel++;
                    toCancel++;
                }

                if(mc.thePlayer.ticksExisted % 3 == 0) {
                    ((C03PacketPlayer) e.packet).moving = false;
                }

                if(mc.thePlayer.ticksExisted % 5 == 0) {
                    PacketUtil.sendSilent(new C0CPacketInput());
                }
            }

        }

        if(mode.is("Vulcan't")) {

            if(e.packet instanceof S08PacketPlayerPosLook) {
                vulcanTimer2.reset();

            //    if(ModuleManager.getModuleClass(Speed.class).getState()) {
            //        ModuleManager.getModuleClass(Speed.class).setState(false);
            //        NotificationManager.addNoti("Disabler", "Disabled speed to attempt to stop lagbacks!", NotificationType.WARNING, 1500L);
            //    }

                expectedTeleport = true;
            }

            if(e.packet instanceof C0FPacketConfirmTransaction) {
                transactions.add(((C0FPacketConfirmTransaction) e.packet));
                e.cancel();
            }

            if(e.packet instanceof C00PacketKeepAlive) {
                ((C00PacketKeepAlive) e.packet).key = new Random().nextInt();

                if(expectedTeleport) {
                    e.cancel();
                }
            }

        }

        if(mode.is("Minemora")) {
            if(e.packet instanceof S08PacketPlayerPosLook) {
                expectedTeleport = true;
                PacketUtil.sendSilent(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
            }

            if(e.packet instanceof C0FPacketConfirmTransaction) {
                ((C0FPacketConfirmTransaction) e.packet).uid -= 1;
            }

            if(e.packet instanceof C00PacketKeepAlive) {
                ((C00PacketKeepAlive) e.packet).key = new Random().nextInt();

                if(expectedTeleport) {
                    e.cancel();
                }
            }
        }

    }

    public boolean shouldNotRun() {
        if (this.mc.thePlayer == null) {
            return true;
        }
        if(mc.isSingleplayer()) {
            return true;
        }
        if (this.mc.thePlayer.inventory == null) {
            return true;
        }
        if (this.mc.thePlayer.inventory.hasItem(Items.compass)) {
            return true;
        }
        return this.mc.thePlayer == null || this.mc.thePlayer.ticksExisted <= 5;
    }

    public double moveDif() {
        return Math.abs(mc.thePlayer.lastTickPosX - mc.thePlayer.posX)+
                Math.abs(mc.thePlayer.lastTickPosY - mc.thePlayer.posY)+
                        Math.abs(mc.thePlayer.lastTickPosZ - mc.thePlayer.posZ);
    }

    public void sendPredictPacket() {
        sendRelativePacket(-(mc.thePlayer.lastTickPosX - mc.thePlayer.posX),
                -(mc.thePlayer.lastTickPosY - mc.thePlayer.posY),
                -(mc.thePlayer.lastTickPosZ - mc.thePlayer.posZ));
    }

    public void sendRelativePacket(double x, double y, double z) {
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, true));
    }
}
