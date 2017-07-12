package com.src.wugang.jsbridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.wugang.jsbridge.library.BridgeWebView;
import com.wugang.jsbridge.library.JSFunction;
import com.wugang.jsbridge.library.JsPlugin;
import com.wugang.jsbridge.library.anno.JsInject;
import com.wugang.jsbridge.library.utils.ImagePickerPluginUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  private ImagePickerPluginUtils imagePickerPlugin ;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BridgeWebView webView = (BridgeWebView) findViewById(R.id.web_view);
    webView.addJavascriptInterface(new A(), "android");
    webView.addJavascriptInterface(new B(), "ui");
    webView.loadUrl("http://www.hao123.com");
    //webView.loadUrl("http://192.168.10.217:1080/static/h5user",url,null);
    //webView.loadUrl("http://192.168.10.217:1080/static/h5user","http://192.168.10.217:1080/static/h5user/templates/service.html",null);
    imagePickerPlugin = ImagePickerPluginUtils.getInstance(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    imagePickerPlugin.onActivityResult(requestCode,resultCode,data);
  }

  public class A implements JsPlugin {

    @JsInject()
    public void test(final JSFunction function) {
      function.execute(String.format("{\"serverId\":%d}", 1));
      //imagePickerPlugin.onPicker(new ImageLoader() {
      //  @Override
      //  public void displayImage(Activity activity, String s, ImageView imageView, int i, int i1) {
      //    Glide.with(activity).load(s).into(imageView);
      //  }
      //
      //  @Override public void clearMemoryCache() {
      //
      //  }
      //}).subscribe(new Action1<String>() {
      //  @Override public void call(String strings) {
      //    JSONObject jsonObject = new JSONObject();
      //    try {
      //      JSONArray jsonArray = new JSONArray();
      //      jsonArray.put(strings);
      //
      //      jsonObject.put("images",jsonArray);
      //    } catch (JSONException e) {
      //      e.printStackTrace();
      //    }
      //    function.execute("{\"images\":[\""+strings+"\"]}");
      //  }
      //});
    }
  }

  public class B implements JsPlugin {
    @JsInject
    public void test(String data,JSFunction function) {
      Toast.makeText(getApplicationContext(), data + "--", 1).show();
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("loginState",true);
        JSONArray array = new JSONArray();
        array.put(data);
        jsonObject.put("arr",array);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      function.execute(jsonObject);
    }
  }
}
