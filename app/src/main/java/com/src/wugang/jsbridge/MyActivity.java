package com.src.wugang.jsbridge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MyActivity extends Activity {

    private WebView mWebView;
    private Button mBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mWebView = (WebView) findViewById(R.id.webview);
        mBtn = (Button) findViewById(R.id.btn);


        /**
         * 自适应屏幕
         */
//        autoJustScreen();

        /**
         * 一.1.1 加载在线url
         */
//        loadOnlineURL("http://www.baidu.com");

        /**
         * 一.1.2加载本地html文件
         */
        loadAssetsFile();

        /**
         * 一.2.1启用WebViewJavaScript
         */
        enableJs();


        /**
         * 二.1 html调用Native的方法,传递this对象
         * @param webView
         */
//        html2Navtive(mWebView);


        /**
         * 二.2 html调用Native的方法,传递JSBridge对象
         * @param webView
         */
        html2Navtive_CLASS(mWebView);


        /**
         * 三 Android中调用WebView中的JS方法
         * @param webView
         */
//        Android2JS(mWebView);
    }

    /**
     * 自动适应屏幕
     */
    private void autoJustScreen(){
        WebSettings settings = mWebView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl("http://www.w3school.com.cn/");
    }

    /**
     * 一.1.1 加载在线url
     *
     * @param url
     */
    private void loadOnlineURL(final String url) {
        mWebView.setWebViewClient(new WebViewClient());

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(url);
            }
        });
    }

    /**
     * 一.1.2加载本地html文件
     */
    private void loadAssetsFile() {
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("file:///android_asset/web.html");
            }
        });
    }

    /**
     * 一.2.1启用WebViewJavaScript
     */
    private void enableJs() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    /**
     * 二.1 html调用Native的方法,传递this对象
     *
     * @param webView
     */
    private void html2Navtive(WebView webView) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "android");
        mWebView.loadUrl("file:///android_asset/web.html");
    }

    @JavascriptInterface
    public void toastMessage(String message,String s) {
        Toast.makeText(getApplicationContext(), "通过Natvie传递的Toast:" + message+"-----"+s, Toast.LENGTH_LONG).show();
    }

    /**
     * 二.2 html调用Native的方法,传递JSBridge对象
     *
     * @param webView
     */
    @SuppressLint("JavascriptInterface") public void html2Navtive_CLASS(WebView webView) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSBridge(), "android");
        mWebView.loadUrl("file:///android_asset/web.html");
    }

    public class JSBridge {
                @JavascriptInterface
        public void toastMessage(String message,String s) {
            Toast.makeText(getApplicationContext(), "通过Natvie传递的Toast:" + message+"------"+s, Toast.LENGTH_LONG).show();
                    mWebView.loadUrl("javascript:"+s);
        }
    }


    /**
     * 三 Android中调用WebView中的JS方法
     *
     * @param webView
     */
    public void Android2JS(final WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "android");
        webView.loadUrl("file:///android_asset/web.html");

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:sum(3,8)");
            }
        });
    }

    public void onSumResult(int result) {
        Toast.makeText(this, "received result:" + result, Toast.LENGTH_SHORT).show();
    }
}
