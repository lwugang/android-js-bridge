# android-js-bridge
###android js 互相调用
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
	    compile 'com.github.lwugang:android-js-bridge:v2.0'
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
- A对象表示注入的插件对象,必须实现JsPlugin接口,A类中的所有public方法都会被注入到js中，可以被js调用
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
        	//调用 android 中的方法
            android.test(20,function(d){
                  alert(d);
                  return 2048;
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
[参考项目https://github.com/lwugang/safe-java-js-webview-bridge](https://github.com/lwugang/safe-java-js-webview-bridge)
[参考项目https://github.com/dukeland/EasyJSWebView](https://github.com/dukeland/EasyJSWebView)