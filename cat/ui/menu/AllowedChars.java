package cat.ui.menu;

public class AllowedChars
{
    /**
     * Array of the special characters that are allowed in any text drawing of
     * Minecraft.
     */
    public static final char[] allowedCharactersArray = new char[]{'/', '\n',
            '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"',
            ':'};

    public static boolean isAllowedCharacter(char character)
    {
        return character >= 32 && character != 127;
    }

    /**
     * Filter string by only keeping those characters for which
     * isAllowedCharacter() returns true.
     */
    public static String filterAllowedCharacters(String input)
    {
        StringBuilder var1 = new StringBuilder();
        char[] var2 = input.toCharArray();

        for (char var5 : var2) {
            if (isAllowedCharacter(var5))
                var1.append(var5);
        }

        return var1.toString();
    }
}
