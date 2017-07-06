package com.src.wugang.jsbridge;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.imagepicker.loader.ImageLoader;
import com.wugang.jsbridge.library.BridgeWebView;
import com.wugang.jsbridge.library.JSFunction;
import com.wugang.jsbridge.library.JsPlugin;
import com.wugang.jsbridge.library.anno.JsInject;
import com.wugang.jsbridge.library.utils.ImagePickerPluginUtils;
import java.net.URLEncoder;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

  private ImagePickerPluginUtils imagePickerPlugin ;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BridgeWebView webView = (BridgeWebView) findViewById(R.id.web_view);
    webView.setWebChromeClient(new WebChromeClient());
    webView.addJavascriptInterface(new A(), "android");
    webView.addJavascriptInterface(new B(), "ui");
    webView.loadUrl("file:///android_asset/test.html");
    imagePickerPlugin = ImagePickerPluginUtils.getInstance(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    imagePickerPlugin.onActivityResult(requestCode,resultCode,data);
  }

  public class A implements JsPlugin {

    @JsInject("demo")
    public void test(String data, final JSFunction function) {
      imagePickerPlugin.onPicker(new ImageLoader() {
        @Override
        public void displayImage(Activity activity, String s, ImageView imageView, int i, int i1) {
          Glide.with(activity).load(s).into(imageView);
        }

        @Override public void clearMemoryCache() {

        }
      }).subscribe(new Action1<String>() {
        @Override public void call(String strings) {
          function.execute(strings);
        }
      });
    }
  }

  public class B implements JsPlugin {
    @JsInject
    public void test(int data,JSFunction function) {
      Toast.makeText(getApplicationContext(), data + "--", 1).show();
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("loginState",true);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      HashMap<String,Object> map = new HashMap<>();
      map.put("loginState",true);
      function.execute("{\"loginState\":true}");
    }
  }
}
