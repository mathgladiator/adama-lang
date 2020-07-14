/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Function;

public class UtilityTests {
  @Test
  public void simple() throws Exception {
    Assert.assertTrue(Utility.parseJsonObject("{}").isObject());
    Assert.assertTrue(Utility.parseJsonObjectThrows("{}").isObject());
    Utility.createObjectNode();
    Utility.createArrayNode();
  }

  @Test
  public void parsing() {
    try {
      Utility.parseJsonObject("[]");
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("java.lang.Exception: given json is not an ObjectNode at root", re.getMessage());
    }
    try {
      Utility.parseJsonObjectThrows("[]");
      Assert.fail();
    } catch (Exception re) {
      Assert.assertEquals("given json is not an ObjectNode at root", re.getMessage());
    }
    try {
      Utility.parseJsonObject("[");
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertTrue(re.getCause() instanceof JsonProcessingException);
    }
  }

  @Test
  public void coverage() {
    Utility.identity(123);
    Utility.identity(123L);
  }

  @Test
  public void lookup() {
    Integer[] X = new Integer[] {1, 3, 5};
    Assert.assertEquals(1, (int) Utility.lookup(X, 0).get());
    Assert.assertEquals(3, (int) Utility.lookup(X, 1).get());
    Assert.assertEquals(5, (int) Utility.lookup(X, 2).get());
    Assert.assertFalse(Utility.lookup(X, -1).has());
    Assert.assertFalse(Utility.lookup(X, 4).has());
  }

  @Test
  public void convert_single() {
    Assert.assertEquals(123, (int) Utility.convertSingle("123", (String s) -> Integer.parseInt(s)));
  }
  @Test
  public void convert_maybe() {
    NtMaybe<String> ms = new NtMaybe<>();
    Assert.assertFalse(Utility.convertMaybe(ms, (String s) -> Integer.parseInt(s)).has());
    ms.set("123");
    Assert.assertTrue(Utility.convertMaybe(ms, (String s) -> Integer.parseInt(s)).has());
    Assert.assertEquals(123, (int) Utility.convertMaybe(ms, (String s) -> Integer.parseInt(s)).get());
  }
  @Test
  public void convert_array() {
    Function<Integer, Integer[]> makeArray = (Integer n) -> new Integer[n];
    Function<String, Integer> conv = (String s) -> Integer.parseInt(s);
    String[] data = new String[] {"123", "42"};
    Integer[] arr = Utility.convertMultiple(data, makeArray, conv);
    Assert.assertEquals(123, (int) arr[0]);
    Assert.assertEquals(42, (int) arr[1]);
  }
  @Test
  public void convert_list() {
    Function<Integer, Integer[]> makeArray = (Integer n) -> new Integer[n];
    Function<String, Integer> conv = (String s) -> Integer.parseInt(s);
    String[] data = new String[] {"123", "42"};
    ArrayList<String> list = new ArrayList<>();
    list.add("123");
    list.add("42");
    Integer[] arr = Utility.convertMultiple(list, makeArray, conv);
    Assert.assertEquals(123, (int) arr[0]);
    Assert.assertEquals(42, (int) arr[1]);
  }



}
