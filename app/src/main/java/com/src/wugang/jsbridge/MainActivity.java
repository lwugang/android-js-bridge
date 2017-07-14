package com.src.wugang.jsbridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.lzy.imagepicker.loader.ImageLoader;
import com.wugang.jsbridge.library.BridgeWebView;
import com.wugang.jsbridge.library.JSFunction;
import com.wugang.jsbridge.library.JsPlugin;
import com.wugang.jsbridge.library.JsReturnValueCallback;
import com.wugang.jsbridge.library.anno.JsInject;
import com.wugang.jsbridge.library.utils.ImagePickerPluginUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

  private ImagePickerPluginUtils imagePickerPlugin ;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BridgeWebView webView = (BridgeWebView) findViewById(R.id.web_view);
    webView.addJavascriptInterface(new A(), "android");
    webView.addJavascriptInterface(new B(), "ui");
    webView.loadUrl("file:///android_asset/test.html");

    //webView.loadUrl("http://192.168.10.217:1080/static/h5user",url,null);
    //webView.loadUrl("http://192.168.10.217:1080/static/h5user","http://192.168.10.217:1080/static/h5user/templates/service.html",null);
    imagePickerPlugin = ImagePickerPluginUtils.getInstance(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    imagePickerPlugin.onActivityResult(requestCode,resultCode,data);
  }

  @JsInject
  public class A implements JsPlugin {

    public String getResult() {
      return "getResult";
    }
    public void testFun(JSFunction jsFunction){
      jsFunction.execute("testFun");
    }
    @JsInject("ddd")
    public void testFunReturn(JSFunction jsFunction){
      jsFunction.execute(new JsReturnValueCallback() {
        @Override public void onReturnValue(String result) {
          Toast.makeText(MainActivity.this,result,0).show();
        }
      },"testFunReturn");
    }
  }

  public class B implements JsPlugin {

    @JsInject("showImagePicker")
    public void test(String data,final JSFunction function) {
      imagePickerPlugin.onPicker(new ImageLoader() {
        @Override
        public void displayImage(Activity activity, String s, ImageView imageView, int i, int i1) {
          Glide.with(activity).load(s).into(imageView);
        }

        @Override public void clearMemoryCache() {

        }}).subscribe(new Action1<String>() {
        @Override public void call(String s) {
          function.execute(s);
        }
      });

    }
  }
}
