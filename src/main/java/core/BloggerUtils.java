package core;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static core.ErrorHandle.handleError;
import static core.StringUtils.correctPath;
import static core.StringUtils.escapeSpecialRegexChars;

public class BloggerUtils {

    /**
     * 移动存在markdown文件的项目目录、笔记目录等
     *
     * @param markdown          markdown文件
     * @param sourceRootPattern 转化的根目录的绝对路径, 形如 /home/mz3/CSAPP
     */
    static Markdown move(Markdown markdown, Pattern sourceRootPattern, String objectName) {
        // Is it publishing?
        markdown.setDoPublish(StringUtils.isPublish(markdown.getContent()));

        if (!markdown.isDoPublish()) {
            return markdown;
        }

        /**
         * 获取相对路径, 例如
         * "/home/mz3/Desktop/CSAPP/first/1" -> "first/1" = relativePath
         */
        Matcher tempMatcher = sourceRootPattern.matcher(markdown.getFilePathWithoutExtension());
        if (!tempMatcher.find()) {
            handleError("can't move " + markdown.getFilePathWithoutExtension());
            return markdown;
        }
        String relativePath = objectName + tempMatcher.group(1);
        System.out.println(relativePath);

        // move images' directory
        List<String> images = null;
        if (!(images = replaceImagePath(markdown, relativePath)).isEmpty()) {
            images.forEach((String image) -> {
                try {
                    // System.out.println(image);
                    FileUtils.copyFileToDirectory(
                            new File(markdown.getDirectory() + image),
                            new File(Constant.BLOG_ABSOLUTE_PATH + Constant.BLOG_IMAGE_RELATIVE_ROOT
                                    + relativePath + Constant.IMAGES_DIRECTORY_SUFFIX));
                } catch (Exception e) {
                    handleError(e);
                }
            });
        }

        markdown.setFile(new File(Constant.BLOG_ABSOLUTE_POST_ROOT + relativePath + Markdown.ends));

        // move markdown
        markdown.doWrite();

        return markdown;
    }

    /**
     * 匹配图片路径, 截断扣除
     * "123432![WindowsTerminal](0002.terminal改造.assets/image-20200721161709980.png)324234" ->
     * {"![WindowsTerminal](0002.terminal改造", "![WindowsTerminal]", "0002.terminal改造", "/image-20200721161709980.png"}
     */
    static final Pattern imagePattern = Pattern.compile("((\\!\\[.*?\\])\\((.*?))\\.assets(.*)\\)", Pattern.MULTILINE);

    /**
     * @param markdown     markdown文件
     * @param relativePath 相对于项目目录的相对路径
     * @return 图片名称的列表, 形如: learn/01.png
     */
    private static List<String> replaceImagePath(Markdown markdown, String relativePath) {
        String result = markdown.getContent();
        boolean flag = false;
        LinkedList<String> images = new LinkedList<>();

        Matcher matcher = imagePattern.matcher(result);
        while (matcher.find()) {
            result = result.replace(matcher.group(1),
                    matcher.group(2) + "(" + Constant.BLOG_IMAGE_RELATIVE_ROOT + relativePath);
            images.add(correctPath(Constant.FILE_SEPARATE + matcher.group(3) + Constant.IMAGES_DIRECTORY_SUFFIX + matcher.group(4)));
            flag = true;
        }

        if (flag) {
            markdown.setContent(result);
            markdown.setHasImage(true);
        }
        return images;
    }
}

