package com.wugang.jsbridge.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Map;

/**
 * Created by lwg on 17-6-27.
 */

public class BridgeWebView extends WebView {

  private JsCallJava jsCallJava;

  protected boolean isInject;//是否是通过loadUrl加载的

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

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int contentHeight = getContentHeight();
    if (!isInject&&contentHeight>0) {
      isInject = contentHeight > 0;
      jsCallJava.onInject(this);
    }
  }

  @Override public void invalidate() {
    super.invalidate();
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
    //isLoadUrl = true;
    //if(URLUtil.isHttpUrl(url)||URLUtil.isHttpsUrl(url)){
    //  jsCallJava.onInject(this);
    //  ThreadUtils.getInstance().downloadHtml(url, jsCallJava.getINJECT_JS(),new ThreadUtils.OnResultListener() {
    //    @Override public void onResult(final String result) {
    //      post(new Runnable() {
    //        @Override public void run() {
    //          jsCallJava.setInject(true);
    //          loadDataWithBaseURL(baseUrl,result,"text/html;charset=utf-8",null,historyUrl);
    //        }
    //      });
    //    }
    //
    //    @Override public void onError() {
    //      post(new Runnable() {
    //        @Override public void run() {
    //          jsCallJava.setInject(false);
    //          isLoadUrl = false;
    //          BridgeWebView.super.loadUrl(url);
    //        }
    //      });
    //    }
    //  });
    //}
    initClient();
    super.loadUrl(url);
  }
  //
  ///**
  // * 调用此方法注入
  // */
  //public void inject() {
  //  if (reloadCount > 10) {
  //    reload();
  //    return;
  //  }
  //  reloadCount++;
  //  loadUrl("javascript:Bridge.onDocumentLoad()");
  //}

  @Override public void loadData(String data, String mimeType, String encoding) {
    initClient();
    super.loadData(data, mimeType, encoding);
  }

  @Override public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
    initClient();
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
