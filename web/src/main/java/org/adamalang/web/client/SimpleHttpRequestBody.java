/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

/** a simplified http body */
public interface SimpleHttpRequestBody {
  /** the size of the body */
  long size();

  /** read a chunk */
  int read(byte[] chunk);

  /** the body is finished being read */
  void finished();

  public static SimpleHttpRequestBody EMPTY = new SimpleHttpRequestBody() {
    @Override
    public long size() {
      return 0;
    }

    @Override
    public int read(byte[] chunk) {
      return 0;
    }

    @Override
    public void finished() {
    }
  };
}
