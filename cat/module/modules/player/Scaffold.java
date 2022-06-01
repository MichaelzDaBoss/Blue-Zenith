package cat.module.modules.player;

import cat.events.impl.*;
import cat.module.ModuleManager;
import cat.module.modules.misc.Disabler;
import cat.module.value.types.ModeValue;
import cat.util.AngleUtility;
import cat.util.MillisTimer;
import cat.util.RotationUtil;
import cat.util.scaffold.BlockData;
import com.google.common.eventbus.Subscribe;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {
	public static List<Block> blacklist;
	private BlockData blockData = null;
	private BlockData lastBlockData;
	private final MillisTimer placeTime = new MillisTimer();
	private boolean rotated;

	public static AngleUtility angleUtility = new AngleUtility(110, 120, 30, 40);
	public static List<Block> blacklistedBlocks;
	public static MillisTimer isScaffolding = new MillisTimer();
	private MillisTimer placeTimer = new MillisTimer();
	private MillisTimer clickTimer = new MillisTimer();
	BlockData data = null;
	Vec3 lastVec3;
	int ticks;
	int slots;
	float[] curRotate;
	int keepRotationTicks;

	boolean doShit;
	float lastYaw;
	float[] lastRot;
	int hotbar;

	public final ModeValue mode = new ModeValue("Mode", "Bypass", true, null, "Bypass", "Verus", "Hypixel");

	public final BooleanValue safewalk = new BooleanValue("SafeWalk", true, true, __ -> mode.is("Bypass"));
	public final BooleanValue slow = new BooleanValue("Slow", true, true, __ -> mode.is("Bypass"));

	public final BooleanValue boost = new BooleanValue("Boost", true, true, __ -> mode.is("Verus"));

	public final FloatValue timerBoost = new FloatValue("Timer Boost", 1, 1, 2, 0.1f, true, __ -> mode.is("Hypixel"));

	public BooleanValue sprint = new BooleanValue("Sprint", true, true, null);
	public BooleanValue noZero = new BooleanValue("No 0 Items", true, true, null);

	@Override
	public String getTag() {
		return mode.get();
	}

	public Scaffold() {
		super("Scaffold", "", ModuleCategory.PLAYER, Keyboard.KEY_B, "BlockFly", "FeetPlace");
		blacklist = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava,
				Blocks.flowing_lava, Blocks.tnt, Blocks.sand, Blocks.enchanting_table, Blocks.beacon,
				Blocks.noteblock, Blocks.sand, Blocks.chest, Blocks.gravel, Blocks.ender_chest);
	}

	@Override
	public void onEnable() {

		if(mode.is("Hypixel")) {
			slots = mc.thePlayer.inventory.currentItem;
			curRotate = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch - 0.5f};
			ticks = 0;
			placeTimer.reset();
			((Disabler) ModuleManager.getModuleClass(Disabler.class)).targetTimer = timerBoost.get();
		}

	}

	MillisTimer slowdownTimer = new MillisTimer();

	@Override
	public void onDisable() {
		doShit = false;

		if(mode.is("Hypixel")) {
			((Disabler) ModuleManager.getModuleClass(Disabler.class)).targetTimer = 1.0F;
		}
	}

	@Subscribe
	public void onMove(MoveEvent event) {

		if(!sprint.get()) {
			this.mc.thePlayer.setSprinting(false);
		}

		if(mode.is("Verus")) {
			event.safewalk = true;

			if(boost.get() && mode.is("Verus")) {
				event.z *= 1.1;
				event.x *= 1.1;
			}
		}

		if(mode.is("Bypass")) {
			if(slow.get()) {
				event.x *= 0.9;
				event.z *= 0.9;
			}

			if(safewalk.get()) {

				event.safewalk = true;

				if (!mc.thePlayer.onGround || mc.thePlayer.isCollidedHorizontally || this.mc.thePlayer.movementInput.jump) {
					event.x = 0;
					event.z = 0;
				}

			}
		}

	}

	@Subscribe
	public void onMotion(UpdatePlayerEvent event) {

		if(mode.is("Hypixel")) {

			if(event.isPre()) {
				if (hasBlock()) {
					data = null;
					findBlockData();
					if (data != null) {
						// Rotation
						lastVec3 = data.hitVec;
						rotated = true;
						keepRotationTicks = 0;
					}
				}

				RotationUtil.Rotation rotation = toRotation(lastVec3, false);
				event.setYaw(rotation.getYaw());
				event.setPitch(rotation.getPitch());
			} else {
				if (!rotated)
					return;

				rotated = false;

				hotbar = this.getBlockFromHotbar();

				if (hotbar == -1) // no blocks in hotbar!
					return;

				if (slots != hotbar) {
					mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(hotbar));
				}

				ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(hotbar);


				if (this.data != null && stack != null
						&& stack.getItem() instanceof ItemBlock
						&& stack.stackSize > 0) {
					if (this.placeTimer.hasTimeReached(0)) {
						this.placeTimer.reset();
						mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
						if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, data.position, data.face, data.hitVec)) {
							slowdownTimer.reset();
							mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0APacketAnimation());
						}
						mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
						// if (!noSlowdown.getValueState())
						isScaffolding.reset();
					}
				}
			}
		}

		if(mode.is("Verus")) {
			if(event.isPre()) {

				if(doShit) {
					this.mc.thePlayer.swingItem();
					this.placeBlock(this.blockData.position, this.blockData.face, true);
					doShit = false;
					lastBlockData = blockData;
				}

				event.pitch = (87.4F);
				event.yaw = (lastYaw);

				lastBlockData = blockData;
				this.blockData = null;

				BlockPos blockBelow;

				if (this.mc.thePlayer.onGround) {

					double x2 = Math.cos(Math.toRadians(this.mc.thePlayer.rotationYaw + 90.0F));
					double z2 = Math.sin(Math.toRadians(this.mc.thePlayer.rotationYaw + 90.0F));
					double var18 = (double) this.mc.thePlayer.movementInput.moveForward * 0.4D * x2;
					double xOffset = var18 + (double) this.mc.thePlayer.movementInput.moveStrafe * 0.4D * z2;
					var18 = (double) this.mc.thePlayer.movementInput.moveForward * 0.4D * z2;
					double zOffset = var18 - (double) this.mc.thePlayer.movementInput.moveStrafe * 0.4D * x2;
					double x = this.mc.thePlayer.posX + xOffset;
					double y = this.mc.thePlayer.posY - 1.0D;
					double z = this.mc.thePlayer.posZ + zOffset;
					blockBelow = new BlockPos(x, y, z);

				} else {

					blockBelow = new BlockPos(this.mc.getRenderViewEntity().posX,
							this.mc.getRenderViewEntity().getEntityBoundingBox().minY - 1.0D,
							this.mc.getRenderViewEntity().posZ);

				}

				if (mc.theWorld.getBlockState(blockBelow).getBlock() == Blocks.air) {
					this.blockData = this.getBlockDataLegit(blockBelow);

					if(this.blockData == null) {
						return;
					}

					float[] target;
					target = RotationUtil.getRotations(this.blockData.position, this.blockData.face);

					event.yaw = target[0];
					lastYaw = event.yaw;
					event.pitch = (87.4F);
					lastRot = target;

					rotated = true;
				}

			} else {

				rotated = false;

				if (this.blockData != null && this.mc.thePlayer.inventory.getCurrentItem() != null
						&& this.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBlock
						&& this.mc.thePlayer.inventory.getCurrentItem().stackSize > 0) {
					if (this.placeTime.hasTimeReached(25)) {
						this.placeTime.reset();

						doShit = true;
					}
				}

			}
		}

		if(mode.is("Bypass")) {
			if(event.isPre()) {

				if(doShit) {
					this.mc.thePlayer.swingItem();
					this.placeBlock(this.blockData.position, this.blockData.face, false);
					doShit = false;
				}

				event.pitch = (87.4F);
				event.yaw = (lastYaw);

				this.blockData = null;
				if (!this.mc.thePlayer.isSneaking()) {
					BlockPos blockBelow;
					if (this.mc.thePlayer.onGround) {
						double x2 = Math.cos(Math.toRadians(this.mc.thePlayer.rotationYaw + 90.0F));
						double z2 = Math.sin(Math.toRadians(this.mc.thePlayer.rotationYaw + 90.0F));
						double var18 = (double) this.mc.thePlayer.movementInput.moveForward * 0.4D * x2;
						double xOffset = var18 + (double) this.mc.thePlayer.movementInput.moveStrafe * 0.4D * z2;
						var18 = (double) this.mc.thePlayer.movementInput.moveForward * 0.4D * z2;
						double zOffset = var18 - (double) this.mc.thePlayer.movementInput.moveStrafe * 0.4D * x2;
						double x = this.mc.thePlayer.posX + xOffset;
						double y = this.mc.thePlayer.posY - 1.0D;
						double z = this.mc.thePlayer.posZ + zOffset;
						blockBelow = new BlockPos(x, y, z);
					} else {
						blockBelow = new BlockPos(this.mc.getRenderViewEntity().posX,
								this.mc.getRenderViewEntity().getEntityBoundingBox().minY - 1.0D,
								this.mc.getRenderViewEntity().posZ);
					}

					if (mc.theWorld.getBlockState(blockBelow).getBlock() == Blocks.air) {
						this.blockData = this.getBlockDataLegit(blockBelow);

						if(this.blockData == null) {
							return;
						}

						float[] rots = RotationUtil.getRotations(this.blockData.position, this.blockData.face);
						float yaw = rots[0];

						yaw = Math.max(Math.min(yaw, 180), -180);

						if(Math.abs(yaw - lastYaw) > 30 && Math.abs(yaw - lastYaw) < 150) {
							yaw = getMiddle(lastYaw, yaw);
						}

						yaw = Math.max(Math.min(yaw, 180), -180);

						event.yaw = yaw;
						lastYaw = event.yaw;
						event.pitch = (87.4F);

						rotated = true;
					}
				}
			} else {

				rotated = false;

				if (this.blockData != null && this.mc.thePlayer.inventory.getCurrentItem() != null
						&& this.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBlock
						&& this.mc.thePlayer.inventory.getCurrentItem().stackSize > 0) {
					if (this.placeTime.hasTimeReached((long) (100 + (Math.random() * 50)))) {
						this.placeTime.reset();

						doShit = true;
					}
				}

			}
		}

	}

	@Subscribe
	public void onGameLoop(GameLoopEvent event) {

		if(noZero.get()) {
			if(mc.thePlayer == null || mc.thePlayer.inventory == null || mc.thePlayer.inventory.mainInventory == null)
				return;

			for (int i = 0; i < 8; i++) {
				if (this.mc.thePlayer.inventory.mainInventory[i] != null
						&& this.mc.thePlayer.inventory.mainInventory[i].stackSize <= 0)
					this.mc.thePlayer.inventory.mainInventory[i] = null;
			}
		}

	}

	@Subscribe
	public void onPacket(PacketEvent event) {

		if(mode.is("Hypixel")) {
			if (event.packet instanceof C09PacketHeldItemChange) {
				C09PacketHeldItemChange packet = ((C09PacketHeldItemChange) event.packet);
				slots = packet.getSlotId();
			}
		}

	}

	@Subscribe
	public void onUpdate(UpdateEvent event) {

		if(mode.is("Hypixel")) {
			final int slot = this.getBlockFromInventory();

			if (slot == -1)
				return;

			if (getHotbarBlocksLeft() < 2 && this.getBlockFromInventory() != -1) {
				if (clickTimer.hasTimeReached(200)) {
					this.swap(this.getBlockFromInventory(), findEmptySlot());
					clickTimer.reset();
				}
			}
		}

	}

	private void placeBlock(BlockPos pos, EnumFacing facing, boolean verus) {
		ItemStack heldItem = this.mc.thePlayer.inventory.getCurrentItem();
		if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock))
			return;

		this.mc.thePlayer.inventory.mainInventory[this.mc.thePlayer.inventory.currentItem].stackSize -= 1;

		if(verus) { // 14E bypas
			mc.playerController.onPlayerRightClickVerus(mc.thePlayer, mc.theWorld, heldItem, pos, facing,
					new Vec3(pos.getX(), pos.getY(), pos.getZ()));
		} else {
			mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, heldItem, pos, facing,
					new Vec3(pos.getX(), pos.getY(), pos.getZ()));
		}
	}

	private BlockData getBlockDataLegit(final BlockPos input) {
		BlockPos pos = input;
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
			return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		}

		pos = input.add(-1, 0, 0);

		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		}

		pos = input.add(1, 0, 0);

		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		}

		pos = input.add(0, 0, -1);

		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		}

		pos = input.add(0, 0, 1);

		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}
		if (!Scaffold.blacklist
				.contains(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		}

		return null;
	}

	public static float getMiddle(float a, float b) {
		return (a + b) / 2;
	}

	static {
		blacklistedBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava,
				Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane,
				Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice,
				Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest,
				Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt,
				Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore,
				Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
				Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
				Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook,
				Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom,
				Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand,
				Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web,
				Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence, Blocks.redstone_torch);
	}

	private boolean hasBlock() {
		int BlockInInventory = findBlock(9, 36);
		int BlockInHotbar = findBlock(36, 45);

		if (BlockInInventory == -1 && BlockInHotbar == -1) {
			return false;
		}
		return true;
	}

	private int findBlock(int startSlot, int endSlot) {
		int i = startSlot;
		while (i < endSlot) {
			ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if (stack != null && stack.getItem() instanceof ItemBlock
					&& ((ItemBlock) stack.getItem()).getBlock().isFullBlock()) {
				return i;
			}
			++i;
		}
		return -1;
	}

	public boolean isAirBlock(Block block) {
		return block.getMaterial().isReplaceable() && (!(block instanceof BlockSnow) || block.getBlockBoundsMaxY() <= 0.125D);
	}

	public double[] getExpandCoords(double y) {
		BlockPos underPos = new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ);
		Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
		MovementInput movementInput = mc.thePlayer.movementInput;
		float forward = movementInput.moveForward, strafe = movementInput.moveStrafe, yaw = mc.thePlayer.rotationYaw;
		double xCalc = -999, zCalc = -999, dist = 0, expandDist = 0.0;

		while (!isAirBlock(underBlock)) {
			xCalc = mc.thePlayer.posX;
			zCalc = mc.thePlayer.posZ;
			dist++;
			if (dist > expandDist) dist = expandDist;
			xCalc += (forward * 0.45 * MathHelper.cos((float) Math.toRadians(yaw + 90.0f)) + strafe * 0.45 * MathHelper.sin((float) Math.toRadians(yaw + 90.0f))) * dist;
			zCalc += (forward * 0.45 * MathHelper.sin((float) Math.toRadians(yaw + 90.0f)) - strafe * 0.45 * MathHelper.cos((float) Math.toRadians(yaw + 90.0f))) * dist;
			if (dist == expandDist) break;
			underPos = new BlockPos(xCalc, y, zCalc);
			underBlock = mc.theWorld.getBlockState(underPos).getBlock();
		}

		return new double[]{xCalc, zCalc};
	}

	BlockPos blockBelow;

	public void findBlockData() {
		double yPos = MathHelper.floor_double(mc.thePlayer.posY) - 1;
		boolean air = isAirBlock(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, yPos, mc.thePlayer.posZ)).getBlock());
		double xPos = air ? mc.thePlayer.posX : getExpandCoords(yPos)[0], zPos = air ? mc.thePlayer.posZ : getExpandCoords(yPos)[1];
		blockBelow = new BlockPos(xPos, yPos, zPos);

		boolean setBlockData = mc.theWorld.getBlockState(blockBelow).getBlock().getMaterial().isReplaceable() || mc.theWorld.getBlockState(blockBelow).getBlock() == Blocks.air;
		data = setBlockData ? getBlockData(blockBelow) : null;
	}

	private BlockData getBlockData(BlockPos pos) {
		if (isPosSolid(pos.add(0, -1, 0))) {
			return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos.add(-1, 0, 0))) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos.add(1, 0, 0))) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos.add(0, 0, 1))) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos.add(0, 0, -1))) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos1 = pos.add(-1, 0, 0);

		if (isPosSolid(pos1.add(0, -1, 0))) {
			return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos1.add(-1, 0, 0))) {
			return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos1.add(1, 0, 0))) {
			return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos1.add(0, 0, 1))) {
			return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos1.add(0, 0, -1))) {
			return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos2 = pos.add(1, 0, 0);

		if (isPosSolid(pos2.add(0, -1, 0))) {
			return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos2.add(-1, 0, 0))) {
			return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos2.add(1, 0, 0))) {
			return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos2.add(0, 0, 1))) {
			return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos2.add(0, 0, -1))) {
			return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos3 = pos.add(0, 0, 1);

		if (isPosSolid(pos3.add(0, -1, 0))) {
			return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos3.add(-1, 0, 0))) {
			return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos3.add(1, 0, 0))) {
			return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos3.add(0, 0, 1))) {
			return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos3.add(0, 0, -1))) {
			return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos4 = pos.add(0, 0, -1);

		if (isPosSolid(pos4.add(0, -1, 0))) {
			return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos4.add(-1, 0, 0))) {
			return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos4.add(1, 0, 0))) {
			return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos4.add(0, 0, 1))) {
			return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos4.add(0, 0, -1))) {
			return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos19 = pos.add(-2, 0, 0);

		if (isPosSolid(pos1.add(0, -1, 0))) {
			return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos1.add(-1, 0, 0))) {
			return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos1.add(1, 0, 0))) {
			return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos1.add(0, 0, 1))) {
			return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos1.add(0, 0, -1))) {
			return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos29 = pos.add(2, 0, 0);

		if (isPosSolid(pos2.add(0, -1, 0))) {
			return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos2.add(-1, 0, 0))) {
			return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos2.add(1, 0, 0))) {
			return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos2.add(0, 0, 1))) {
			return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos2.add(0, 0, -1))) {
			return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos39 = pos.add(0, 0, 2);

		if (isPosSolid(pos3.add(0, -1, 0))) {
			return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos3.add(-1, 0, 0))) {
			return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos3.add(1, 0, 0))) {
			return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos3.add(0, 0, 1))) {
			return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos3.add(0, 0, -1))) {
			return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos49 = pos.add(0, 0, -2);

		if (isPosSolid(pos4.add(0, -1, 0))) {
			return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos4.add(-1, 0, 0))) {
			return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos4.add(1, 0, 0))) {
			return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos4.add(0, 0, 1))) {
			return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos4.add(0, 0, -1))) {
			return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos5 = pos.add(0, -1, 0);

		if (isPosSolid(pos5.add(0, -1, 0))) {
			return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos5.add(-1, 0, 0))) {
			return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos5.add(1, 0, 0))) {
			return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos5.add(0, 0, 1))) {
			return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos5.add(0, 0, -1))) {
			return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos6 = pos5.add(1, 0, 0);

		if (isPosSolid(pos6.add(0, -1, 0))) {
			return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos6.add(-1, 0, 0))) {
			return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos6.add(1, 0, 0))) {
			return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos6.add(0, 0, 1))) {
			return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos6.add(0, 0, -1))) {
			return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos7 = pos5.add(-1, 0, 0);

		if (isPosSolid(pos7.add(0, -1, 0))) {
			return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos7.add(-1, 0, 0))) {
			return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos7.add(1, 0, 0))) {
			return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos7.add(0, 0, 1))) {
			return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos7.add(0, 0, -1))) {
			return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos8 = pos5.add(0, 0, 1);

		if (isPosSolid(pos8.add(0, -1, 0))) {
			return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos8.add(-1, 0, 0))) {
			return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos8.add(1, 0, 0))) {
			return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos8.add(0, 0, 1))) {
			return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos8.add(0, 0, -1))) {
			return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos9 = pos5.add(0, 0, -1);

		if (isPosSolid(pos9.add(0, -1, 0))) {
			return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos9.add(-1, 0, 0))) {
			return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos9.add(1, 0, 0))) {
			return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos9.add(0, 0, 1))) {
			return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos9.add(0, 0, -1))) {
			return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
		}
		return null;
	}

	private boolean isPosSolid(BlockPos pos) {
		final Block block = mc.theWorld.getBlockState(pos).getBlock();
		return !blacklistedBlocks.contains(block);
	}

	public RotationUtil.Rotation toRotation(final Vec3 vec, final boolean predict) {
		final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		if (predict)
			eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

		final double diffX = vec.xCoord - eyesPos.xCoord;
		final double diffY = vec.yCoord - eyesPos.yCoord;
		final double diffZ = vec.zCoord - eyesPos.zCoord;

		return new RotationUtil.Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
				MathHelper.wrapAngleTo180_float(
						(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
	}

	private int getBlockFromInventory() {
		int biggest = 0;
		int biggestSlot = -1;
		for (int i = 9; i < 36; ++i) {
			final ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];

			if (!isScaffoldBlock(itemStack))
				continue;

			if (biggest < itemStack.stackSize) {
				biggest = itemStack.stackSize;
				biggestSlot = i;
			}
		}

		return biggestSlot;
	}

	public static boolean isScaffoldBlock(ItemStack itemStack) {
		if (itemStack == null)
			return false;

		if (itemStack.stackSize <= 0)
			return false;

		if (!(itemStack.getItem() instanceof ItemBlock))
			return false;

		ItemBlock itemBlock = (ItemBlock) itemStack.getItem();

		// whitelist
		if (itemBlock.getBlock() == Blocks.glass)
			return true;

		// only fullblock
		if (!itemBlock.getBlock().isFullBlock())
			return false;

		return true;
	}

	private int getHotbarBlocksLeft() {
		return getHotbarContent().stream().filter(Scaffold::isScaffoldBlock)
				.mapToInt(itemStack -> itemStack.stackSize).sum();
	}

	public List<ItemStack> getHotbarContent() {
		List<ItemStack> result = new ArrayList<>();
		result.addAll(Arrays.asList(mc.thePlayer.inventory.mainInventory).subList(0, 9));
		return result;
	}

	private void swap(final int slot, final int hotbarNum) {
		this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2,
				this.mc.thePlayer);
	}

	public int findEmptySlot() {
		for (int i = 0; i < 8; i++) {
			if (mc.thePlayer.inventory.mainInventory[i] == null)
				return i;
		}

		return mc.thePlayer.inventory.currentItem + (mc.thePlayer.inventory.getCurrentItem() == null ? 0
				: ((mc.thePlayer.inventory.currentItem < 8) ? 1 : -1));
	}

	int lastSlot;
	MillisTimer blockTimer = new MillisTimer();

	private int getBlockFromHotbar() {
		if (blockTimer.hasTimeReached(5000) || mc.thePlayer.inventory.getStackInSlot(hotbar).stackSize < 2) {
			int biggest = 0;
			int biggestSlot = -1;
			for (int i = 0; i < 9; ++i) {
				final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];

				if (!isScaffoldBlock(itemStack))
					continue;

				if (biggest < itemStack.stackSize) {
					biggest = itemStack.stackSize;
					biggestSlot = i;
				}
			}
			blockTimer.reset();
			lastSlot = biggestSlot;
			return biggestSlot;
		} else {
			return lastSlot;
		}
	}


}
