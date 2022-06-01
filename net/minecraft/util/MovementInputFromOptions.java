package net.minecraft.util;

import cat.BlueZenith;
import cat.events.impl.MoveButtonEvent;
import cat.util.Button;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        final MoveButtonEvent event = new MoveButtonEvent(new Button(this.gameSettings.keyBindLeft.isKeyDown(), 90),
                new Button(this.gameSettings.keyBindRight.isKeyDown(), -90), new Button(this.gameSettings.keyBindBack.isKeyDown(), 180),
                new Button(this.gameSettings.keyBindForward.isKeyDown(), 0), this.gameSettings.keyBindSneak.isKeyDown(), this.gameSettings.keyBindJump.isKeyDown());
        BlueZenith.eventManager.call(event);

        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (event.getForward().isButton()) {
            ++this.moveForward;
        }

        if (event.getBackward().isButton()) {
            --this.moveForward;
        }

        if (event.getLeft().isButton()) {
            ++this.moveStrafe;
        }

        if (event.getRight().isButton()) {
            --this.moveStrafe;
        }

        this.jump = event.isJump();
        this.sneak = event.isSneak();

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}
