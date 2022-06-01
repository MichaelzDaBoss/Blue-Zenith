package cat.ui.clickgui.component;

import java.util.ArrayList;

public interface Expandable {

    void setExpanded(boolean expanded);
    boolean isExpanded();
    ArrayList<Component> getComponents();

}
