package com.wugang.jsbridge.library.image;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.lzy.imagepicker.ui.ImageCropActivity;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewActivity;
import com.wugang.jsbridge.library.R;
import com.wugang.jsbridge.library.utils.ImagePickerPluginUtils;

/**
 * Created by lwg on 2017/3/7.
 * 华海律正
 * 图片选择
 */

public class ImageSelectedActivity extends ImageGridActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ImageView viewById = (ImageView) findViewById(com.lzy.imagepicker.R.id.btn_back);
    viewById.setImageResource(R.mipmap.ic_image_selected_back);
    LinearLayout rl = (LinearLayout) findViewById(R.id.content);
    rl.setFitsSystemWindows(true);
    findViewById(R.id.top_bar).setBackgroundColor(
        Color.parseColor(ImagePickerPluginUtils.TOP_BAR_COLOR));
  }

  @Override public void startActivityForResult(Intent intent, int requestCode) {
    if (intent != null && intent.getComponent() != null) {
      if (intent.getComponent().getClassName().equals(ImageCropActivity.class.getName())) {
        intent.setClass(this, ImageSelectedCropActivity.class);
      }
      if (intent.getComponent().getClassName().equals(ImagePreviewActivity.class.getName())) {
        intent.setClass(this, MyImagePreviewActivity.class);
      }
    }
    super.startActivityForResult(intent, requestCode);
  }
}
