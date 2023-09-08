/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.gossip;

import org.junit.Assert;
import org.junit.Test;

public class InstanceTests extends CommonTest {

  @Test
  public void flow() {
    Instance instance = new Instance(newBuilder().setCounter(224).setId("id").setIp("ip").setRole("proxy").setPort(4242).setMonitoringPort(10002).build(), 0, false);
    Assert.assertEquals(224, instance.counter());
    instance.bump(12);
    Assert.assertEquals(225, instance.counter());
    Assert.assertEquals(12, instance.witnessed());
    instance.absorb(0, 254242);
    Assert.assertEquals(225, instance.counter());
    Assert.assertEquals(12, instance.witnessed());
    instance.absorb(500, 254242);
    Assert.assertEquals(500, instance.counter());
    Assert.assertEquals(254242, instance.witnessed());
    Assert.assertFalse(instance.canDelete(0));
    Assert.assertEquals("id", instance.toEndpoint().id);
    Assert.assertEquals("ip", instance.toEndpoint().ip);
    Assert.assertEquals(500, instance.toEndpoint().counter);
    Assert.assertEquals("proxy", instance.toEndpoint().role);
    Assert.assertEquals(4242, instance.toEndpoint().port);
    Assert.assertEquals("ip:4242", instance.target());
    Assert.assertEquals("proxy", instance.role());
    Assert.assertEquals(10002, instance.toEndpoint().monitoringPort);
    Assert.assertEquals(0, Instance.humanizeCompare(instance, instance));
    Instance other = new Instance(newBuilder().setCounter(224).setId("id").setIp("ip2").setRole("proxy").setPort(4242).setMonitoringPort(10002).build(), 0, false);
    Assert.assertEquals(-1, Instance.humanizeCompare(instance, other));
  }

  @Test
  public void deletion() {
    Instance instance = new Instance(newBuilder().setCounter(224).setId("id").setIp("ip").setRole("proxy").setPort(4242).build(), 0, false);
    instance.bump(100);
    Assert.assertFalse(instance.canDelete(100));
    Assert.assertFalse(instance.tooOldMustDelete(100));
    Assert.assertFalse(instance.canDelete(7599));
    Assert.assertFalse(instance.tooOldMustDelete(7599));
    Assert.assertFalse(instance.canDelete(7600));
    Assert.assertFalse(instance.tooOldMustDelete(7600));
    Assert.assertTrue(instance.canDelete(7601));
    Assert.assertFalse(instance.tooOldMustDelete(7601));
    Assert.assertTrue(instance.canDelete(10099));
    Assert.assertFalse(instance.tooOldMustDelete(10099));
    Assert.assertTrue(instance.canDelete(10100));
    Assert.assertFalse(instance.tooOldMustDelete(10100));
    Assert.assertTrue(instance.canDelete(10101));
    Assert.assertTrue(instance.tooOldMustDelete(10101));
  }

  @Test
  public void equals() {
    Instance instance1 = new Instance(newBuilder().setCounter(224).setId("id").setIp("ip").setRole("proxy").setPort(4242).build(), 0, false);
    Instance instance2 = new Instance(newBuilder().setCounter(224).setId("id").setIp("ip").setRole("proxy").setPort(4242).build(), 0, false);
    Instance instance3 = new Instance(newBuilder().setCounter(224).setId("id2").setIp("ip").setRole("proxy").setPort(4242).build(), 0, false);
    Assert.assertEquals(instance1, instance1);
    Assert.assertEquals(instance1, instance2);
    Assert.assertNotEquals(instance3, instance1);
    Assert.assertNotEquals(instance3, instance2);
    Assert.assertNotEquals(instance3, "x");
    Assert.assertNotEquals(instance3, 123);
  }

  @Test
  public void compare() {
    Instance instance1 = new Instance(newBuilder().setCounter(224).setId("1").setIp("ip").setRole("proxy").setPort(4242).build(), 0, false);
    Instance instance2 = new Instance(newBuilder().setCounter(224).setId("2").setIp("ip").setRole("proxy").setPort(4242).build(), 0, false);
    Assert.assertEquals(-1, instance1.compareTo(instance2));
    Assert.assertEquals(1, instance2.compareTo(instance1));
    Assert.assertEquals(0, instance1.compareTo(instance1));
    Assert.assertEquals(0, instance2.compareTo(instance2));
  }

  @Test
  public void hashing() {
    Instance instance1 = new Instance(newBuilder().setCounter(224).setId("id").setIp("ip").setRole("proxy").setPort(4242).build(), 0, false);
    Instance instance2 = new Instance(newBuilder().setCounter(224).setId("id").setIp("ip").setRole("proxy").setPort(4242).build(), 0, false);
    Assert.assertEquals(211180553, instance1.hashCode());
    Assert.assertEquals(211180553, instance2.hashCode());
  }
}
