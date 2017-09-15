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
import net.bither.util.NativeUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lwg on 17-7-3.
 * 图片选择
 */

public class ImagePickerPluginUtils {

  private Activity mActivity;

  private OnListener listener;

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

  /**
   * 设置压缩质量
   */
  public static void setDefaultQuality(int defaultQuality) {
    NativeUtil.setDefaultQuality(defaultQuality);
  }

  /**
   * 设置 压缩过后的图片大小
   */
  public static void setDefaultMaxSize(int defaultMaxSize) {
    NativeUtil.setDefaultMaxSize(defaultMaxSize);
  }

  public void onActivityResult(final List<ImageItem> listpath) {
    if (listener != null) listener.onStart();
    Observable.create(new Observable.OnSubscribe<List<String>>() {
      @Override public void call(Subscriber<? super List<String>> subscriber) {
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
            subscriber.onNext(images);
          }
        } catch (FileNotFoundException e) {
          subscriber.onError(e);
          if (listener != null) listener.onError(e);
        } catch (IOException e) {
          if (listener != null) listener.onError(e);
          subscriber.onError(e);
        }
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<String>>() {
          @Override public void onCompleted() {
            if (listener != null) listener.onCompleted();
          }

          @Override public void onError(Throwable throwable) {
            if (listener != null) listener.onError(throwable);
          }

          @Override public void onNext(List<String> strings) {
            if (listener != null) listener.onCompleted();
          }
        });
  }


  public interface OnListener {
    void onStart();

    void onError(Throwable e);

    void onCompleted();
  }
}
