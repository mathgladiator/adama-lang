/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.runtime.reactives.RxClient;
import org.junit.Assert;
import org.junit.Test;

public class NtClientTests {
  @Test
  public void comparisons() {
    NtClient cv1 = new NtClient("a", "b");
    NtClient cv2 = new NtClient("b", "b");
    NtClient cv3 = new NtClient("b", "a");
    NtClient cv4 = new NtClient("b", "c");
    Assert.assertEquals(-1, cv1.compareTo(cv2));
    Assert.assertEquals(1, cv1.compareTo(cv3));
    Assert.assertEquals(-1, cv1.compareTo(cv4));
    Assert.assertEquals(1, cv2.compareTo(cv1));
    Assert.assertEquals(1, cv2.compareTo(cv3));
    Assert.assertEquals(-1, cv2.compareTo(cv4));
    Assert.assertEquals(-1, cv3.compareTo(cv1));
    Assert.assertEquals(-1, cv3.compareTo(cv2));
    Assert.assertEquals(-2, cv3.compareTo(cv4));
    Assert.assertEquals(1, cv4.compareTo(cv1));
    Assert.assertEquals(1, cv4.compareTo(cv2));
    Assert.assertEquals(2, cv4.compareTo(cv3));
    Assert.assertEquals(0, cv1.compareTo(cv1));
    Assert.assertEquals(0, cv2.compareTo(cv2));
    Assert.assertEquals(0, cv3.compareTo(cv3));
    Assert.assertEquals(0, cv4.compareTo(cv4));
    Assert.assertFalse(cv1.equals(cv2));
    Assert.assertFalse(cv1.equals(cv3));
    Assert.assertFalse(cv1.equals(cv4));
    Assert.assertFalse(cv2.equals(cv1));
    Assert.assertFalse(cv2.equals(cv3));
    Assert.assertFalse(cv2.equals(cv4));
    Assert.assertFalse(cv3.equals(cv1));
    Assert.assertFalse(cv3.equals(cv2));
    Assert.assertFalse(cv3.equals(cv4));
    Assert.assertFalse(cv4.equals(cv1));
    Assert.assertFalse(cv4.equals(cv2));
    Assert.assertFalse(cv4.equals(cv3));
    Assert.assertTrue(cv1.equals(cv1));
    Assert.assertTrue(cv2.equals(cv2));
    Assert.assertTrue(cv3.equals(cv3));
    Assert.assertTrue(cv4.equals(cv4));
    Assert.assertFalse(cv4.equals("sys"));
  }

  @Test
  public void coverage() {
    NtClient.NO_ONE.toString();
    ObjectNode on = Utility.createObjectNode();
    NtClient.NO_ONE.dump(on);
    NtClient got = NtClient.from(on);
    Assert.assertEquals(got, NtClient.NO_ONE);
    Assert.assertEquals("{\"agent\":\"?\",\"authority\":\"?\"}", on.toString());
  }
  
  @Test
  public void from() {
    NtClient a = NtClient.from(null);
    Assert.assertEquals(a, NtClient.NO_ONE);
    NtClient b = NtClient.from(Utility.createArrayNode());
    Assert.assertEquals(b, NtClient.NO_ONE);
    Assert.assertEquals(2016, a.hashCode());
  }
}
