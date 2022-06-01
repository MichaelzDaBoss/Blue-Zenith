package cat.events.impl;

import cat.events.Event;
import cat.util.Button;

public class MoveButtonEvent extends Event {
    private Button left;
    private Button right;
    private Button backward;
    private Button forward;
    private boolean sneak;
    private boolean jump;

    public MoveButtonEvent(Button left, Button right, Button backward, Button forward, boolean sneak, boolean jump) {
        this.left = left;
        this.right = right;
        this.backward = backward;
        this.forward = forward;
        this.sneak = sneak;
        this.jump = jump;
    }

    public Button getLeft() {
        return left;
    }

    public void setLeft(Button left) {
        this.left = left;
    }

    public Button getRight() {
        return right;
    }

    public void setRight(Button right) {
        this.right = right;
    }

    public Button getBackward() {
        return backward;
    }

    public void setBackward(Button backward) {
        this.backward = backward;
    }

    public Button getForward() {
        return forward;
    }

    public void setForward(Button forward) {
        this.forward = forward;
    }

    public boolean isSneak() {
        return sneak;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setForward(final boolean forward) {
        this.getForward().button = forward;
    }

    public void setBackward(final boolean backward) {
        this.getBackward().button = backward;
    }

    public void setLeft(final boolean left) {
        this.getLeft().button = left;
    }

    public void setRight(final boolean right) {
        this.getRight().button = right;
    }
}
