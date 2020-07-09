package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

import static core.ErrorHandle.handleError;
import static core.StringUtils.correctPath;

public class Blogger extends Thread {
    private Pattern sourceRootPattern;
    private File sourceAbsolutePostPath;

    public Blogger(String sourceAbsolutePostPath) {
        sourceRootPattern = Pattern.compile(StringUtils.escapeSpecialRegexChars(correctPath(sourceAbsolutePostPath)) + "(.*)");
        this.sourceAbsolutePostPath = new File(sourceAbsolutePostPath);
    }

    public static void main(String[] args) {
        Constant.SOURCE_ABSOLUTE_POST_PATH_STREAM.forEach(sourceAbsolutePostPath -> new Blogger(sourceAbsolutePostPath).start());
    }

    @Override
    public void run() {
        System.out.println("[+] Start to copy " + this.sourceAbsolutePostPath);
        try {
            Files.walk(this.sourceAbsolutePostPath.toPath())
                    // 过滤 markdown 文件
                    .filter(path -> path.toFile().isFile() && path.toString().endsWith(".md"))
                    // File -> Markdown
                    .map(Markdown::new)
                    // 开始执行博客下 markdown 生成
                    .peek(markdown -> BloggerUtils.move(markdown, sourceRootPattern))
                    // 过滤掉未上传的文件, 再输出
                    .filter(Markdown::isDoPublish)
                    // 输出日志
                    .forEach(markdown -> {
                        if (Constant.PRINT_FILE)
                            System.out.println(markdown);
                    });
            System.out.println("[+] success to copy " + this.sourceAbsolutePostPath);
        } catch (Exception e) {
            handleError(e);
        }
    }
}
