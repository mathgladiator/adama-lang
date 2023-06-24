/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.Inflight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

/** write the body of an HTTP response to disk */
public class FileWriterHttpResponder implements SimpleHttpResponder {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileWriterHttpResponder.class);
  private final File fileToWrite;
  public final FileOutputStream output;
  public final Callback<Void> callback;
  private boolean good;
  private long left;
  private boolean checkSize;
  private final Inflight alarm;

  public FileWriterHttpResponder(File fileToWrite, Inflight alarm, Callback<Void> callback) throws ErrorCodeException {
    try {
      this.fileToWrite = fileToWrite;
      this.alarm = alarm;
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
      alarm.up();
      LOGGER.error("failed-to-write: {} status:{} headers:{}", fileToWrite.toString(), header.status, header.headers.toString());
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
