package com.wugang.jsbridge.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by lwg on 17-6-27.
 */

public class BridgeWebView extends WebView {

  private Object injectObject;
  private String injectName;

  private BridgeChromeClient bridgeChromeClient;
  public BridgeWebView(Context context) {
    super(context);
  }

  public BridgeWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @SuppressLint("JavascriptInterface") @Override public void addJavascriptInterface(Object object, String name) {
    if(object instanceof JsPlugin) {
      injectObject = object;
      injectName = name;
      if(!getSettings().getJavaScriptEnabled())
        getSettings().setJavaScriptEnabled(true);
      //super.addJavascriptInterface(object, name);
      if(bridgeChromeClient==null)
        setWebChromeClient(new WebChromeClient());
    }else{
      Log.e("addJavascriptInterface","addJavascriptInterface 方法的注入对象必须 {@link JsPlugin} 接口");
    }
  }

  @Override public void setWebChromeClient(WebChromeClient client) {
    super.setWebChromeClient(bridgeChromeClient = new BridgeChromeClient(client,injectName,injectObject));
  }
}
