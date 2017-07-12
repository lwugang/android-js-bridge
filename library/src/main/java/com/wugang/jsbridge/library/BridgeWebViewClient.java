/**
 * Summary: 应用中使用的WebChromeClient基类
 * Version 1.0
 * Date: 13-11-8
 * Time: 下午2:31
 * Copyright: Copyright (c) 2013
 */

package com.wugang.jsbridge.library;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class BridgeWebViewClient extends WebViewClient {
  private WebViewClient webViewClient;

  private JsCallJava mJsCallJava;

  public BridgeWebViewClient(WebViewClient webViewClient, JsCallJava mJsCallJava) {
    this.webViewClient = webViewClient;
    this.mJsCallJava = mJsCallJava;
  }
  //   ----------------需要处理的方法 start-------------------

  @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
    //BridgeWebView webView = (BridgeWebView) view;
    //if(!webView.isLoadUrl){
    //  mJsCallJava.onInject(view);
    //}
    webViewClient.onPageStarted(view, url, favicon);
  }

  @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
    if (mJsCallJava.shouldOverrideUrlLoading(view, url)) return true;
    return webViewClient.shouldOverrideUrlLoading(view, url);
  }
  //   ----------------需要处理的方法 end-------------------

  @Override public void onFormResubmission(WebView view, Message dontResend, Message resend) {
    webViewClient.onFormResubmission(view, dontResend, resend);
  }

  @Override public void onLoadResource(WebView view, String url) {
    webViewClient.onLoadResource(view, url);
  }

  @Override public void onDetectedBlankScreen(String s, int i) {
    super.onDetectedBlankScreen(s, i);
  }

  @Override public WebResourceResponse shouldInterceptRequest(WebView webView,
      WebResourceRequest webResourceRequest, Bundle bundle) {
    mJsCallJava.shouldOverrideUrlLoading(webView, webResourceRequest.getUrl().toString());
    return super.shouldInterceptRequest(webView, webResourceRequest, bundle);
  }

  @Override public void onPageFinished(WebView view, String url) {
    //BridgeWebView webView = (BridgeWebView) view;
    //if(webView.isLoadUrl) {
    //  webView.isLoadUrl = false;
    //}else{
    //  mJsCallJava.onInject(view);
    //}
    webViewClient.onPageFinished(view, url);
  }

  @Override public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
    webViewClient.onReceivedClientCertRequest(view, request);
  }

  @Override
  public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
    webViewClient.onReceivedError(view, request, error);
  }

  @Override
  public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,
      String realm) {
    webViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
  }

  @Override
  public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    webViewClient.onReceivedError(view, errorCode, description, failingUrl);
  }

  @Override public void onReceivedHttpError(WebView view, WebResourceRequest request,
      WebResourceResponse errorResponse) {
    webViewClient.onReceivedHttpError(view, request, errorResponse);
  }

  @Override
  public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
    webViewClient.onReceivedLoginRequest(view, realm, account, args);
  }

  @Override public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
    webViewClient.onReceivedSslError(view, handler, error);
  }

  @Override public void onScaleChanged(WebView view, float oldScale, float newScale) {
    webViewClient.onScaleChanged(view, oldScale, newScale);
  }

  @Override public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
    webViewClient.onUnhandledKeyEvent(view, event);
  }

  @Override public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
    webViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
  }


  @Override public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
    return webViewClient.shouldOverrideKeyEvent(view, event);
  }

  @Override
  public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
    return webViewClient.shouldInterceptRequest(view, request);
  }

  @Override public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    return webViewClient.shouldInterceptRequest(view, url);
  }

  @Override public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
    webViewClient.doUpdateVisitedHistory(view, url, isReload);
  }
}
