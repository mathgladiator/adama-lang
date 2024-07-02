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
package org.adamalang.runtime.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.AdamaStream;

public class MockAdamaStream implements AdamaStream {
  private final StringBuilder writer;

  public MockAdamaStream() {
    this.writer = new StringBuilder();
  }

  @Override
  public String toString() {
    return writer.toString();
  }

  @Override
  public void update(String newViewerState) {
    writer.append("UPDATE:" + newViewerState + "\n");
  }

  @Override
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    writer.append("SEND:" + channel + "/" + marker + "/" + message + "\n");
    if (channel.equals("failure")) {
      callback.failure(new ErrorCodeException(-1));
    } else {
      callback.success(123);
    }
  }

  @Override
  public void password(String password, Callback<Integer> callback) {
    writer.append("PASSWORD!\n");
    if (password.equals("failed")) {
      callback.failure(new ErrorCodeException(-23));
    } else {
      callback.success(42);
    }
  }

  @Override
  public void canAttach(Callback<Boolean> callback) {
    writer.append("CANATTACH\n");
    callback.success(true);
    callback.failure(new ErrorCodeException(-2));
  }

  @Override
  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    writer.append("ATTACH:" + id + "/" + name + "/" + contentType + "/" + size + "/" + md5 + "/" + sha384 + "\n");
    if (id.equals("failure")) {
      callback.failure(new ErrorCodeException(-2));
    } else {
      callback.success(1);
    }
  }

  @Override
  public void close() {
    writer.append("CLOSE\n");
  }
}
