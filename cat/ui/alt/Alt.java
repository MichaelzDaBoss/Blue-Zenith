package cat.ui.alt;

public class Alt {

    public String email;
    public String password;
    public String username;
    public AltType type;

    public Alt(String email, String password, AltType type) {
        this.email = email;
        this.password = password;
        this.type = type;
        this.username = "Unknown";
    }

    public Alt(String username) {
        this.username = username;
        this.type = AltType.OFFLINE;
    }



}
