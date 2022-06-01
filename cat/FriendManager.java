package cat;

import java.util.ArrayList;

public class FriendManager {

    public static ArrayList<String> friends = new ArrayList<>();

    public static void add(String name) {
        friends.add(name.toLowerCase());
    }

    public static void remove(String name) {
        friends.remove(name.toLowerCase());
    }

    public static boolean is(String name) {
        return friends.contains(name.toLowerCase());
    }

}
