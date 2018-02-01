package com.wugang.jsbridge.library;

import android.webkit.WebView;

/**
 * Created by lwg on 17-12-8.
 */

public interface IInject {
  void addJavascriptInterfaces(BridgeWebView bridgeWebView, Object obj, String name);

  void inject(WebView view);

  boolean shouldOverrideUrlLoading(WebView view, String url);

  String getInjectString();
}
