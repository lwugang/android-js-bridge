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
    private WebChromeClient webChromeClient;

    private JsCallJava mJsCallJava;
    private boolean mIsInjectedJS;

    public BridgeChromeClient(WebChromeClient webChromeClient,JsCallJava mJsCallJava) {
        this.webChromeClient = webChromeClient;
        this.mJsCallJava = mJsCallJava;
    }
    //   ----------------需要处理的方法 start-------------------

    @Override
    public void onProgressChanged (WebView view, int newProgress) {
        //为什么要在这里注入JS
        //1 OnPageStarted中注入有可能全局注入不成功，导致页面脚本上所有接口任何时候都不可用
        //2 OnPageFinished中注入，虽然最后都会全局注入成功，但是完成时间有可能太晚，当页面在初始化调用接口函数时会等待时间过长
        //3 在进度变化时注入，刚好可以在上面两个问题中得到一个折中处理
        //为什么是进度大于25%才进行注入，因为从测试看来只有进度大于这个数字页面才真正得到框架刷新加载，保证100%注入成功
        //if (newProgress >= 25) {
        //    if (!mIsInjectedJS) {
        //        mJsCallJava.onInject(view);
        //        mIsInjectedJS = true;
        //    }
        //}else{
        //    mIsInjectedJS = false;
        //}
        super.onProgressChanged(view, newProgress);
    }
    //   ----------------需要处理的方法 end-------------------

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
        JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
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

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return webChromeClient.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
        FileChooserParams fileChooserParams) {
        return webChromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    @Override public void onCloseWindow(WebView window) {
        webChromeClient.onCloseWindow(window);
    }

    @Override public void onGeolocationPermissionsHidePrompt() {
        webChromeClient.onGeolocationPermissionsHidePrompt();
    }

    @Override public void onGeolocationPermissionsShowPrompt(String origin,
        GeolocationPermissions.Callback callback) {
        webChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override public void onHideCustomView() {
        webChromeClient.onHideCustomView();
    }

    @Override public void onPermissionRequest(PermissionRequest request) {
        webChromeClient.onPermissionRequest(request);
    }

    @Override public void onPermissionRequestCanceled(PermissionRequest request) {
        webChromeClient.onPermissionRequestCanceled(request);
    }

    @Override public void onReceivedIcon(WebView view, Bitmap icon) {
        webChromeClient.onReceivedIcon(view, icon);
    }

    @Override public void onReceivedTitle(WebView view, String title) {
        webChromeClient.onReceivedTitle(view, title);
    }

    @Override public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        webChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override public void onRequestFocus(WebView view) {
        webChromeClient.onRequestFocus(view);
    }

    @Override public void onShowCustomView(View view, CustomViewCallback callback) {
        webChromeClient.onShowCustomView(view, callback);
    }

    @Override public boolean onJsTimeout() {
        return webChromeClient.onJsTimeout();
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        webChromeClient.onShowCustomView(view, requestedOrientation, callback);
    }

    @Override public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        webChromeClient.onConsoleMessage(message, lineNumber, sourceID);
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
        webChromeClient.getVisitedHistory(callback);
    }
}
