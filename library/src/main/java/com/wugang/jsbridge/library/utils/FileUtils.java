package com.wugang.jsbridge.library.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lwg on 17-7-3.
 */

public class FileUtils {
  /**
   *  输入流转byte数组
   * @return
   */
  public static byte[] stream2Byte(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] bytes = new byte[1024];
    int len = -1;
    while ((len=is.read(bytes))!=-1){
      baos.write(bytes,0,len);
    }
    byte[] bytes1 = baos.toByteArray();
    is.close();
    baos.close();
    return bytes1;
  }
}
