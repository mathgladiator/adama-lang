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
package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

public class DRecordListTests {
  @Test
  public void flow() {
    final var list = new DRecordList<DBoolean>();
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
      final var delta = writer.planObject();
      final var walk = list.begin();
      walk.next(42);
      final var a = list.getPrior(42, DBoolean::new);
      a.show(true, delta.planField(42));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"42\":true,\"@o\":[42]}", stream.toString());
      Assert.assertEquals(112, list.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
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
      Assert.assertEquals(224, list.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
      final var delta = writer.planObject();
      final var walk = list.begin();
      walk.next(42);
      final var b = list.getPrior(42, DBoolean::new);
      b.show(true, delta.planField(42));
      walk.end(delta);
      delta.end();
      Assert.assertEquals("{\"@o\":[42],\"10\":null}", stream.toString());
      Assert.assertEquals(112, list.__memory());
    }
    {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
      list.hide(writer);
      list.hide(writer);
      Assert.assertEquals("null", stream.toString());
      Assert.assertEquals(0, list.__memory());
    }
  }

  @Test
  public void range() {
    final var list = new DRecordList<DBoolean>();
    final Function<Integer[], String> process = inserts -> {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
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
    Assert.assertEquals("{\"42\":true,\"100\":true,\"50\":true,\"@o\":[42,100,50]}", process.apply(new Integer[]{42, 100, 50}));
    Assert.assertEquals("{\"23\":true,\"@o\":[23,[0,2]]}", process.apply(new Integer[]{23, 42, 100, 50}));
    Assert.assertEquals("{\"77\":true,\"980\":true,\"@o\":[[0,3],77,980]}", process.apply(new Integer[]{23, 42, 100, 50, 77, 980}));
    Assert.assertEquals("{\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"@o\":[1,2,3,4],\"50\":null,\"100\":null,\"980\":null,\"23\":null,\"42\":null,\"77\":null}", process.apply(new Integer[]{1, 2, 3, 4}));
    Assert.assertEquals("{\"0\":true,\"5\":true,\"6\":true,\"@o\":[0,[0,3],5,6]}", process.apply(new Integer[]{0, 1, 2, 3, 4, 5, 6}));
    list.clear();
  }

  @Test
  public void range_removal_regression() {
    final var list = new DRecordList<DBoolean>();
    final Function<Integer[], String> process = inserts -> {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
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
    Assert.assertEquals("{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"5\":true,\"6\":true,\"7\":true,\"8\":true,\"9\":true,\"@o\":[0,1,2,3,4,5,6,7,8,9]}", process.apply(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
    // { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }
    // -5
    // { [0, 4], [6
    Assert.assertEquals("{\"@o\":[[0,4],[6,9]],\"5\":null}", process.apply(new Integer[]{0, 1, 2, 3, 4, 6, 7, 8, 9}));
  }

  @Test
  public void to_nothing() {
    final var list = new DRecordList<DBoolean>();
    final Function<Integer[], String> process = inserts -> {
      final var stream = new JsonStreamWriter();
      final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
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
    Assert.assertEquals("{\"0\":true,\"1\":true,\"2\":true,\"3\":true,\"4\":true,\"6\":true,\"7\":true,\"8\":true,\"9\":true,\"@o\":[0,1,2,3,4,6,7,8,9]}", process.apply(new Integer[]{0, 1, 2, 3, 4, 6, 7, 8, 9}));
    Assert.assertEquals("{\"@o\":[],\"0\":null,\"1\":null,\"2\":null,\"3\":null,\"4\":null,\"6\":null,\"7\":null,\"8\":null,\"9\":null}", process.apply(new Integer[]{}));
  }
}
