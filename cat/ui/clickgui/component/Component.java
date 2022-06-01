package cat.ui.clickgui.component;

public abstract class Component {
    public static final double width = 100, height = 14;

    public abstract void drawPre(double x, double y, int mouseX, int mouseY);
    public abstract void drawPost(double x, double y, int mouseX, int mouseY);
    public abstract void mouseClicked(double x, double y, int mouseX, int mouseY, int button);
    public abstract void mouseReleased(double x, double y, int mouseX, int mouseY, int button);
    public abstract void mouseMoved(double x, double y, int mouseX, int mouseY);
    public abstract void keyTyped(char typedChar, int keyCode);

}
