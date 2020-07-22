package core;

import org.apache.commons.io.FileUtils;

import static core.ErrorHandle.handleError;
import static core.StringUtils.correctPath;
import static core.StringUtils.peelExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Markdown {
    public final static String ends = ".md";
    private File file;
    private String filePathWithoutExtension;
    private String content = null;
    private boolean hasImage;
    private boolean doPublish = true;

    public Markdown(File file) {
        this.file = file;
    }

    public Markdown(String file) {
        this.file = new File(file);
    }

    public Markdown(Path path) {
        this.file = path.toFile();
        this.filePathWithoutExtension = correctPath(peelExtension(path.toString()));
    }

    @Override
    public String toString() {
        return "\nMarkdown{" +
                "\n  file=" + file.getPath() +
                ",\n  hasImage=" + hasImage +
                "\n}";
    }

    public void doRead() {
        try {
            this.content = FileUtils.readFileToString(this.file, Constant.MARKDOWN_CHARSET);
        } catch (IOException e) {
            handleError(e);
        }
    }

    public void doWrite() {
        try {
            FileUtils.writeStringToFile(this.file, this.content, Constant.MARKDOWN_CHARSET);
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void setFilePathWithoutExtension(String filePathWithoutExtension) {
        this.filePathWithoutExtension = correctPath(filePathWithoutExtension);
        this.setFile(new File(this.filePathWithoutExtension));
    }

    public void setDoPublish(boolean doPublish) {
        this.doPublish = doPublish;
    }

    public void setFile(File file) {
        this.filePathWithoutExtension = correctPath(peelExtension(file.getPath()));
        this.file = file;
    }

    public String getFilePathWithoutExtension() {
        return filePathWithoutExtension;
    }

    public boolean isDoPublish() {
        return doPublish;
    }

    public File getFile() {
        return file;
    }

    public String getContent() {
        if (content == null)
            doRead();
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getDirectory() {
        return this.file.getAbsoluteFile().getParent();
    }
}
