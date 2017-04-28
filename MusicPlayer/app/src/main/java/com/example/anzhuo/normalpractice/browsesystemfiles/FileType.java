package com.example.anzhuo.normalpractice.browsesystemfiles;

/**
 * Created by anzhuo on 2016/7/29.
 */
public class FileType {
    private int image;
    private String fileName;
    private int type;
    private String filePath;
    static int TYPE_CHECKED=1;
    static int TYPE_UNCHECKED=-1;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
