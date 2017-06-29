package com.src.wugang.jsbridge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;
import com.wugang.jsbridge.library.BridgeWebView;
import com.wugang.jsbridge.library.JSFunction;
import com.wugang.jsbridge.library.JsPlugin;
import com.wugang.jsbridge.library.JsReturnValueCallback;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BridgeWebView webView = (BridgeWebView) findViewById(R.id.web_view);
    webView.setWebChromeClient(new WebChromeClient());
    webView.addJavascriptInterface(new A(), "android");
    webView.addJavascriptInterface(new B(), "ui");
    webView.loadUrl("file:///android_asset/test.html");

  }

  public class A implements JsPlugin {

    public void test(int data, JSFunction function) {
      Toast.makeText(getApplicationContext(), data + "--", 1).show();
      function.execute(new JsReturnValueCallback() {
        @Override public void onReturnValue(String result) {
          Toast.makeText(getApplicationContext(),  "return value--"+result, 1).show();
        }
      },80);
    }
  }

  public class B implements JsPlugin {
    public void test(int data) {
      Toast.makeText(getApplicationContext(), data + "--", 1).show();
    }
  }
}
