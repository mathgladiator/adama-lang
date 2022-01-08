/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.gossip;

import org.adamalang.gossip.proto.Endpoint;
import org.junit.Assert;
import org.junit.Test;

public class InstanceTests {
  @Test
  public void flow() {
    Instance instance =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("id")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .setMonitoringPort(10002)
                .build(),
            0);
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
    Assert.assertEquals("id", instance.toEndpoint().getId());
    Assert.assertEquals("ip", instance.toEndpoint().getIp());
    Assert.assertEquals(500, instance.toEndpoint().getCounter());
    Assert.assertEquals("proxy", instance.toEndpoint().getRole());
    Assert.assertEquals(4242, instance.toEndpoint().getPort());
    Assert.assertEquals("ip:4242", instance.target());
    Assert.assertEquals("proxy", instance.role());
    Assert.assertEquals(10002, instance.toEndpoint().getMonitoringPort());
  }

  @Test
  public void deletion() {
    Instance instance =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("id")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .build(),
            0);
    instance.bump(100);
    Assert.assertFalse(instance.canDelete(100));
    Assert.assertFalse(instance.tooOldMustDelete(100));
    Assert.assertFalse(instance.canDelete(20099));
    Assert.assertFalse(instance.tooOldMustDelete(20099));
    Assert.assertFalse(instance.canDelete(20100));
    Assert.assertFalse(instance.tooOldMustDelete(20100));
    Assert.assertTrue(instance.canDelete(20101));
    Assert.assertFalse(instance.tooOldMustDelete(20101));
    Assert.assertTrue(instance.canDelete(25099));
    Assert.assertFalse(instance.tooOldMustDelete(25099));
    Assert.assertTrue(instance.canDelete(25100));
    Assert.assertFalse(instance.tooOldMustDelete(25100));
    Assert.assertTrue(instance.canDelete(25101));
    Assert.assertTrue(instance.tooOldMustDelete(25101));
  }

  @Test
  public void equals() {
    Instance instance1 =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("id")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .build(),
            0);
    Instance instance2 =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("id")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .build(),
            0);
    Instance instance3 =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("id2")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .build(),
            0);
    Assert.assertEquals(instance1, instance1);
    Assert.assertEquals(instance1, instance2);
    Assert.assertNotEquals(instance3, instance1);
    Assert.assertNotEquals(instance3, instance2);
    Assert.assertNotEquals(instance3, "x");
    Assert.assertNotEquals(instance3, 123);
  }

  @Test
  public void compare() {
    Instance instance1 =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("1")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .build(),
            0);
    Instance instance2 =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("2")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .build(),
            0);
    Assert.assertEquals(-1, instance1.compareTo(instance2));
    Assert.assertEquals(1, instance2.compareTo(instance1));
    Assert.assertEquals(0, instance1.compareTo(instance1));
    Assert.assertEquals(0, instance2.compareTo(instance2));
  }

  @Test
  public void hashing() {
    Instance instance1 =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("id")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .build(),
            0);
    Instance instance2 =
        new Instance(
            Endpoint.newBuilder()
                .setCounter(224)
                .setId("id")
                .setIp("ip")
                .setRole("proxy")
                .setPort(4242)
                .build(),
            0);
    Assert.assertEquals(211180553, instance1.hashCode());
    Assert.assertEquals(211180553, instance2.hashCode());
  }
}
