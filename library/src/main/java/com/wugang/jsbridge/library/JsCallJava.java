package com.wugang.jsbridge.library;

import android.annotation.SuppressLint;
import android.util.ArrayMap;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsCallJava {
  String INJECT_JS = "window.EasyJS = {\n"
      + "\t__callbacks: {},\n"
      + "\t\n"
      + "\tinvokeCallback: function (cbID, removeAfterExecute){\n"
      + "\t\tvar args = Array.prototype.slice.call(arguments);\n"
      + "\t\targs.shift();\n"
      + "\t\targs.shift();\n"
      + "\t\t\n"
      + "\t\tfor (var i = 0, l = args.length; i < l; i++){\n"
      + "\t\t\targs[i] = decodeURIComponent(args[i]);\n"
      + "\t\t}\n"
      + "\t\t\n"
      + "\t\tvar cb = EasyJS.__callbacks[cbID];\n"
      + "\t\tif (removeAfterExecute){\n"
      + "\t\t\tEasyJS.__callbacks[cbID] = undefined;\n"
      + "\t\t}\n"
      + "\t\treturn cb.apply(null, args);\n"
      + "\t},\n"
      + "\t\n"
      + "\tcall: function (obj, functionName, args){\n"
      + "\t\tvar formattedArgs = [];\n"
      + "\t\tfor (var i = 0, l = args.length; i < l; i++){\n"
      + "\t\t\tif (typeof args[i] == \"function\"){\n"
      + "\t\t\t\tformattedArgs.push(\"f\");\n"
      + "\t\t\t\tvar cbID = \"__cb\" + (+new Date);\n"
      + "\t\t\t\tEasyJS.__callbacks[cbID] = args[i];\n"
      + "\t\t\t\tformattedArgs.push(cbID);\n"
      + "\t\t\t}else{\n"
      + "\t\t\t\tformattedArgs.push(\"s\");\n"
      + "\t\t\t\tformattedArgs.push(encodeURIComponent(args[i]));\n"
      + "\t\t\t}\n"
      + "\t\t}\n"
      + "\t\t\n"
      + "\t\tvar argStr = (formattedArgs.length > 0 ? \":\" + encodeURIComponent(formattedArgs.join(\":\")) : \"\");\n"
      + "\t\t\n"
      + "\t\tvar iframe = document.createElement(\"IFRAME\");\n"
      + "\t\tiframe.setAttribute(\"src\", \"easy-js:\" + obj + \":\" + encodeURIComponent(functionName) + argStr);\n"
      + "\t\tdocument.documentElement.appendChild(iframe);\n"
      + "\t\tiframe.parentNode.removeChild(iframe);\n"
      + "\t\tiframe = null;\n"
      + "\t\t\n"
      + "\t\tvar ret = EasyJS.retValue;\n"
      + "\t\tEasyJS.retValue = undefined;\n"
      + "\t\t\n"
      + "\t\tif (ret){\n"
      + "\t\t\treturn decodeURIComponent(ret);\n"
      + "\t\t}\n"
      + "\t},\n"
      + "\t\n"
      + "\tinject: function (obj, methods){\n"
      + "\t\twindow[obj] = {};\n"
      + "\t\tvar jsObj = window[obj];\n"
      + "\t\t\n"
      + "\t\tfor (var i = 0, l = methods.length; i < l; i++){\n"
      + "\t\t\t(function (){\n"
      + "\t\t\t\tvar method = methods[i];\n"
      + "\t\t\t\tvar jsMethod = method.replace(new RegExp(\":\", \"g\"), \"\");\n"
      + "\t\t\t\tjsObj[jsMethod] = function (){\n"
      + "\t\t\t\t\treturn EasyJS.call(obj, method, Array.prototype.slice.call(arguments));\n"
      + "\t\t\t\t};\n"
      + "\t\t\t})();\n"
      + "\t\t}\n"
      + "\t},\n"
      + "\treturnValue:function(data){\n"
      + "\t\treturn data;\n"
      + "\t}\n"
      + "};";
  //js注入对象
  public Map<String, Object> objectMap;

  //返回值回调队列
  private Map<String, JSFunction> arrayMap;

  public void addJavascriptInterfaces(BridgeWebView bridgeWebView, Object obj, String name) {
    //预注入一个获取js返回值的对象
    bridgeWebView.addJavascriptInterface(this, JSFunction.INJECT_OBJ_NAME);
    if (objectMap == null) objectMap = new HashMap<>();
    objectMap.put(name, obj);
  }

  /**
   * javascript 返回结果
   */
  @JavascriptInterface public void returnValue(String callbackId, String result) {
    JsReturnValueCallback returnValueCallback = arrayMap.get(callbackId).returnValueCallback;
    if (returnValueCallback != null) {
      returnValueCallback.onReturnValue(result);
      arrayMap.remove(callbackId);
    }
  }

  @SuppressLint("WrongConstant") public void onPageStarted(final WebView view, String url) {
    final StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
      sb.append("EasyJS.inject('");
      sb.append(entry.getKey());
      sb.append("', [");
      Method[] methods = entry.getValue().getClass().getDeclaredMethods();
      for (int i = 0; i < methods.length; i++) {
        //只注入public方法
        if (methods[i].getModifiers() != Modifier.PUBLIC) continue;
        sb.append("\"");
        sb.append(methods[i].getName());
        sb.append("\"");
        if (i != (methods.length - 1)) {
          sb.append(",");
        }
      }
      sb.append("]);");
    }
    view.postDelayed(new Runnable() {
      @Override public void run() {
        view.loadUrl("javascript:" + INJECT_JS);
        view.loadUrl("javascript:" + sb.toString());
      }
    }, 20);
  }

  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    if (url.startsWith("easy-js:")) {
      String[] strings = url.split(":");
      //js调用的对象
      String obj = strings[1];
      //js调用的方法名
      String methodName = strings[2];
      //js调用对象对应的 java对象
      Object destJavaObj = objectMap.get(obj);
      try {
        List<Object> javaMethodParams = new ArrayList<>();

        if (strings.length > 3) {//表示有参数
          String[] args = URLDecoder.decode(strings[3], "UTF-8").split(":");
          for (int i = 0, j = 0, l = args.length; i < l; i += 2, j++) {

            String argsType = args[i];
            String argsValue = args[i + 1];

            if ("f".equals(argsType)) {//f 表示这个参数是一个函数
              JSFunction func = new JSFunction();
              javaMethodParams.add(func);
              if (arrayMap == null) arrayMap = new HashMap<>();
              String key = new String(Base64.encode(url.getBytes(), Base64.DEFAULT)).trim();
              func.initWithWebView(view, argsValue, key);
              arrayMap.put(key, func);
            } else if ("s".equals(argsType)) {
              javaMethodParams.add(URLDecoder.decode(argsValue, "UTF-8"));
            }
          }
        }
        invoke(destJavaObj, methodName, javaMethodParams.toArray());
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      return true;
    }
    return false;
  }

  private void invoke(Object javaObj, String methodName, Object[] objects)
      throws InvocationTargetException, IllegalAccessException {
    Method[] declaredMethods = javaObj.getClass().getDeclaredMethods();
    if (declaredMethods == null) return;
    for (int i = 0; i < declaredMethods.length; i++) {
      String name = declaredMethods[i].getName();
      if (methodName != null && methodName.equals(name)) {
        declaredMethods[i].invoke(javaObj, getValueByType(declaredMethods[i], objects));
        return;
      }
    }
  }

  private Object[] getValueByType(Method declaredMethod, Object[] objects) {
    Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
    if (objects.length != parameterTypes.length) {
      throw new IllegalArgumentException("参数不匹配");
    }
    List<Object> objectList = new ArrayList<>();
    for (int i = 0; i < parameterTypes.length; i++) {
      Class<?> type = parameterTypes[i];
      if (type == int.class) {
        objectList.add(Integer.parseInt(objects[i].toString()));
      } else if (type == double.class) {
        objectList.add(Double.parseDouble(objects[i].toString()));
      } else if (type == float.class) {
        objectList.add(Float.parseFloat(objects[i].toString()));
      } else if (type == byte.class) {
        objectList.add(Byte.parseByte(objects[i].toString()));
      } else if (type == long.class) {
        objectList.add(Long.parseLong(objects[i].toString()));
      } else {
        objectList.add(objects[i]);
      }
    }
    return objectList.toArray();
  }
}