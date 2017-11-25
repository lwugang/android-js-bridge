# android-js-bridge
###android js 互相调用
- #####支持js匿名函数接收
- #####支持js json对象接收
- #####支持js函数返回值获取
- #####通过注解注入js方法，支持自定义方法名

Add it in your root build.gradle at the end of repositories:
~~~gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
~~~

Add the dependency
~~~gradle
    dependencies {
	    compile 'com.github.lwugang:android-js-bridge:v0.1.4'
	}

~~~

####使用方式
~~~xml
	<com.wugang.jsbridge.library.BridgeWebView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/web_view"/>
~~~
###Activity
- A对象表示注入的插件对象,必须实现JsPlugin接口,所有需要注入的方法必须加 @JsInject 注解标记
- 或者在类上声明@JsInject 该类中的所有public就会被注入
- 如果该类中的方法不希望被注入可以 对方法加上@NoInject注解
~~~java
    package com.src.wugang.jsbridge;
    import android.content.Intent;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.widget.Toast;
    import com.wugang.jsbridge.library.BridgeWebView;
    import com.wugang.jsbridge.library.JSFunction;
    import com.wugang.jsbridge.library.JsPlugin;
    import com.wugang.jsbridge.library.JsReturnValueCallback;
    import com.wugang.jsbridge.library.anno.JsInject;
    import com.wugang.jsbridge.library.utils.ImagePickerPluginUtils;
    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    public class MainActivity extends AppCompatActivity {

    private ImagePickerPluginUtils imagePickerPlugin ;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BridgeWebView webView = (BridgeWebView) findViewById(R.id.web_view);
        webView.addJavascriptInterface(new A(), "android");
        webView.addJavascriptInterface(new B(), "ui");
        webView.loadUrl("file:///android_asset/test.html");
        imagePickerPlugin = ImagePickerPluginUtils.getInstance(this);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePickerPlugin.onActivityResult(requestCode,resultCode,data);
    }

    @JsInject
    public class A implements JsPlugin {

    public String getResult() {//不支持此中方式返回数据给js
      return "getResult";
    }
    public void testFun(JSFunction jsFunction){
      jsFunction.execute("testFun");
    }
    @JsInject("ddd")//注入方法重命名
    public void testFunReturn(JSFunction jsFunction){
      jsFunction.execute(new JsReturnValueCallback() {
        @Override public void onReturnValue(String result) {
          Toast.makeText(MainActivity.this,result,0).show();
        }
      },"testFunReturn");
    }
    }

    public class B implements JsPlugin {

    @JsInject("showImagePicker")
    public void test(String data,JSFunction function) {
      Toast.makeText(getApplicationContext(), data + "--", 1).show();
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("loginState",true);
        JSONArray array = new JSONArray();
        array.put(data);
        jsonObject.put("arr",array);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      function.execute(jsonObject);
    }
  }
}
~~~
HTML&JS代码
~~~js
<html>
    <script>
        function test(){
            ui.showImagePicker("showImagePicker",function(d){
                   alert(d)
                 //  document.getElementById('img01').src='data:image/png;base64,'+JSON.parse(d).images[0];
            });
        }
        function testFun(){
            android.testFun(function(data){
                alert(data);
            });
        }
        function testFunReturn(){
            android.ddd(function(data){
                alert(data);
                return "testFunReturn";
            });
        }
        function getResult(){//这种形式是获取不到数据的
            var result = android.getResult();
            alert(result);
        }
    </script>
    <body>
        <button onclick="getResult()">getResult</button>
        <button onclick="testFun()">testFun</button>
        <button onclick="testFunReturn()">testFunReturn</button>
        <button onclick="test()">select img</button>
        <img src="" id="img01" width="400" height="400"/>
    </body>
</html>
~~~
###版本历史
	###v0.0.2
- #####修改注入方式，插件类中被注入的方法必须加上 @JsInject 注解标记
###v0.0.5
- #####修改注入bug
###v0.1.1
- #####修改注入bug保证100%注入成功
###v0.1.4
- #####修改注入bug,优化注入，提高注入成功率,保证100%注入成功

#Android7.0 webview 的一个坑(内部已处理)
###Android7.0不会调用此方法
~~~java
@Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
}
~~~
###Android7.0 需要重写此方法
~~~java
@Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
}
~~~

[参考项目https://github.com/lwugang/safe-java-js-webview-bridge](https://github.com/lwugang/safe-java-js-webview-bridge)

[参考项目https://github.com/dukeland/EasyJSWebView](https://github.com/dukeland/EasyJSWebView)

![](https://github.com/lwugang/android-js-bridge/blob/master/device-2017-07-14-171656.png)
