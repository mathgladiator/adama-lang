/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

public class DRecordListTests {
  @Test
  public void flow() {
    final var list = new DRecordList<DBoolean>();
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      final var walk = list.begin();
      walk.next(42);
      final var a = list.getPrior(42, DBoolean::new);
      a.show(true, delta.planField(42));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"42\":true,\"@o\":[42]}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      final var walk = list.begin();
      walk.next(10);
      final var a = list.getPrior(10, DBoolean::new);
      a.show(false, delta.planField(10));
      walk.next(42);
      final var b = list.getPrior(42, DBoolean::new);
      b.show(true, delta.planField(42));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"10\":false,\"@o\":[10,42]}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      final var delta = writer.planObject();
      final var walk = list.begin();
      walk.next(42);
      final var b = list.getPrior(42, DBoolean::new);
      b.show(true, delta.planField(42));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"@o\":[42],\"10\":null}", stream.toString());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
      list.hide(writer);
      list.hide(writer);
      Assert.assertEquals("null", stream.toString());
    }
  }

  @Test
  public void range() {
    final var list = new DRecordList<DBoolean>();
    final Function<Integer[], String> process =
        inserts -> {
          final var stream = new JsonStreamWriter();
          final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
          final var delta = writer.planObject();
          final var walk = list.begin();
          for (final Integer insert : inserts) {
            walk.next(insert);
            final var a = list.getPrior(insert, DBoolean::new);
            a.show(true, delta.planField(insert));
          }
          walk.end(delta);
          delta.end();
          return stream.toString();
        };
    Assert.assertEquals(
        "{\"42\":true,\"100\":true,\"50\":true,\"@o\":[42,100,50]}",
        process.apply(new Integer[] {42, 100, 50}));
    Assert.assertEquals(
        "{\"23\":true,\"@o\":[23,[0,2]]}", process.apply(new Integer[] {23, 42, 100, 50}));
    Assert.assertEquals(
        "{\"77\":true,\"980\":true,\"@o\":[[0,3],77,980]}",
        process.apply(new Integer[] {23, 42, 100, 50, 77, 980}));
    Assert.assertEquals(
        "{\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"@o\":[1,2,3,4],\"50\":null,\"100\":null,\"980\":null,\"23\":null,\"42\":null,\"77\":null}",
        process.apply(new Integer[] {1, 2, 3, 4}));
    Assert.assertEquals(
        "{\"0\":true,\"5\":true,\"6\":true,\"@o\":[0,[0,3],5,6]}",
        process.apply(new Integer[] {0, 1, 2, 3, 4, 5, 6}));
  }

  @Test
  public void range_removal_regression() {
    final var list = new DRecordList<DBoolean>();
    final Function<Integer[], String> process =
        inserts -> {
          final var stream = new JsonStreamWriter();
          final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
          final var delta = writer.planObject();
          final var walk = list.begin();
          for (final Integer insert : inserts) {
            walk.next(insert);
            final var a = list.getPrior(insert, DBoolean::new);
            a.show(true, delta.planField(insert));
          }
          walk.end(delta);
          delta.end();
          return stream.toString();
        };
    Assert.assertEquals(
        "{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"5\":true,\"6\":true,\"7\":true,\"8\":true,\"9\":true,\"@o\":[0,1,2,3,4,5,6,7,8,9]}",
        process.apply(new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
    // { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }
    // -5
    // { [0, 4], [6
    Assert.assertEquals(
        "{\"@o\":[[0,4],[6,9]],\"5\":null}",
        process.apply(new Integer[] {0, 1, 2, 3, 4, 6, 7, 8, 9}));
  }
}
