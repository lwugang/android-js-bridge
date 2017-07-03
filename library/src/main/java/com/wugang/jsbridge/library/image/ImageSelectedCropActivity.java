package com.wugang.jsbridge.library.image;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.lzy.imagepicker.ui.ImageCropActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.wugang.jsbridge.library.R;
import com.wugang.jsbridge.library.utils.ImagePickerPluginUtils;
import java.lang.reflect.Field;

/**
 * Created by lwg on 2017/3/7.
 * 华海律正
 * 图片选择裁剪
 */

public class ImageSelectedCropActivity extends ImageCropActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView viewById = (ImageView) findViewById(com.lzy.imagepicker.R.id.btn_back);
        viewById.setImageResource(R.mipmap.ic_image_selected_back);
        try {
            Field mCropImageView = getClass().getSuperclass().getDeclaredField("mCropImageView");
            mCropImageView.setAccessible(true);
            CropImageView cropImageView = (CropImageView) mCropImageView.get(this);
            LinearLayout rl = (LinearLayout) cropImageView.getParent();
            rl.setFitsSystemWindows(true);

            rl.getChildAt(0).setBackgroundColor(
                Color.parseColor(ImagePickerPluginUtils.TOP_BAR_COLOR));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
