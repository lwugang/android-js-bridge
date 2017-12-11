package com.wugang.jsbridge.library.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.wugang.jsbridge.library.ImageItem;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import net.bither.util.NativeUtil;

/**
 * Created by lwg on 17-7-3.
 * 图片选择
 */

public class ImagePickerPluginUtils {

  private Activity mActivity;

  private OnListener listener;
  private Action<List<String>> action;

  /**
   *
   * @param activity
   */
  private ImagePickerPluginUtils(Activity activity) {
    mActivity = activity;
  }

  public static ImagePickerPluginUtils getInstance(Activity activity) {
    return new ImagePickerPluginUtils(activity);
  }

  public void setListener(OnListener listener) {
    this.listener = listener;
  }


  public void setPickerListener(Action<List<String>> action) {
    this.action = action;
  }

  public void onActivityResult(final List<ImageItem> listpath) {
    if (listener != null) listener.onStart();
    Executors.newSingleThreadExecutor().submit(new Runnable() {
      @Override public void run() {
        try {
          if (listpath != null) {
            final List<String> images = new ArrayList<>();
            for (ImageItem imageItem : listpath) {
              FileInputStream fis = new FileInputStream(imageItem.path);
              Bitmap bitmap = BitmapFactory.decodeStream(fis);
              String savePath = mActivity.getCacheDir().getPath() + "/" + imageItem.name;
              NativeUtil.compressBitmap(bitmap, savePath);
              fis = new FileInputStream(savePath);
              String image = Base64.encodeToString(FileUtils.stream2Byte(fis), Base64.DEFAULT);
              images.add(imageItem.path + "$$" + imageItem.addTime + "$$" + image);
            }
            action.call(images);
            if(listener!=null)
              listener.onCompleted();
          }
        } catch (FileNotFoundException e) {
          if (listener != null) listener.onError(e);
        } catch (IOException e) {
          if (listener != null) listener.onError(e);
        }
      }
    });
  }

  public interface OnListener {
    void onStart();

    void onError(Throwable e);

    void onCompleted();
  }


  public interface Action<T>{
    void call(T t);
  }
}
