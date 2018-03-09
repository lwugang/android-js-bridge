package com.wugang.jsbridge.library;

import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * Created by lwg on 17-6-29.
 * sdk 大于 19 才能获取到返回值
 */

public final class JSFunction {
  private String funcID;
  private WebView webView;
  private boolean removeAfterExecute = false;

  public static final String INJECT_OBJ_NAME = "_callback";

  //回调
  protected JsReturnValueCallback returnValueCallback;

  private String callbackId;

  protected final void initWithWebView(final WebView webView, String funcID, String callbackId) {
    this.webView = webView;
    this.funcID = funcID;
    this.callbackId = callbackId;
  }

  /**
   * 设置当前函数执行完成之后是否从函数队列移除
   */
  public void setRemoveAfterExecute(boolean removeAfterExecute) {
    this.removeAfterExecute = removeAfterExecute;
  }

  /**
   * 执行js函数
   */
  public void execute() {
    execute(null);
  }

  /**
   * 执行js函数
   *
   * @param returnValueCallback js返回值回调
   * @param params 参数
   */
  public void execute(final JsReturnValueCallback returnValueCallback, String... params) {
    try {
      this.returnValueCallback = returnValueCallback;
      final StringBuilder sb = new StringBuilder();
      if (returnValueCallback != null) {
        sb.append(INJECT_OBJ_NAME).append(".returnValue('").append(callbackId).append("',");
      }
      sb.append(String.format("EasyJS.invokeCallback(\"%s\", %s", funcID,
          Boolean.toString(removeAfterExecute)));
      if (params != null) {
        for (int i = 0, l = params.length; i < l; i++) {
          String arg = params[i];
          //arg = URLEncoder.encode(arg, "UTF-8");
          sb.append(String.format(", '%s'", arg));
        }
      }
      if (returnValueCallback != null) {
        sb.append(")");
      }
      sb.append(");");
      if(Build.VERSION.SDK_INT>=19){
        webView.evaluateJavascript("javascript:"+sb.toString(), new ValueCallback<String>() {
          @Override public void onReceiveValue(String value) {

          }
        });
      }else {
        webView.loadUrl("javascript:" + sb.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 执行js函数
   *
   * @param params 参数
   */
  public void execute(String... params) {
    execute(null, params);
  }
}
