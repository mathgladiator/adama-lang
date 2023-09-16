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
package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Assert;
import org.junit.Test;

public class RemoteResultTests {
  @Test
  public void nothing() {
    RemoteResult result = new RemoteResult(null, null, null);
    JsonStreamWriter writer = new JsonStreamWriter();
    result.write(writer);
    Assert.assertEquals("{\"result\":null,\"failure\":null,\"failure_code\":null}", writer.toString());
    RemoteResult copy = new RemoteResult(new JsonStreamReader(writer.toString()));
    Assert.assertEquals(result.result, copy.result);
    Assert.assertEquals(result.failure, copy.failure);
    Assert.assertEquals(result.failureCode, copy.failureCode);
    Assert.assertEquals(result, copy);
    Assert.assertEquals(result.hashCode(), copy.hashCode());
    Assert.assertFalse(result.equals(null));
    Assert.assertFalse(result.equals("XYZ"));
  }

  @Test
  public void success() {
    RemoteResult result = new RemoteResult("{}", null, null);
    JsonStreamWriter writer = new JsonStreamWriter();
    result.write(writer);
    Assert.assertEquals("{\"result\":{},\"failure\":null,\"failure_code\":null}", writer.toString());
    RemoteResult copy = new RemoteResult(new JsonStreamReader(writer.toString()));
    Assert.assertEquals(result.result, copy.result);
    Assert.assertEquals(result.failure, copy.failure);
    Assert.assertEquals(result.failureCode, copy.failureCode);
    Assert.assertEquals(result, copy);
    Assert.assertEquals(result.hashCode(), copy.hashCode());
    Assert.assertFalse(result.equals(null));
    Assert.assertFalse(result.equals("XYZ"));
  }

  @Test
  public void failure() {
    RemoteResult result = new RemoteResult(null, "No", 1000);
    JsonStreamWriter writer = new JsonStreamWriter();
    result.write(writer);
    Assert.assertEquals("{\"result\":null,\"failure\":\"No\",\"failure_code\":1000}", writer.toString());
    RemoteResult copy = new RemoteResult(new JsonStreamReader(writer.toString()));
    Assert.assertEquals(result.result, copy.result);
    Assert.assertEquals(result.failure, copy.failure);
    Assert.assertEquals(result.failureCode, copy.failureCode);
    Assert.assertEquals(result, copy);
    Assert.assertEquals(result.hashCode(), copy.hashCode());
    Assert.assertFalse(result.equals(null));
    Assert.assertFalse(result.equals("XYZ"));
  }

  @Test
  public void allthings() {
    RemoteResult result = new RemoteResult("{}", "Nope", 82);
    JsonStreamWriter writer = new JsonStreamWriter();
    result.write(writer);
    Assert.assertEquals("{\"result\":{},\"failure\":\"Nope\",\"failure_code\":82}", writer.toString());
    RemoteResult copy = new RemoteResult(new JsonStreamReader(writer.toString()));
    Assert.assertEquals(result.result, copy.result);
    Assert.assertEquals(result.failure, copy.failure);
    Assert.assertEquals(result.failureCode, copy.failureCode);
    Assert.assertEquals(result, copy);
    Assert.assertEquals(result.hashCode(), copy.hashCode());
    Assert.assertFalse(result.equals(null));
    Assert.assertFalse(result.equals("XYZ"));
  }
}
