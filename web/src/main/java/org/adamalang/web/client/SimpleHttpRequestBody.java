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
package org.adamalang.web.client;

import com.fasterxml.jackson.databind.node.ObjectNode;

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


    @Override
    public void pumpLogEntry(ObjectNode body) {
      body.put("type", "empty");
      body.put("size", 0);
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

      @Override
      public void pumpLogEntry(ObjectNode body) {
        body.put("type", "byte-array");
        body.put("size", bytes.length);
      }
    };
  }

  /** the size of the body */
  long size();

  /** read a chunk */
  int read(byte[] chunk) throws Exception;

  /** the body is finished being read */
  void finished(boolean success) throws Exception;

  /** fill a log entry */
  void pumpLogEntry(ObjectNode body);
}
