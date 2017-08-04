package com.wugang.jsbridge.library.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.wugang.jsbridge.library.image.ImageSelectedActivity;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.bither.util.NativeUtil;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by lwg on 17-7-3.
 * 图片选择
 */

public class ImagePickerPluginUtils {

  public static final String TOP_BAR_COLOR = "#cc22292c";

  private Activity mActivity;

  private rx.Observable observable;
  private Subscriber<? super String> subscriber;

  private static ImagePickerPluginUtils utils;

  /**
   *
   * @param activity
   */
  private ImagePickerPluginUtils(Activity activity) {
    mActivity = activity;
  }

  public static ImagePickerPluginUtils getInstance(Activity activity) {
    utils = new ImagePickerPluginUtils(activity);
    return utils;
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

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (ImagePicker.RESULT_CODE_ITEMS == resultCode) {
      List<ImageItem> listpath =
          (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
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
            images.add(image);
          }
          subscriber.onNext(images.get(0));
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 选择图片
   */
  public Observable<String> onPicker(ImageLoader imageLoader) {
    ImagePicker imagePicker = ImagePicker.getInstance();
    imagePicker.setImageLoader(imageLoader);   //设置图片加载器
    imagePicker.setShowCamera(true);  //显示拍照按钮
    imagePicker.setMultiMode(false);
    imagePicker.setCrop(false);
    Intent intent = new Intent(mActivity, ImageSelectedActivity.class);
    mActivity.startActivityForResult(intent, 0);
    observable = Observable.create(new Observable.OnSubscribe<String>() {
      @Override public void call(Subscriber<? super String> subscriber) {
        ImagePickerPluginUtils.this.subscriber = subscriber;
      }
    });
    return observable;
  }

  public void destory(){
    mActivity = null;
    utils = null;
  }
}
