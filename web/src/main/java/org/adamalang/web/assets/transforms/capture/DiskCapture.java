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
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetStream;

import java.io.*;

/** for large assets, we store them on a disk using a random-ish name */
public class DiskCapture implements AssetStream {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(DiskCapture.class);
  private final SimpleExecutor executor;
  private final NtAsset asset;
  private final File fileToUse;
  private final Callback<InflightAsset> callback;
  private FileOutputStream output;
  private boolean finished;

  public DiskCapture(SimpleExecutor executor, NtAsset asset, File transformCacheDirectory, Callback<InflightAsset> callback) {
    this.executor = executor;
    this.asset = asset;
    this.fileToUse = new File(transformCacheDirectory, asset.id() + "." + ProtectedUUID.generate() + ".cache");
    this.callback = callback;
    this.finished = false;
  }

  @Override
  public void headers(long length, String contentType, String contentMd5) {
    executor.execute(new NamedRunnable("open") {
      @Override
      public void execute() throws Exception {
        try {
          output = new FileOutputStream(fileToUse);
        } catch (Exception ex) {
          fail(ex);
        }
      }
    });
  }

  @Override
  public void body(byte[] chunk, int offset, int length, boolean last) {
    executor.execute(new NamedRunnable("body") {
      @Override
      public void execute() throws Exception {
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
    });
  }

  private boolean raiseFinish(boolean cleanup) {
    if (finished) {
      return false;
    }
    if (cleanup) {
      AvoidExceptions.flushAndClose(output);
      AvoidExceptions.deleteFile(fileToUse);
    }
    finished = true;
    return true;
  }

  private void success() {
    if (raiseFinish(false)) {
      InflightAsset ia = new InflightAsset() {
        @Override
        public InputStream open() throws Exception {
          return new FileInputStream(fileToUse);
        }

        @Override
        public void finished() {
          fileToUse.delete();
        }
      };
      callback.success(ia);
    }
  }

  private void fail(Exception ex) {
    if (raiseFinish(true)) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.ASSET_TRANSFORM_DISK_CAPTURE_EXCEPTION, ex, EXLOGGER));
    }
  }

  @Override
  public void failure(int code) {
    executor.execute(new NamedRunnable("failure") {
      @Override
      public void execute() throws Exception {
        fail(new ErrorCodeException(code));
      }
    });
  }
}
