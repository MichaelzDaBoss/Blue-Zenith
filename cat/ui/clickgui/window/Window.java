package cat.ui.clickgui.window;

public abstract class Window {

    public static final double width = 100, height = 14, maxHeight = 300;

    public double x, y;
    public boolean expanded = false;

    public Window(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract void drawPre(int mouseX, int mouseY);
    public abstract void drawPost(int mouseX, int mouseY);
    public abstract void mouseClicked(int mouseX, int mouseY, int button);
    public abstract void mouseReleased(int mouseX, int mouseY, int button);
    public abstract void mouseMoved(int mouseX, int mouseY);
    public abstract void keyTyped(char typedChar, int keyCode);

}
