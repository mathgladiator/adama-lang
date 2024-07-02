/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.web.assets.transforms.capture;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.web.assets.AssetStream;

import java.io.*;

/** for small assets, we keep it in memory */
public class MemoryCapture implements AssetStream {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(DiskCapture.class);
  private final Callback<InflightAsset> callback;
  private ByteArrayOutputStream output;
  private boolean finished;

  public MemoryCapture(Callback<InflightAsset> callback) {
    this.callback = callback;
    this.finished = false;
  }

  @Override
  public void headers(long length, String contentType, String contentMd5) {
    this.output = new ByteArrayOutputStream((int) length);
  }

  @Override
  public void body(byte[] chunk, int offset, int length, boolean last) {
    try {
      output.write(chunk, offset, length);
      if (last) {
        output.flush();
        output.close();
        success();
      }
    } catch (Exception ex) {
      fail(ex);
    }
  }

  private void success() {
    if (finished) {
      return;
    }
    finished = true;
    byte[] memory = output.toByteArray();
    callback.success(new InflightAsset() {
      @Override
      public InputStream open() throws Exception {
        return new ByteArrayInputStream(memory);
      }

      @Override
      public void finished() {
        // nothing to clean up
      }
    });
  }

  private void fail(Exception ex) {
    if (finished) {
      return;
    }
    finished = true;
    callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.ASSET_TRANSFORM_DISK_CAPTURE_EXCEPTION, ex, EXLOGGER));
  }

  @Override
  public void failure(int code) {
    fail(new ErrorCodeException(code));
  }
}
