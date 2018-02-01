package com.src.wugang.jsbridge;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;
import com.wugang.jsbridge.library.BridgeWebView;
import com.wugang.jsbridge.library.JSFunction;
import com.wugang.jsbridge.library.JsPlugin;
import com.wugang.jsbridge.library.JsReturnValueCallback;
import com.wugang.jsbridge.library.anno.JsInject;
import com.wugang.jsbridge.library.utils.ImagePickerPluginUtils;

public class MainActivity extends AppCompatActivity {

  private ImagePickerPluginUtils imagePickerPlugin;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BridgeWebView webView = (BridgeWebView) findViewById(R.id.web_view);
    webView.addJavascriptInterface(new A(), "LYUIHandle");
    //webView.addJavascriptInterface(new B(), "LYRouterHandle");
    //webView.addJavascriptInterface(new B(), "LYUserHandle");
    //syncCookie("file:///android_asset/test.html","token=123456");

    //webView.loadUrl("http://10.41.3.97:8080/test.html");
    webView.loadUrl("file:///android_asset/test.html");

    //webView.loadUrl("http://192.168.10.217:1080/static/h5user",url,null);
    //webView.loadUrl("http://192.168.10.217:1080/static/h5user","http://192.168.10.217:1080/static/h5user/templates/service.html",null);
    //imagePickerPlugin = ImagePickerPluginUtils.getInstance(this);
  }

  public void start(View v) {
    startActivity(new Intent(this, getClass()));
  }

  /**
   * 将cookie同步到WebView
   *
   * @param url WebView要加载的url
   * @param cookie 要同步的cookie
   * @return true 同步cookie成功，false同步cookie失败
   * @Author JPH
   */
  public boolean syncCookie(String url, String cookie) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      CookieSyncManager.createInstance(getApplicationContext());
    }
    CookieManager cookieManager = CookieManager.getInstance();
    cookieManager.setCookie(url, cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
    String newCookie = cookieManager.getCookie(url);
    CookieSyncManager.getInstance().sync();
    return TextUtils.isEmpty(newCookie) ? false : true;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //imagePickerPlugin.onActivityResult(requestCode,resultCode,data);
  }

  @JsInject public class A implements JsPlugin {

    public String getResult() {
      return "getResult";
    }
    @JsInject
    public void testFun(JSFunction... jsFunction) {
      //Log.e("--------", "testFun: "+str );
      for (int i = 0; i < jsFunction.length; i++) {
        jsFunction[i].execute();
      }
    }

    @JsInject("ddd") public void testFunReturn(JSFunction jsFunction) {
      jsFunction.execute(new JsReturnValueCallback() {
        @Override public void onReturnValue(String result) {
          Toast.makeText(MainActivity.this, result, 0).show();
        }
      }, "testFunReturn");
    }
  }

  public class B implements JsPlugin {

    @JsInject("showImagePicker") public void test(String data, final JSFunction function) {
      //imagePickerPlugin.onPicker(new ImageLoader() {
      //  @Override
      //  public void displayImage(Activity activity, String s, ImageView imageView, int i, int i1) {
      //    Glide.with(activity).load(s).into(imageView);
      //  }
      //
      //  @Override public void clearMemoryCache() {
      //
      //  }}).subscribe(new Action1<String>() {
      //  @Override public void call(String s) {
      //    function.execute(s);
      //  }
      //});

    }
  }
}
