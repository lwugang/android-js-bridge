//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wugang.jsbridge.library;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageItem implements Serializable, Parcelable {
  public String name;
  public String path;
  public long size;
  public int width;
  public int height;
  public String mimeType;
  public long addTime;
  public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
    public ImageItem createFromParcel(Parcel source) {
      return new ImageItem(source);
    }

    public ImageItem[] newArray(int size) {
      return new ImageItem[size];
    }
  };

  public boolean equals(Object o) {
    if (!(o instanceof ImageItem)) {
      return super.equals(o);
    } else {
      ImageItem item = (ImageItem) o;
      return this.path.equalsIgnoreCase(item.path) && this.addTime == item.addTime;
    }
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.path);
    dest.writeLong(this.size);
    dest.writeInt(this.width);
    dest.writeInt(this.height);
    dest.writeString(this.mimeType);
    dest.writeLong(this.addTime);
  }

  public ImageItem() {
  }

  protected ImageItem(Parcel in) {
    this.name = in.readString();
    this.path = in.readString();
    this.size = in.readLong();
    this.width = in.readInt();
    this.height = in.readInt();
    this.mimeType = in.readString();
    this.addTime = in.readLong();
  }

  public static ImageItem create(com.lzy.imagepicker.bean.ImageItem imageItem) {
    ImageItem imageItem1 = new ImageItem();
    imageItem1.addTime = imageItem.addTime;
    imageItem1.height = imageItem.height;
    imageItem1.mimeType = imageItem.mimeType;
    imageItem1.name = imageItem.name;
    imageItem1.path = imageItem.path;
    imageItem1.size = imageItem.size;
    imageItem1.width = imageItem.width;
    return imageItem1;
  }

  public static List<ImageItem> create(List<com.lzy.imagepicker.bean.ImageItem> imageItems) {
    List<ImageItem> imageItemList = new ArrayList<>();
    if (imageItems != null && !imageItems.isEmpty()) {
      for (int i = 0; i < imageItems.size(); i++) {
        imageItemList.add(create(imageItems.get(i)));
      }
    }
    return imageItemList;
  }
}
