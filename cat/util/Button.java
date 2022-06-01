package cat.util;

public class Button {
    public boolean button;
    public double offset;

    public Button(boolean button, double offset) {
        this.button = button;
        this.offset = offset;
    }

    public boolean isButton() {
        return button;
    }

    public void setButton(boolean button) {
        this.button = button;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }
}
