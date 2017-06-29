/**
 * Summary: 异步回调页面JS函数管理对象
 * Version 1.0
 * Date: 13-11-26
 * Time: 下午7:55
 * Copyright: Copyright (c) 2013
 */

package com.wugang.jsbridge.library;

import android.util.Log;
import android.webkit.WebView;
import java.lang.ref.WeakReference;

/**
 * js返回值回调
 */
public interface JsReturnValueCallback {
  void onReturnValue(String result);
}
