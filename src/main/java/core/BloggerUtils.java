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
    private static final Pattern markdownHeadPattern = Pattern.compile("---([\\s\\S]*?)---", Pattern.MULTILINE);
    private static final Pattern publishPattern = Pattern.compile("^publish: (true|false)", Pattern.MULTILINE);
    private static final Pattern imagePattern = Pattern.compile("((\\!\\[.*?\\])\\(.*?)\\.assets(.*)\\)", Pattern.MULTILINE);

    /**
     *
     * 移动存在markdown文件的项目目录、笔记目录等
     * @param markdown markdown文件
     * @param sourceRootPattern 转化的根目录的绝对路径, 形如 /home/mz3/CSAPP
     */
    static void move(Markdown markdown, Pattern sourceRootPattern) {
        // Is it publishing?
        Matcher mdHeadMatcher = markdownHeadPattern.matcher(markdown.getContent());
        if (mdHeadMatcher.find()) {
            Matcher matcher = publishPattern.matcher(mdHeadMatcher.group(1));
            if (matcher.find()) {
                markdown.setDoPublish(matcher.group(1).equals("true"));
            }
        }
        if (!markdown.isDoPublish())
            return;

        Matcher tempMatcher = sourceRootPattern.matcher(markdown.getFilePathWithoutExtension());
        if (!tempMatcher.find()) {
            handleError("can't move " + markdown.getFilePathWithoutExtension());
            return;
        }
        String relativePath = tempMatcher.group(1);

        // move images' directory
        List<String> images = null;
        if (!(images = replaceImagePath(markdown, relativePath)).isEmpty()) {
//            try {
                Stream<String> imageStream = images.parallelStream();
                imageStream.forEach((String image) -> {
                    try {
                        System.out.println(image);
                        FileUtils.copyFileToDirectory(
                                new File(markdown.getFilePathWithoutExtension() + ".assets" + image),
                                new File(Constant.BLOG_ABSOLUTE_PATH + Constant.BLOG_IMAGE_RELATIVE_ROOT
                                + relativePath + ".assets/"));
                    } catch (Exception e) {
                        handleError(e);
                    }
                });
//                FileUtils.copyDirectory(new File(markdown.getFilePathWithoutExtension() + ".assets/"),
//                        new File(Constant.BLOG_ABSOLUTE_PATH + Constant.BLOG_IMAGE_RELATIVE_ROOT
//                                + relativePath + ".assets/"));
//            } catch (IOException e) {
//                handleError(e);
//            }
        }

        markdown.setFile(new File(Constant.BLOG_ABSOLUTE_POST_ROOT + relativePath + Markdown.ends));

        // move markdown
        markdown.doWrite();
    }

    /**
     *
     * @param markdown     markdown文件
     * @param relativePath 相对于项目目录的相对路径
     * @return 图片名称的列表, 形如: /01.png
     */
    private static List<String> replaceImagePath(Markdown markdown, String relativePath) {
        String result = markdown.getContent();
        Matcher matcher = imagePattern.matcher(result);
        boolean flag = false;
        LinkedList<String> images = new LinkedList<>();

        while (matcher.find()) {
            result = result.replace(matcher.group(1),
                    correctPath(matcher.group(2) + "(" + Constant.BLOG_IMAGE_RELATIVE_ROOT + relativePath));
            images.add(correctPath(matcher.group(3)));
            flag = true;
        }
        if (flag)
            markdown.setContent(result);
        return images;
    }
}

