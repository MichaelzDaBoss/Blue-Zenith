package cat.util;

public class MousePos {

    public int mouseX, mouseY;
    public float partialTicks;

    public MousePos(int mouseX, int mouseY, float partialTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
    }
}
