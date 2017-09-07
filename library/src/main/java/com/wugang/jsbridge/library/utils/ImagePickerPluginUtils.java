package com.wugang.jsbridge.library.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.wugang.jsbridge.library.image.ImageSelectedActivity;
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

  public static final String TOP_BAR_COLOR = "#cc22292c";

  private Activity mActivity;

  private rx.Observable observable;
  private Subscriber<? super List<String>> subscriber;

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

  public void onActivityResult(int requestCode, int resultCode, final Intent data) {
    if (ImagePicker.RESULT_CODE_ITEMS == resultCode) {
      Observable.create(new Observable.OnSubscribe<List<String>>() {
        @Override public void call(Subscriber<? super List<String>> subscriber) {
          try {
            List<ImageItem> listpath =
                (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (listpath != null) {
              final List<String> images = new ArrayList<>();
              for (ImageItem imageItem : listpath) {
                FileInputStream fis = new FileInputStream(imageItem.path);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                String savePath = mActivity.getCacheDir().getPath() + "/" + imageItem.name;
                NativeUtil.compressBitmap(bitmap, savePath);
                fis = new FileInputStream(savePath);
                String image = Base64.encodeToString(FileUtils.stream2Byte(fis), Base64.DEFAULT);
                images.add(image);
              }
              subscriber.onNext(images);
            }
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<String>>() {
        @Override public void onCompleted() {

        }

        @Override public void onError(Throwable throwable) {

        }

        @Override public void onNext(List<String> strings) {
          subscriber.onNext(strings);
        }
      });
    }
  }

  /**
   * 选择图片
   */
  public Observable<List<String>> onPicker(ImagePicker imagePicker) {
    Intent intent = new Intent(mActivity, ImageSelectedActivity.class);
    mActivity.startActivityForResult(intent, 0);
    observable = Observable.create(new Observable.OnSubscribe<List<String>>() {
      @Override public void call(Subscriber<? super List<String>> subscriber) {
        ImagePickerPluginUtils.this.subscriber = subscriber;
      }
    });
    return observable;
  }
}
