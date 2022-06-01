package cat.module;

public enum ModuleCategory {
    COMBAT("Combat"),
    FUN("Fun"),
    MOVEMENT("Movement"),
    MISC("Misc"),
    PLAYER("Player"),
    HVH("HvH/Botting"),
    RENDER("Render");
   //WORLD("World");
    public String displayName;
    public boolean showContent;
    ModuleCategory(String displayName) {
        this.displayName = displayName;
        this.showContent = false;
    }
}
