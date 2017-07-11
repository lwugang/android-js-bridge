package com.wugang.jsbridge.library.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lwg on 17-7-11.
 */

public class ThreadUtils {
  private ExecutorService service = Executors.newFixedThreadPool(2);
  private int reconnect = 0;
  private ThreadUtils() {

  }

  private static ThreadUtils threadUtils = new ThreadUtils();

  public static ThreadUtils getInstance() {
    return threadUtils;
  }

  public void downloadHtml(final String url,final String injectCode,final OnResultListener onResultListener) {
    service.execute(new Runnable() {
      @Override public void run() {
        try {
          HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
          int responseCode = urlConnection.getResponseCode();
          if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = urlConnection.getInputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            StringBuffer sb = new StringBuffer();
            while ((len = inputStream.read(bytes)) != -1) {
              sb.append(new String(bytes, 0, len));
            }
            final String str = "<script>"+injectCode+"</script>";

            sb.insert("<html>".length(),str);

            onResultListener.onResult(sb.toString());
            return;
          }else {
            retry();
          }
        } catch (IOException e) {
          e.printStackTrace();
          retry();
        }
      }

      private void retry() {
        if(reconnect>3) {
          onResultListener.onError();
          return;
        }
        reconnect++;
        run();
      }
    });
  }

  public interface OnResultListener{
    void onResult(String result);
    void onError();
  }
}
