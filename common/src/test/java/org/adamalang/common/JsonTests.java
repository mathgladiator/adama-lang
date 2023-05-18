/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class JsonTests {
  @Test
  public void coverage() throws Exception {
    Json.newJsonObject();
    Json.parseJsonObject("{}");
    boolean failure = true;
    try {
      Json.parseJsonObjectThrows("x");
      failure = false;
    } catch (Exception ex) {

    }
    try {
      Json.parseJsonObjectThrows("[]");
      failure = false;
    } catch (Exception ex) {

    }
    try {
      Json.parseJsonObject("x");
      failure = false;
    } catch (RuntimeException ex) {
    }
    try {
      Json.parseJsonObject("[]");
      failure = false;
    } catch (RuntimeException ex) {
    }
    Json.parseJsonArray("[]");
    try {
      Json.parseJsonArray("{}");
      failure = false;
    } catch (Exception ex) {

    }
    try {
      Json.parseJsonArray("x");
      failure = false;
    } catch (Exception ex) {

    }
    Assert.assertTrue(failure);
  }
}
