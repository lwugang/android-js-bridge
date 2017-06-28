package com.src.wugang.jsbridge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import com.wugang.jsbridge.library.JsCallback;
import com.wugang.jsbridge.library.JsPlugin;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    WebView webView = (WebView) findViewById(R.id.web_view);
    webView.addJavascriptInterface(new A(), "android");
    webView.loadUrl("file:///android_asset/test.html");
  }

  public class A implements JsPlugin{
    public void jsCall(WebView webView, int data, final JsCallback jsCallback,
        final JsCallback jc) {
      Toast.makeText(webView.getContext(), data + "--", 1).show();
      webView.postDelayed(new Runnable() {
        @Override public void run() {
          try {
            jsCallback.apply("android callback");
            jc.apply();
          } catch (JsCallback.JsCallbackException e) {
            e.printStackTrace();
          }
        }
      }, 2000);
    }

    public void setHtml(WebView webView, String s) {
      Log.e("----------", "jsCall: " + s);
    }
  }
}
