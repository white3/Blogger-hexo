package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.Stream;

import static core.StringUtils.correctPath;
import static core.ErrorHandle.handleError;

public class Constant {
    static final boolean PRINT_FILE;
    static final String FILE_SEPARATE = "/";
    static final String GLOBAL_CONFIG_FILE = "hexo-blogger.properties";  // 此处输入文件名
    static final String MARKDOWN_CHARSET;
    static final Stream<String> SOURCE_ABSOLUTE_POST_PATH_STREAM;
    static final String BLOG_ABSOLUTE_PATH;
    static final String BLOG_ABSOLUTE_IMAGE_ROOT;
    static final String BLOG_ABSOLUTE_POST_ROOT;
    static final String BLOG_IMAGE_RELATIVE_ROOT;
    static final String BLOG_POST_RELATIVE_ROOT;
    static final boolean PUBLISH_DEFAULT_VALUE;
    static final String IMAGES_DIRECTORY_SUFFIX;

    static {
        Properties globalConf = new Properties();
        try {
            globalConf.load(new FileInputStream(new File(GLOBAL_CONFIG_FILE)));
        } catch (IOException e) {
            handleError(e);
            handleError("[-] failed to load config!");
            System.exit(-1);
        }
        SOURCE_ABSOLUTE_POST_PATH_STREAM = Stream.of(globalConf.getProperty("SOURCE_POST_PATH").split("[ ]*\\|[ ]*")).map(StringUtils::correctPath);

        PUBLISH_DEFAULT_VALUE = globalConf.getProperty("default-publish").toLowerCase().equalsIgnoreCase("true");
        PRINT_FILE = globalConf.getProperty("PRINT_FILE").equalsIgnoreCase("true");

        IMAGES_DIRECTORY_SUFFIX = correctPath(globalConf.getProperty("IMAGES_DIRECTORY_SUFFIX"));
        BLOG_ABSOLUTE_PATH = correctPath(globalConf.getProperty("BLOG_PATH"));
        MARKDOWN_CHARSET = correctPath(globalConf.getProperty("MARKDOWN_CHARSET"));
        BLOG_IMAGE_RELATIVE_ROOT = correctPath(globalConf.getProperty("BLOG_IMAGE_ABSTRACT_ROOT"));
        BLOG_POST_RELATIVE_ROOT = correctPath(globalConf.getProperty("BLOG_POST_ABSTRACT_ROOT"));
        BLOG_ABSOLUTE_IMAGE_ROOT = correctPath(BLOG_ABSOLUTE_PATH + BLOG_IMAGE_RELATIVE_ROOT);
        BLOG_ABSOLUTE_POST_ROOT = correctPath(BLOG_ABSOLUTE_PATH + BLOG_POST_RELATIVE_ROOT);
        System.out.println("[+] success to load config !");
    }
}
