/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import java.io.ByteArrayInputStream;

/** a simplified http body */
public interface SimpleHttpRequestBody {
  SimpleHttpRequestBody EMPTY = new SimpleHttpRequestBody() {
    @Override
    public long size() {
      return 0;
    }

    @Override
    public int read(byte[] chunk) throws Exception {
      return 0;
    }

    @Override
    public void finished(boolean success) throws Exception {
    }
  };

  static SimpleHttpRequestBody WRAP(byte[] bytes) {
    ByteArrayInputStream memory = new ByteArrayInputStream(bytes);
    return new SimpleHttpRequestBody() {
      @Override
      public long size() {
        return bytes.length;
      }

      @Override
      public int read(byte[] chunk) throws Exception {
        return memory.read(chunk);
      }

      @Override
      public void finished(boolean success) throws Exception {
        memory.close();
      }
    };
  }

  /** the size of the body */
  long size();

  /** read a chunk */
  int read(byte[] chunk) throws Exception;

  /** the body is finished being read */
  void finished(boolean success) throws Exception;
}
