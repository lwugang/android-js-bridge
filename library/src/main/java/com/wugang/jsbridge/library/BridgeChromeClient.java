/**
 * Summary: 应用中使用的WebChromeClient基类
 * Version 1.0
 * Date: 13-11-8
 * Time: 下午2:31
 * Copyright: Copyright (c) 2013
 */

package com.wugang.jsbridge.library;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

public class BridgeChromeClient extends WebChromeClient {
  private final WebView webView;
  private WebChromeClient webChromeClient;

  private JsCallJava mJsCallJava;

  public BridgeChromeClient(WebChromeClient webChromeClient, JsCallJava mJsCallJava,
      WebView webView) {
    this.webChromeClient = webChromeClient;
    this.mJsCallJava = mJsCallJava;
    this.webView = webView;
  }
  //   ----------------需要处理的方法 start-------------------

  @Override public void onProgressChanged(WebView view, int newProgress) {
    //if(newProgress!=0) {
    //  mJsCallJava.onInject(view);
    //}
    webChromeClient.onProgressChanged(view,newProgress);
  }
  //   ----------------需要处理的方法 end-------------------

  @Override public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
      JsPromptResult result) {
    return webChromeClient.onJsPrompt(view, url, message, defaultValue, result);
  }

  @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
    return webChromeClient.onConsoleMessage(consoleMessage);
  }

  @Override public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
      Message resultMsg) {
    return webChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
  }

  @Override public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
    return webChromeClient.onJsAlert(view, url, message, result);
  }

  @Override
  public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
    return webChromeClient.onJsBeforeUnload(view, url, message, result);
  }

  @Override public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
    return webChromeClient.onJsConfirm(view, url, message, result);
  }

  @Override public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
      FileChooserParams fileChooserParams) {
    return webChromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
  }

  @Override public void onCloseWindow(WebView window) {
    webChromeClient.onCloseWindow(window);
  }

  @Override public void onGeolocationPermissionsHidePrompt() {
    webChromeClient.onGeolocationPermissionsHidePrompt();
  }


  @Override public void onHideCustomView() {
    webChromeClient.onHideCustomView();
  }


  @Override public void onReceivedIcon(WebView view, Bitmap icon) {
    webChromeClient.onReceivedIcon(view, icon);
  }

  @Override public void onReceivedTitle(WebView view, String title) {
    //mJsCallJava.onInject(view);
    webChromeClient.onReceivedTitle(view, title);
  }

  @Override public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
    webChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
  }

  @Override public void onRequestFocus(WebView view) {
    webChromeClient.onRequestFocus(view);
  }

  @Override public boolean onJsTimeout() {
    return webChromeClient.onJsTimeout();
  }

  @Override public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
      long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
    webChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize,
        totalQuota, quotaUpdater);
  }

  @Override public void onReachedMaxAppCacheSize(long requiredStorage, long quota,
      WebStorage.QuotaUpdater quotaUpdater) {
    webChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
  }

  @Override public Bitmap getDefaultVideoPoster() {
    return webChromeClient.getDefaultVideoPoster();
  }

  @Override public View getVideoLoadingProgressView() {
    return webChromeClient.getVideoLoadingProgressView();
  }

  @Override public void getVisitedHistory(ValueCallback<String[]> callback) {
    //mJsCallJava.onInject(webView);
    webChromeClient.getVisitedHistory(callback);
  }

  @Override public void onShowCustomView(View view, CustomViewCallback callback) {
    webChromeClient.onShowCustomView(view, callback);
  }

  @Override
  public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
    webChromeClient.onShowCustomView(view, requestedOrientation, callback);
  }

  @Override public void onGeolocationPermissionsShowPrompt(String origin,
      GeolocationPermissions.Callback callback) {
    webChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
  }

  @Override public void onPermissionRequest(PermissionRequest request) {
    webChromeClient.onPermissionRequest(request);
  }

  @Override public void onPermissionRequestCanceled(PermissionRequest request) {
    webChromeClient.onPermissionRequestCanceled(request);
  }

  @Override public void onConsoleMessage(String message, int lineNumber, String sourceID) {
    webChromeClient.onConsoleMessage(message, lineNumber, sourceID);
  }
}
