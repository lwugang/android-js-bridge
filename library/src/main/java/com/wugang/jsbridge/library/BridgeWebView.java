package com.wugang.jsbridge.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Map;

/**
 * Created by lwg on 17-6-27.
 */

public class BridgeWebView extends WebView {

  private JsCallJava jsCallJava;

  protected boolean isLoadUrl;//是否是通过loadUrl加载的

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
    if (!getSettings().getJavaScriptEnabled()) getSettings().setJavaScriptEnabled(true);
  }

  @Override public void setWebViewClient(WebViewClient client) {
    super.setWebViewClient(bridgeWebViewClient = new BridgeWebViewClient(client, jsCallJava));
  }

  @Override public void setWebChromeClient(WebChromeClient client) {
    super.setWebChromeClient(bridgeChromeClient = new BridgeChromeClient(client, jsCallJava, this));
  }

  /**
   * 如果注入的对象没有实现{@link JsPlugin}接口,就使用原生的注入方式
   * 原生注入方式不支持执行 匿名js函数，不支持返回值获取
   */
  @SuppressLint("JavascriptInterface") @Override public void addJavascriptInterface(Object object,
      String name) {
    if (object instanceof JsPlugin) {
      jsCallJava.addJavascriptInterfaces(this, object, name);
    } else {
      super.addJavascriptInterface(object, name);
    }
  }

  public void loadUrl(final String url) {
    if(!URLUtil.isJavaScriptUrl(url)){
      jsCallJava.onInject(this);
    }
    initClient();
    super.loadUrl(url);
  }

  @Override public void loadData(String data, String mimeType, String encoding) {
    initClient();
    jsCallJava.onInject(this);
    super.loadData(data, mimeType, encoding);
  }

  @Override public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
    initClient();
    if(!URLUtil.isJavaScriptUrl(url))
      jsCallJava.onInject(this);
    super.loadUrl(url, additionalHttpHeaders);
  }

  @Override
  public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding,
      String historyUrl) {
    initClient();
    super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
  }

  private void initClient() {
    if (bridgeWebViewClient == null) {
      this.setWebViewClient(new WebViewClient());
    }
    if (bridgeChromeClient == null) this.setWebChromeClient(new WebChromeClient());
  }
}
