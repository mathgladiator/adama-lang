/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.io.File;
import java.io.FileOutputStream;

/** write the body of an HTTP response to disk */
public class FileWriterHttpResponder implements SimpleHttpResponder {
  public final FileOutputStream output;
  public final Callback<Void> callback;
  private boolean good;
  private long left;
  private boolean checkSize;

  public FileWriterHttpResponder(File fileToWrite, Callback<Void> callback) throws ErrorCodeException {
    try {
      this.output = new FileOutputStream(fileToWrite);
      this.callback = callback;
      this.good = true;
    } catch (Exception ex) {
      throw new ErrorCodeException(ErrorCodes.WEB_BASE_FILE_WRITER_IOEXCEPTION_CREATE, ex);
    }
  }

  @Override
  public void start(SimpleHttpResponseHeader header) {
    if (good && header.status != 200) {
      this.good = false;
      callback.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_FILE_WRITER_NOT_200));
    }
  }

  @Override
  public void bodyStart(long size) {
    this.left = size;
    this.checkSize = size >= 0;
  }

  @Override
  public void bodyFragment(byte[] chunk, int offset, int len) {
    if (write(output, chunk, offset, len, callback)) {
      if (good && checkSize) {
        left -= len;
      }
    }
  }

  @Override
  public void bodyEnd() {
    if (finish(output, callback)) {
      if (good) {
        if (left == 0 || !checkSize) {
          callback.success(null);
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_FILE_WRITER_PREMATURE_END));
        }
      }
    }
  }

  @Override
  public void failure(ErrorCodeException ex) {
    good = false;
    callback.failure(ex);
  }

  public static boolean write(FileOutputStream output, byte[] chunk, int offset, int len, Callback<Void> callback) {
    try {
      output.write(chunk, offset, len);
      return true;
    } catch (Exception ioe) {
      callback.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_FILE_WRITER_IOEXCEPTION_FRAGMENT, ioe));
      return false;
    }
  }

  public static boolean finish(FileOutputStream output, Callback<Void> callback) {
    try {
      output.flush();
      output.close();
      return true;
    } catch (Exception ioe) {
      callback.failure(new ErrorCodeException(ErrorCodes.WEB_BASE_FILE_WRITER_IOEXCEPTION_CLOSE, ioe));
      return false;
    }
  }
}
