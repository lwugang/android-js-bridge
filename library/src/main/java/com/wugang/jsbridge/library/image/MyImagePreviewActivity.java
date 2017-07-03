package com.wugang.jsbridge.library.image;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.lzy.imagepicker.ui.ImagePreviewActivity;
import com.wugang.jsbridge.library.R;
import com.wugang.jsbridge.library.utils.ImagePickerPluginUtils;

/**
 * Created by lwg on 2017/3/16.
 * 华海律正
 */

public class MyImagePreviewActivity extends ImagePreviewActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView viewById = (ImageView) findViewById(com.lzy.imagepicker.R.id.btn_back);
        viewById.setImageResource(R.mipmap.ic_image_selected_back);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
        params.topMargin = 0;
        topBar.setLayoutParams(params);
        LinearLayout rl = (LinearLayout) findViewById(R.id.content);
        rl.setFitsSystemWindows(true);
        findViewById(R.id.top_bar).setBackgroundColor(
            Color.parseColor(ImagePickerPluginUtils.TOP_BAR_COLOR));
    }
}
