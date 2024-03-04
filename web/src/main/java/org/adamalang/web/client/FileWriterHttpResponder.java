/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
  private final Inflight notFoundAlarm;

  public FileWriterHttpResponder(File fileToWrite, Inflight notFoundAlarm, Callback<Void> callback) throws ErrorCodeException {
    try {
      this.fileToWrite = fileToWrite;
      this.notFoundAlarm = notFoundAlarm;
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
      if (header.status == 404) {
        notFoundAlarm.up();
      }
      switch (header.status) {
        case 410:
        case 404:
        case 403:
          // these are converted to unique errors
          break;
        default:
          LOGGER.error("failed-to-write: {} status:{} headers:{}", fileToWrite.toString(), header.status, header.headers.toString());
      }
      int errorCode = HttpError.translateHttpStatusCodeToError(header.status, ErrorCodes.WEB_BASE_FILE_WRITER_NOT_200);
      callback.failure(new ErrorCodeException(errorCode));
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
    } else {
      good = false;
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
    } else {
      good = false;
    }
  }

  @Override
  public void failure(ErrorCodeException ex) {
    if (good) {
      good = false;
      callback.failure(ex);
    }
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
