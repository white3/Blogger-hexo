package core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringUtils {
    static final Pattern IMAGE_PATTERN = Pattern.compile("((\\!\\[.*?\\])\\((.*?))\\.assets(.*)\\)", Pattern.MULTILINE);

//    public static void main(String[] args) {
//        String[] s = new String[]{
//                "![WindowsTerminal](afafs/0002.terminal改造.assets/image-20200721161709980.png)",
//                "123432![WindowsTerminal](0002.terminal改造.assets/image-20200721161709980.png)324234",
//                "![Windows/Terminal](0002.terminal改造.assets/image-20200721161709980.png)",
//                "D:/note/menzel3.fun"
//        };
//        // Stream.of(s).forEach(StringUtils::getNameWithSuffix);
//        Stream.of(s).map(StringUtils::correctPath).map(StringUtils::getNameWithSuffix).forEach(System.out::println);
//    }

    /**
     * 匹配图片路径, 截断扣除
     * "123432![WindowsTerminal](0002.terminal改造.assets/image-20200721161709980.png)324234" ->
     * {"![WindowsTerminal](0002.terminal改造", "![WindowsTerminal]", "0002.terminal改造", "/image-20200721161709980.png"}
     *
     * @param markdownContent
     * @return
     */
    public static String[] markdownImageLinkSeparate(String markdownContent) {
        Matcher matcher = IMAGE_PATTERN.matcher(markdownContent);
        if (matcher.find())
            return new String[]{matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4)};
        return null;
    }

    static final Pattern NAME_WITH_SUFFIX_PATTERN = Pattern.compile("/?([^/]+)/?$");

    /**
     * 从绝对路径提取处文件名称
     * 例如: 从 C:/ha_ha_ha/filename 提取出 filename
     *
     * @param s
     * @return
     */
    public static String getNameWithSuffix(String s) {
        Matcher matcher = NAME_WITH_SUFFIX_PATTERN.matcher(s);
        if (matcher.find())
            return matcher.group(1);
        return s;
    }

    public static final Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

    /**
     * 将字符串的正则关键字符转义
     *
     * @param str
     * @return
     */
    public static String escapeSpecialRegexChars(String str) {
        return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
    }

    /**
     * 将字符串转化为正则字符串, 将一些正则关键字符转义, 并编译为pattern对象
     *
     * @param text
     * @return
     */
    public static Pattern toSafePattern(String text) {
        return Pattern.compile(".*" + escapeSpecialRegexChars(text) + ".*");
    }

    static Pattern GENERAL_SEPARATOR = Pattern.compile("\\\\");

    /**
     * 修正文件路径的斜杠，全部转化为 /
     *
     * @param path
     * @return
     */
    public static String correctPath(String path) {
        return GENERAL_SEPARATOR.matcher(path).replaceAll("/");
    }

    static Pattern PEEL_EXTENSION = Pattern.compile("\\.[^\\./]+?$");

    /**
     * 去除文件后缀
     *
     * @param str
     * @return
     */
    public static String peelExtension(String str) {
        return PEEL_EXTENSION.matcher(str).replaceFirst("");
    }

    static final Pattern markdownHeadPattern = Pattern.compile("---([\\s\\S]*?)---", Pattern.MULTILINE);
    static final Pattern publishPattern = Pattern.compile("^publish: (true|false)", Pattern.MULTILINE);

    /**
     * 判断content中是否有 publish: true
     *
     * @param content
     * @return
     */
    static boolean isPublish(String content) {
        Matcher mdHeadMatcher = markdownHeadPattern.matcher(content);
        if (mdHeadMatcher.find()) {
            Matcher matcher = publishPattern.matcher(mdHeadMatcher.group(1));
            if (matcher.find()) {
                return matcher.group(1).toLowerCase().equalsIgnoreCase("true");
            }
        }
        return Constant.PUBLISH_DEFAULT_VALUE;
    }
}
