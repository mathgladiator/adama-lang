/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockLivingDocument;
import org.adamalang.runtime.mocks.MockRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RxTableTests {
  @Test
  public void memory() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    Assert.assertEquals(176, table.__memory());
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    Assert.assertEquals(462, table.__memory());
  }

  private MockLivingDocument doc(int keyStart) {
    final var document = new MockLivingDocument();
    while (document.__genNextAutoKey() < keyStart - 1) {}
    return document;
  }

  @Test
  public void dump() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var writer = new JsonStreamWriter();
    table.__dump(writer);
    Assert.assertEquals(
        "{\"4\":{\"data\":\"\",\"index\":13},\"5\":{\"data\":\"\",\"index\":12},\"6\":{\"data\":\"\",\"index\":13}}",
        writer.toString());
  }

  @Test
  public void fast_create_and_delete_churn() {
    MockLivingDocument document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var a = table.make(); // 7
    table.make().__delete(); // 8
    final var b = table.make(); // 9
    {
      final var writer = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      table.__commit("t", writer, reverse);
      Assert.assertEquals(
          "\"t\":{\"7\":{\"data\":\"\",\"index\":0},\"9\":{\"data\":\"\",\"index\":0}}",
          writer.toString());
      Assert.assertEquals("\"t\":{\"7\":null,\"9\":null}", reverse.toString());
    }
    {
      final var writer = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      table.__commit("t", writer, reverse);
      Assert.assertEquals("", writer.toString());
      Assert.assertEquals("", reverse.toString());
    }
    {
      Assert.assertEquals(5, table.size());
      a.__delete();
      Assert.assertEquals(4, table.size());
      final var writer = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      table.__commit("t", writer, reverse);
      Assert.assertEquals("\"t\":{\"7\":null}", writer.toString());
      Assert.assertEquals("\"t\":{\"7\":{\"data\":\"\",\"index\":0}}", reverse.toString());
    }
    {
      Assert.assertEquals(4, table.size());
      b.__delete();
      Assert.assertEquals(3, table.size());
      final var writer = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      table.__commit("t", writer, reverse);
      Assert.assertEquals("\"t\":{\"9\":null}", writer.toString());
      Assert.assertEquals("\"t\":{\"9\":{\"data\":\"\",\"index\":0}}", reverse.toString());
    }
  }

  @Test
  public void hydrate_indexing() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    Assert.assertEquals(4, table.getById(4).__id());
    Assert.assertNull(table.getById(500));
    Assert.assertEquals(3, table.size());
    Assert.assertEquals(2, table.getIndex((short) 0).of(13, IndexQuerySet.LookupMode.Equals).size());
  }

  @Test
  public void id_seed() {
    final var document = doc(4);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(new JsonStreamReader("{}"));
    Assert.assertEquals(4, table.make().id);
    Assert.assertEquals(5, table.make().id);
    Assert.assertEquals(6, table.make().id);
  }

  @Test
  public void idgen() {
    final var document = new MockLivingDocument();
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    Assert.assertEquals(1, table.make().id);
    Assert.assertEquals(2, table.make().id);
    Assert.assertEquals(3, table.make().id);
  }

  @Test
  public void indexing() {
    final var document = doc(4);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(new JsonStreamReader("{}"));
    final var a = table.make();
    final var b = table.make();
    final var c = table.make();
    a.index.set(13);
    b.index.set(13);
    c.index.set(13);
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    table.__commit("t", writer, reverse);
    Assert.assertEquals(
        "\"t\":{\"4\":{\"data\":\"\",\"index\":13},\"5\":{\"data\":\"\",\"index\":13},\"6\":{\"data\":\"\",\"index\":13}}",
        writer.toString());
    Assert.assertEquals("\"t\":{\"4\":null,\"5\":null,\"6\":null}", reverse.toString());
    Assert.assertEquals(4, table.getById(4).__id());
    Assert.assertNull(table.getById(500));
    Assert.assertEquals(3, table.getIndex((short) 0).of(13, IndexQuerySet.LookupMode.Equals).size());
    Assert.assertEquals(3, table.size());
  }

  @Test
  public void insert_deleta() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    Assert.assertEquals(3, table.size());
    table.__insert(new JsonStreamReader("{\"4\":null,\"5\":null,\"6\":{\"index\":52}}"));
    Assert.assertEquals(1, table.size());
  }

  @Test
  public void patch_delete() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__patch(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    Assert.assertEquals(3, table.size());
    table.__patch(new JsonStreamReader("{\"4\":null,\"5\":null,\"6\":{\"index\":52}}"));
    Assert.assertEquals(1, table.size());
  }

  @Test
  public void noindex() {
    final var document = new MockLivingDocument();
    new RxTable<>(document, document, "name", MockRecord::new, 0);
  }

  @Test
  public void revert_creates() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var r7 = table.make();
    Assert.assertNotNull(table.getById(7));
    table.__revert();
    Assert.assertTrue(r7.__isDying());
    Assert.assertNull(table.getById(7));
  }

  @Test
  public void revert_item_changes() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var r4 = table.getById(4);
    r4.index.set(23);
    Assert.assertEquals(23, (int) r4.index.get());
    table.__revert();
    Assert.assertEquals(13, (int) r4.index.get());
  }

  @Test
  public void revert_item_deletes() {
    final var document = new MockLivingDocument();
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var r4 = table.getById(4);
    r4.__delete();
    Assert.assertTrue(r4.__isDying());
    table.__revert();
    Assert.assertFalse(r4.__isDying());
    Assert.assertNotNull(table.getById(4));
  }

  @Test
  public void scanning_dumb_filter() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var records = new ArrayList<MockRecord>();
    for (final MockRecord mr :
        table.scan(
            new WhereClause<MockRecord>() {
              @Override
              public int[] getIndices() {
                throw new UnsupportedOperationException();
              }

              @Override
              public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
              }

              @Override
              public void scopeByIndicies(final IndexQuerySet __set) {
                // do nothing, therefere dumb
              }

              @Override
              public boolean test(final MockRecord item) {
                return false;
              }
            })) {
      records.add(mr);
    }
    Assert.assertEquals(3, records.size());
  }

  @Test
  public void scanning_empty_index_eliminates() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var records = new ArrayList<MockRecord>();
    for (final MockRecord mr :
        table.scan(
            new WhereClause<MockRecord>() {
              @Override
              public int[] getIndices() {
                throw new UnsupportedOperationException();
              }

              @Override
              public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
              }

              @Override
              public void scopeByIndicies(final IndexQuerySet __set) {
                __set.intersect(0, 10000, IndexQuerySet.LookupMode.Equals);
              }

              @Override
              public boolean test(final MockRecord item) {
                return false;
              }
            })) {
      records.add(mr);
    }
    Assert.assertEquals(0, records.size());
  }

  @Test
  public void scanning_intersect_exact_again() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var records = new ArrayList<MockRecord>();
    for (final MockRecord mr :
        table.scan(
            new WhereClause<MockRecord>() {
              @Override
              public int[] getIndices() {
                throw new UnsupportedOperationException();
              }

              @Override
              public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
              }

              @Override
              public void scopeByIndicies(final IndexQuerySet __set) {
                __set.intersect(0, 13, IndexQuerySet.LookupMode.Equals);
                __set.intersect(0, 13, IndexQuerySet.LookupMode.Equals);
              }

              @Override
              public boolean test(final MockRecord item) {
                return false;
              }
            })) {
      records.add(mr);
    }
    Assert.assertEquals(2, records.size());
  }

  @Test
  public void scanning_intersect_self() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var records = new ArrayList<MockRecord>();
    for (final MockRecord mr :
        table.scan(
            new WhereClause<MockRecord>() {
              @Override
              public int[] getIndices() {
                throw new UnsupportedOperationException();
              }

              @Override
              public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
              }

              @Override
              public void scopeByIndicies(final IndexQuerySet __set) {
                __set.intersect(0, 13, IndexQuerySet.LookupMode.Equals);
                __set.intersect(0, 12, IndexQuerySet.LookupMode.Equals);
              }

              @Override
              public boolean test(final MockRecord item) {
                return false;
              }
            })) {
      records.add(mr);
    }
    Assert.assertEquals(0, records.size());
  }

  @Test
  public void scanning_no_filter() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}"));
    final var records = new ArrayList<MockRecord>();
    for (final MockRecord mr : table.scan(null)) {
      records.add(mr);
    }
    Assert.assertEquals(3, records.size());
  }

  @Test
  public void scanning_use_the_index() {
    final var document = doc(7);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    final var records = new ArrayList<MockRecord>();
    for (final MockRecord mr :
        table.scan(
            new WhereClause<MockRecord>() {
              @Override
              public int[] getIndices() {
                throw new UnsupportedOperationException();
              }

              @Override
              public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
              }

              @Override
              public void scopeByIndicies(final IndexQuerySet __set) {
                __set.intersect(0, 13, IndexQuerySet.LookupMode.Equals);
              }

              @Override
              public boolean test(final MockRecord item) {
                return false;
              }
            })) {
      records.add(mr);
    }
    Assert.assertEquals(2, records.size());
  }

  @Test
  public void subscriberCleanUpOnTables() {
    final var document = doc(7);
    final var common = new RxLazy<>(null, () -> 42);
    final var table = new RxTable<>(document, document, "name", MockRecord::new, 1);
    table.__insert(
        new JsonStreamReader("{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}"));
    Assert.assertEquals(0, table.__getSubscriberCount());
    for (final MockRecord mr : table) {
      common.__subscribe(mr);
    }
    common.__raiseInvalid();
    Assert.assertEquals(3, common.__getSubscriberCount());
    common.__raiseInvalid();
    Assert.assertEquals(3, common.__getSubscriberCount());
    for (final MockRecord mr : table) {
      mr.__delete();
    }
    common.__raiseInvalid();
    Assert.assertEquals(3, common.__getSubscriberCount());
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    table.__commit("t", writer, reverse);
    Assert.assertEquals("\"t\":{\"4\":null,\"5\":null,\"6\":null}", writer.toString());
    Assert.assertEquals(
        "\"t\":{\"4\":{\"data\":\"\",\"index\":13},\"5\":{\"data\":\"\",\"index\":12},\"6\":{\"data\":\"\",\"index\":13}}",
        reverse.toString());
    Assert.assertEquals(3, common.__getSubscriberCount());
    common.__raiseInvalid();
    Assert.assertEquals(0, common.__getSubscriberCount());
  }
}
