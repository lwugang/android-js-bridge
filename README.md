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
	    compile 'com.github.lwugang:android-js-bridge:v0.0.8'
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
~~~java
	   package com.src.wugang.jsbridge;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.util.Log;
    import android.webkit.WebChromeClient;
    import android.webkit.WebView;
    import android.widget.Toast;
    import com.wugang.jsbridge.library.BridgeWebView;
    import com.wugang.jsbridge.library.JSFunction;
    import com.wugang.jsbridge.library.JsPlugin;
    import com.wugang.jsbridge.library.JsReturnValueCallback;

    public class MainActivity extends AppCompatActivity {

      @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BridgeWebView webView = (BridgeWebView) findViewById(R.id.web_view);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new A(), "android");
        webView.addJavascriptInterface(new B(), "ui");
        webView.loadUrl("file:///android_asset/test.html");

      }

      public class A implements JsPlugin {
		@JsInject("demo")// 必须加上此注解
        public void test(int data, JSFunction function) {
          Toast.makeText(getApplicationContext(), data + "--", 1).show();
          //返回值方式 高低版本都可以使用
          function.execute(new JsReturnValueCallback() {
            @Override public void onReturnValue(String result) {
              Toast.makeText(getApplicationContext(),  "return value--"+result, 1).show();
            }
          },80);
        }
      }

      public class B implements JsPlugin {
        public void test(int data) {
          Toast.makeText(getApplicationContext(), data + "--", 1).show();
        }
      }
    }
~~~
HTML&JS代码
~~~js
<html>
    <script>
        function test(){
            var obj = "{'name':'lwg','age':123}";
            //支持json对象传递,匿名函数传递
            android.test(obj,function(d){
                  alert(d);
                  return 2048;//支持返回值获取
            });
        }

    </script>
    <body>
        <button onclick="test()">call android</button>
        <button onclick="ui.test(222222)">call android</button>
        <a href="file:///android_asset/test1.html">test1</a>
        <button onclick="location.reload()">刷新</button>
    </body>
</html>
~~~
###版本历史
	###v0.0.2
- #####加入图片浏览，图片默认处理成base64,并压缩 (使用图片浏览服务必须的依赖)
~~~gradle
 	 compile 'com.lzy.widget:imagepicker:0.5.5'
  	compile 'io.reactivex:rxjava:1.1.2'
~~~
- #####修改注入方式，插件类中被注入的方法必须加上 @JsInject 注解标记
###v0.0.5
- #####修改注入bug

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