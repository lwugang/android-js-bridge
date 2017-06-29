package com.wugang.jsbridge.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by lwg on 17-6-27.
 */

public class BridgeWebView extends WebView {

  private JsCallJava jsCallJava;

  private BridgeWebViewClient bridgeWebViewClient;

  public BridgeWebView(Context context) {
    super(context);
    init();
  }

  public BridgeWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    jsCallJava = new JsCallJava();
  }

  @Override public void setWebViewClient(WebViewClient client) {
    super.setWebViewClient(bridgeWebViewClient = new BridgeWebViewClient(client,jsCallJava));
  }

  /**
   * 如果注入的对象没有实现{@link JsPlugin}接口,就使用原生的注入方式
   *  原生注入方式不支持执行 匿名js函数，不支持返回值获取
   * @param object
   * @param name
   */
  @SuppressLint("JavascriptInterface") @Override public void addJavascriptInterface(Object object, String name) {
    if(object instanceof JsPlugin) {
      if(!getSettings().getJavaScriptEnabled())
        getSettings().setJavaScriptEnabled(true);
      if(bridgeWebViewClient==null)
        this.setWebViewClient(new WebViewClient());
      jsCallJava.addJavascriptInterfaces(object,name);
    }else{
      super.addJavascriptInterface(object,name);
    }
  }
}
