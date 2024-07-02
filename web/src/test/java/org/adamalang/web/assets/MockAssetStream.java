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
package org.adamalang.web.assets;

import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class MockAssetStream implements AssetStream {

  private ByteArrayOutputStream memory = new ByteArrayOutputStream();
  private long length = -1;
  private String type = "";
  private String md5 = null;
  private boolean done = false;
  private Integer failureCode = null;

  @Override
  public void headers(long length, String contentType, String md5) {
    this.length = length;
    this.type = contentType;
    this.md5 = md5;
  }

  public void assertHeaders(long exLength, String exType) {
    Assert.assertEquals(exLength, this.length);
    Assert.assertEquals(exType, this.type);
  }

  @Override
  public void body(byte[] chunk, int offset, int length, boolean last) {
    memory.write(chunk, offset, length);
    if (last) {
      Assert.assertFalse(done);
      done = true;
    }
  }

  public void assertNotDone() {
    Assert.assertFalse(done);
  }

  public void assertDone() {
    Assert.assertTrue(done);
  }

  public void assertBody(String expect) {
    Assert.assertEquals(expect, new String(memory.toByteArray(), StandardCharsets.UTF_8));
  }

  public void assertNoFailure() {
    Assert.assertNull(failureCode);
  }

  public void assertFailure(int code) {
    Assert.assertNotNull(failureCode);
    Assert.assertEquals(code, (int) failureCode);
  }

  @Override
  public void failure(int code) {
    this.failureCode = code;
  }
}
