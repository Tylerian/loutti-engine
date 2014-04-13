package net.luotti.engine.communication.codecs;

public class BinaryDecoder {

    private static final int MIN_NUMBER_VAL			= 32;
    private static final int MAX_NUMBER_VAL			= 60;

    private static final int MIN_CHAR_UPPERCASE_VAL	= 65;
    private static final int MAX_CHAR_UPPERCASE_VAL	= 90;

    private static final int MIN_CHAR_LOWERCASE_VAL	= 97;
    private static final int MAX_CHAR_LOWERCASE_VAL	= 122;

    public static String parse(String input)
    {
        StringBuilder builder = new StringBuilder(input.length());

        for (int i = 0, k = 0; i < input.length(); i++)
        {
            k = input.charAt(i);
            if (k >= MIN_NUMBER_VAL && k <= MAX_NUMBER_VAL) builder.append(k);
            else if (k >= MIN_CHAR_LOWERCASE_VAL && k <= MAX_CHAR_LOWERCASE_VAL) builder.append(k);
            else if (k >= MIN_CHAR_UPPERCASE_VAL && k <= MAX_CHAR_UPPERCASE_VAL) builder.append(k);
            else builder.append(String.format("[%s]", (int) k));
        }

        return builder.toString();
    }
}
