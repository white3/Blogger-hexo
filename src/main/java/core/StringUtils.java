package core;

import java.util.regex.Pattern;

public class StringUtils {
    static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

    public static String escapeSpecialRegexChars(String str) {
        return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
    }

    public static Pattern toSafePattern(String text) {
        return Pattern.compile(".*" + escapeSpecialRegexChars(text) + ".*");
    }

    static Pattern GENERAL_SEPARATOR = Pattern.compile("\\\\");

    public static String correctPath(String path) {
        return GENERAL_SEPARATOR.matcher(path).replaceAll("/");
    }

    static Pattern PEEL_EXTENSION = Pattern.compile("\\.[^\\./]+?$");

    public static String peelExtension(String str) {
        return PEEL_EXTENSION.matcher(str).replaceFirst("");
    }


}
