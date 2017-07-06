package com.wugang.jsbridge.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Map;

/**
 * Created by lwg on 17-6-27.
 */

public class BridgeWebView extends WebView {

  private JsCallJava jsCallJava;

  private BridgeWebViewClient bridgeWebViewClient;
  private BridgeChromeClient bridgeChromeClient;

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

  @Override public void setWebChromeClient(WebChromeClient client) {
    super.setWebChromeClient(bridgeChromeClient = new BridgeChromeClient(client,jsCallJava));
  }

  /**
   * 如果注入的对象没有实现{@link JsPlugin}接口,就使用原生的注入方式
   *  原生注入方式不支持执行 匿名js函数，不支持返回值获取
   * @param object
   * @param name
   */
  @SuppressLint("JavascriptInterface") @Override public void addJavascriptInterface(Object object, String name) {
    if(!getSettings().getJavaScriptEnabled())
      getSettings().setJavaScriptEnabled(true);
    if(object instanceof JsPlugin) {
      jsCallJava.addJavascriptInterfaces(this,object,name);
    }else{
      super.addJavascriptInterface(object,name);
    }
  }

  @Override public void loadUrl(String url) {
    if(bridgeWebViewClient==null) {
      this.setWebViewClient(new WebViewClient());
    }
    if(bridgeChromeClient==null)
      this.setWebChromeClient(new WebChromeClient());
    super.loadUrl(url);
    if(!jsCallJava.isInject()) {
      addJavascriptInterface(this, "Bridge");
      loadUrl("javascript:Bridge.onDocumentLoad()");
    }
  }

  @JavascriptInterface
  public void onDocumentLoad(){
    jsCallJava.onInject(this);
  }

  @Override public void loadData(String data, String mimeType, String encoding) {
    if(bridgeWebViewClient==null) {
      this.setWebViewClient(new WebViewClient());
    }
    if(bridgeChromeClient==null)
      this.setWebChromeClient(new WebChromeClient());
    super.loadData(data, mimeType, encoding);
  }

  @Override public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
    if(bridgeWebViewClient==null) {
      this.setWebViewClient(new WebViewClient());
    }
    if(bridgeChromeClient==null)
      this.setWebChromeClient(new WebChromeClient());
    super.loadUrl(url, additionalHttpHeaders);
  }

  @Override
  public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding,
      String historyUrl) {
    initClient();
    super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
  }

  private void initClient() {
    if(bridgeWebViewClient==null) {
      this.setWebViewClient(new WebViewClient());
    }
    if(bridgeChromeClient==null)
      this.setWebChromeClient(new WebChromeClient());
  }
}
