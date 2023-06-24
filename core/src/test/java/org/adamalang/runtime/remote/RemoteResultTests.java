/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
