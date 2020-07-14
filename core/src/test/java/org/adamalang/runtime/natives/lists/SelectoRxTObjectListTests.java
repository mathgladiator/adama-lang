/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives.lists;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.bridges.NativeBridge;
import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.mocks.MockLivingDocument;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRecordBridge;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.ops.SilentDocumentMonitor;
import org.adamalang.runtime.reactives.RxFactory;
import org.adamalang.runtime.reactives.RxLazy;
import org.adamalang.runtime.reactives.RxTable;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class SelectoRxTObjectListTests {
    @Test
    public void flow() {
        MockLivingDocument document = new MockLivingDocument();
        ObjectNode data = Utility.createObjectNode();
        MockRecordBridge bridge = new MockRecordBridge();
        RxTable<MockRecord> table = new RxTable<>(document, "tname", data, document, bridge);
    }

    @Test
    public void table_iterate_lookup() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":8,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13},\"7\":{\"index\":5}}}}"), "t", bridge);
        table.getById(7).__delete();
        Assert.assertEquals(4, table.iterate(true).get().lookup(0).get().__id());
    }

    @Test
    public void table_iterate_where_pkey_lookup() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        WhereClause<MockRecord> where = new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer getPrimaryKey() {
                return 5;
            }

            @Override
            public boolean test(MockRecord item) {
                return true;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
                throw new UnsupportedOperationException();
            }
        };
        Assert.assertEquals(5, table.iterate(true).get().where(true, where).lookup(0).get().__id());
    }

    @Test
    public void table_iterate_where_pkey_lookup_dead() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxLazy<Integer> common = new RxLazy<>(null, NativeBridge.INTEGER_NATIVE_SUPPORT, () -> 42);
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        table.getById(5).__delete();
        WhereClause<MockRecord> where = new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer getPrimaryKey() {
                return 5;
            }

            @Override
            public boolean test(MockRecord item) {
                return true;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
                throw new UnsupportedOperationException();
            }
        };
        NtList<MockRecord> list = table.iterate(false).get().where(true, where);
        Assert.assertEquals(0, list.size());
        Assert.assertEquals(false, list.lookup(0).has());
    }

    @Test
    public void table_iterate_where_brute_force() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":8,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13},\"7\":{\"index\":5}}}}"), "t", bridge);
        table.getById(7).__delete();
        WhereClause<MockRecord> where = new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer getPrimaryKey() {
                return null;
            }

            @Override
            public boolean test(MockRecord item) {
                return item.__id() == 5;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
            }
        };
        Assert.assertEquals(5, table.iterate(true).get().where(true, where).lookup(0).get().__id());
    }

    @Test
    public void table_iterate_shuffle() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        Random rng = new Random(0);
        NtList<MockRecord> list = table.iterate(false).shuffle(true, rng);
        Assert.assertEquals(5, list.lookup(0).get().__id());
        Assert.assertEquals(6, list.lookup(1).get().__id());
        Assert.assertEquals(4, list.lookup(2).get().__id());
    }

    @Test
    public void table_iterate_limit() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        Random rng = new Random(0);
        NtList<MockRecord> list = table.iterate(false).skipAndLimit(true, 0, 1);
        Assert.assertEquals(4, list.lookup(0).get().__id());
    }

    @Test
    public void table_iterate_shuffle_then_order() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        Random rng = new Random(0);
        NtList<MockRecord> list = table.iterate(false).orderBy(true, (a, b) -> -Integer.compare(a.id, b.id));
        Assert.assertEquals(6, list.lookup(0).get().__id());
        Assert.assertEquals(5, list.lookup(1).get().__id());
        Assert.assertEquals(4, list.lookup(2).get().__id());
    }

    @Test
    public void table_iterate_reduce() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        Random rng = new Random(0);
        NtMap<Integer, Integer> map = table.iterate(false).reduce((mr) -> mr.index.get(), (lmr) -> lmr.size());
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(3, (int) map.lookup(13).get());
    }

    @Test
    public void table_iterate_size() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        Assert.assertEquals(3, table.iterate(false).size());
    }

    @Test
    public void table_iterate_basics() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        MockRecord[] arr = table.iterate(true).toArray();
        int count = 0;
        for (MockRecord mr : table.iterate(true)) {
            count++;
        }
        Assert.assertEquals(3, count);
    }

    @Test
    public void table_iterate_delete() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        table.iterate(true).__delete();
        ObjectNode delta = Utility.createObjectNode();
        table.__commit("t", delta);
        Assert.assertEquals("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":null,\"5\":null,\"6\":null}}}", delta.toString());
    }

    @Test
    public void table_iterate_map() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        table.iterate(true).map((mr) -> mr.data.set("cake"));
        ObjectNode delta = Utility.createObjectNode();
        table.__commit("t", delta);
        Assert.assertEquals("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"data\":\"cake\"},\"5\":{\"data\":\"cake\"},\"6\":{\"data\":\"cake\"}}}}", delta.toString());
    }

    @Test
    public void table_iterate_transform() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        table.iterate(true).transform((mr) -> mr.index.bumpUpPost(), NativeBridge.INTEGER_NATIVE_SUPPORT);
        ObjectNode delta = Utility.createObjectNode();
        table.__commit("t", delta);
        Assert.assertEquals("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":14},\"5\":{\"index\":14},\"6\":{\"index\":14}}}}", delta.toString());
    }

    @Test
    public void table_where_effectiveness() {
        MockLivingDocument document = new MockLivingDocument(new SilentDocumentMonitor() {
            @Override
            public boolean shouldMeasureTableColumnIndexEffectiveness() {
                return true;
            }
        });
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":8,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":2},\"6\":{\"index\":5},\"7\":{\"index\":5}}}}"), "t", bridge);
        table.getById(7).__delete();
        NtList<MockRecord> list = table.iterate(false).where(false, new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                return new int[] { 0, 13};
            }

            @Override
            public Integer getPrimaryKey() {
                return null;
            }

            @Override
            public boolean test(MockRecord item) {
                return item.index.get() == 13;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
            }
        });
        Assert.assertEquals(1, list.size());
    }
}
