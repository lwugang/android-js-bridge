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
	   compile 'com.github.lwugang:android-js-bridge:v1.0'
	}
~~~

#####使用方式
~~~xml
	<com.wugang.jsbridge.library.BridgeWebView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/web_view"/>
~~~
######Activity
- A对象表示注入的插件对象,必须实现JsPlugin接口,A类中的所有public方法都会被注入到js中，可以被js调用
~~~java
	WebView webView = (WebView) findViewById(R.id.web_view);
    webView.addJavascriptInterface(new A(), "android");
    webView.loadUrl("file:///android_asset/test.html");
    
    public class A implements JsPlugin{
        public void jsCall(WebView webView, int data, final JsCallback jsCallback,
            final JsCallback jc) {
          Toast.makeText(webView.getContext(), data + "--", 1).show();
          webView.postDelayed(new Runnable() {
            @Override public void run() {
              try {
                jsCallback.apply("android callback");
                jc.apply();
              } catch (JsCallback.JsCallbackException e) {
                e.printStackTrace();
              }
            }
          }, 2000);
        }

        public void setHtml(WebView webView, String s) {
          Log.e("----------", "jsCall: " + s);
        }
  }
~~~
[参考项目https://github.com/lwugang/safe-java-js-webview-bridge](https://github.com/lwugang/safe-java-js-webview-bridge)