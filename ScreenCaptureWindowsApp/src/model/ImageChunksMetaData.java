package model;

import java.io.Serializable;

public class ImageChunksMetaData implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    private int imageNo = 0;
    private String imageName = "";
    private long imageSize = 0l;

    public int getImageNo() {
        return imageNo;
    }

    public void setImageNo(int imageNo) {
        this.imageNo = imageNo;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public long getImageSize() {
        return imageSize;
    }

    public void setImageSize(long imageSize) {
        this.imageSize = imageSize;
    }

    @Override
    public String toString() {
        return new StringBuffer("image_number = ")
                .append(getImageNo())
                .append("\n")
                .append("image_name = ")
                .append(getImageName())
                .append("\n")
                .append("image_size = ")
                .append(getImageSize())
                .append("\n")
                .toString();
    }
}
