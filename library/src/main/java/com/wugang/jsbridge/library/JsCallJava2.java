package com.wugang.jsbridge.library;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.wugang.jsbridge.library.anno.JsInject;
import com.wugang.jsbridge.library.anno.NoInject;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsCallJava2 implements IInject {
  String INJECT_JS = "if(!window.EasyJS){\n"
      + "    window.EasyJS = {\n"
      + "        __callbacks: {},\n"
      + "        \n"
      + "        invokeCallback: function (cbID, removeAfterExecute){\n"
      + "            var args = Array.prototype.slice.call(arguments);\n"
      + "            args.shift();\n"
      + "            args.shift();"
      + "for (var i = 0, l = args.length; i < l; i++){\n"
      + "\t\t\targs[i] = decodeURIComponent(args[i]);\n"
      + "\t\t}"
      + "            var cb = EasyJS.__callbacks[cbID];\n"
      + "            if (removeAfterExecute){\n"
      + "                EasyJS.__callbacks[cbID] = undefined;\n"
      + "            }\n"
      + "            return cb.apply(null, args);\n"
      + "        },\n"
      + "        \n"
      + "        call: function (obj, functionName, args){\n"
      + "            var formattedArgs = [];\n"
      + "            for (var i = 0, l = args.length; i < l; i++){\n"
      + "                if (typeof args[i] == \"function\"){\n"
      + "                    formattedArgs.push(\"f\");\n"
      + "                    var cbID = \"__cb\" + parseInt((+new Date)*Math.random()*Math.random()*Math.random());\n"
      + "                    EasyJS.__callbacks[cbID] = args[i];\n"
      + "                    formattedArgs.push(cbID);\n"
      + "                }else{\n"
      + "                    formattedArgs.push(\"s\");\n"
      + "                    formattedArgs.push(encodeURIComponent(args[i]));\n"
      + "                }\n"
      + "            }\n"
      + "            \n"
      + "            var argStr = (formattedArgs.length > 0 ? \":\" + encodeURIComponent(formattedArgs.join(\":\")) : \"\");\n"
      + "            \n"
      + "            var iframe = document.createElement(\"IFRAME\");\n"
      + "            iframe.setAttribute(\"src\", \"easy-js:\" + obj + \":\" + encodeURIComponent(functionName) + argStr);\n"
      + "            document.documentElement.appendChild(iframe);\n"
      + "            iframe.parentNode.removeChild(iframe);\n"
      + "            iframe = null;\n"
      + "            \n"
      + "            var ret = EasyJS.retValue;\n"
      + "            EasyJS.retValue = undefined;\n"
      + "            \n"
      + "            if (ret){\n"
      + "                return decodeURIComponent(ret);\n"
      + "            }\n"
      + "        },\n"
      + "        \n"
      + "        inject: function (obj, methods){\n"
      + "            if(typeof(window[obj])!='undefined')\n"
      + "                return;\n"
      + "            window[obj] = {};\n"
      + "            var jsObj = window[obj];\n"
      + "            for (var i = 0, l = methods.length; i < l; i++){\n"
      + "                (function (){\n"
      + "                    var method = methods[i];\n"
      + "                    var jsMethod = method.replace(new RegExp(\":\", \"g\"), \"\");\n"
      + "                    jsObj[jsMethod] = function (){\n"
      + "                        return EasyJS.call(obj, method, Array.prototype.slice.call(arguments));\n"
      + "                    };\n"
      + "                })();\n"
      + "            }\n"
      + "        }\n"
      + "    };\n"
      + "}";
  //过滤object对象的方法
  public List<String> filterMethodNames =
      Arrays.asList("getClass", "hashCode", "equals", "toString", "notify", "notifyAll", "wait");
  //js注入对象
  public Map<String, Object> objectMap;
  //js 注入对象对应的方法列表
  public Map<Object, Map<String, String>> objectMethodMap;

  //返回值回调队列
  private Map<String, JSFunction> arrayMap;

  private boolean isInject = false;
  private String string;

  private Handler handler = new Handler(Looper.getMainLooper());

  @Override
  public void addJavascriptInterfaces(BridgeWebView bridgeWebView, Object obj, String name) {
    //预注入一个获取js返回值的对象
    bridgeWebView.addJavascriptInterface(this, JSFunction.INJECT_OBJ_NAME);
    if (objectMap == null) {
      objectMap = new HashMap<>();
    }
    objectMap.put(name, obj);
  }

  /**
   * javascript 注入一个获取返回值的方法
   */
  @JavascriptInterface public void returnValue(String callbackId, String result) {
    JSFunction jsFunction = arrayMap.get(callbackId);
    if (jsFunction == null) return;
    JsReturnValueCallback returnValueCallback = jsFunction.returnValueCallback;
    if (returnValueCallback != null) {
      returnValueCallback.onReturnValue(result);
      arrayMap.remove(callbackId);
    }
  }

  @Override public void inject(WebView view) {
    if (objectMap == null || objectMap.isEmpty()) return;
    if (isInject()) {
      loadJs(view);
      return;
    }
    String objectJs = "if(typeof(window.%s)=='undefined'){ window.%s = {";
    String methodJs = "%s:function(){"
        + " return EasyJS.call(\"%s\", \"%s\", Array.prototype.slice.call(arguments));},";
    final StringBuilder objectSb = new StringBuilder();

    for (Map.Entry<String, Object> entry : objectMap.entrySet()) {

      objectSb.append(String.format(objectJs, entry.getKey(), entry.getKey()));

      List<Method> methods = findInjectMethods(entry.getValue());
      if (methods.size() > 0 && objectMethodMap == null) {
        objectMethodMap = new HashMap<>();
      }
      Map<String, String> temp = new HashMap<>();

      objectMethodMap.put(entry.getValue(), temp);

      final StringBuilder methodSb = new StringBuilder();

      for (int i = 0; i < methods.size(); i++) {
        JsInject jsInject = methods.get(i).getAnnotation(JsInject.class);
        String name = methods.get(i).getName();
        if (filterMethodNames.contains(name)) {
          continue;
        }
        if (jsInject != null) {
          String tempName = jsInject.value();
          if (!TextUtils.isEmpty(tempName)) name = tempName;
        }
        temp.put(name, methods.get(i).getName());
        methodSb.append(String.format(methodJs, name, entry.getKey(), name));
      }
      if (methodSb.length() > 0) methodSb.deleteCharAt(methodSb.length() - 1);
      objectSb.append(methodSb);
      objectSb.append("}}");
    }
    string = objectSb.toString();
    setInject(true);
    loadJs(view);
  }

  /**
   * cbID
   * 查找需要注入的方法
   */
  List<Method> findInjectMethods(Object object) {
    List<Method> methodList = new ArrayList<>();

    Class<?> aClass = object.getClass();
    //获取类中所有的方法
    Method[] aClassMethods = aClass.getMethods();
    //获取父类的注解
    JsInject annotation = aClass.getAnnotation(JsInject.class);
    //如果父类包含次注解就注入该类中的所有方法
    boolean isInjectClass = annotation != null;
    if (aClassMethods != null) {
      for (int i = 0; i < aClassMethods.length; i++) {
        Method aClassMethod = aClassMethods[i];
        if (isInjectClass) {
          if (aClassMethod.getAnnotation(NoInject.class) == null) {
            methodList.add(aClassMethod);
          }
        } else {
          //获取方法上的注解
          JsInject jsInject = aClassMethod.getAnnotation(JsInject.class);
          if (jsInject != null) {
            methodList.add(aClassMethod);
          }
        }
      }
    }
    return methodList;
  }

  @Override public String getInjectString() {
    return INJECT_JS + "\n" + string;
  }

  private void loadJs(final WebView view) {
    handler.post(new Runnable() {
      @Override public void run() {
        view.loadUrl("javascript:{" + INJECT_JS + "\n" + string + "}");
      }
    });
  }

  public boolean isInject() {
    return isInject;
  }

  public void setInject(boolean inject) {
    isInject = inject;
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
    methodName = objectMethodMap.get(javaObj).get(methodName);
    if (methodName == null) return;
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
      } else if (type.isArray()) {
        if (type.getComponentType() == JSFunction.class) {
          JSFunction[] jsFunctions = new JSFunction[objects.length - i];
          System.arraycopy(objects, i, jsFunctions, 0, jsFunctions.length);
          objectList.add(jsFunctions);
        } else {
          objectList.add(objects);
        }
        return objectList.toArray();
      } else {
        objectList.add(objects[i]);
      }
    }
    return objectList.toArray();
  }
}