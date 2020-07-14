/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.bridges.NativeBridge;
import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.mocks.MockLivingDocument;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRecordBridge;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RxTableTests {

    @Test
    public void idgen() {
        MockLivingDocument document = new MockLivingDocument();
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{}"), "t", new MockRecordBridge());
        Assert.assertEquals(0, table.make().id);
        Assert.assertEquals(1, table.make().id);
        Assert.assertEquals(2, table.make().id);
    }

    @Test
    public void id_seed() {
        MockLivingDocument document = new MockLivingDocument();
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":4}}"), "t", new MockRecordBridge());
        Assert.assertEquals(4, table.make().id);
        Assert.assertEquals(5, table.make().id);
        Assert.assertEquals(6, table.make().id);
    }

    @Test
    public void indexing() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":4}}"), "t", bridge);
        MockRecord a = table.make();
        MockRecord b = table.make();
        MockRecord c = table.make();
        a.index.set(13);
        b.index.set(13);
        c.index.set(13);
        ObjectNode delta = Utility.createObjectNode();
        table.__commit("t", delta);
        Assert.assertEquals("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}", delta.toString());
        Assert.assertEquals(4, table.getById(4).__id());
        Assert.assertNull(table.getById(500));
        Assert.assertEquals(3, table.getIndex((short)0).of(13).size());
        Assert.assertEquals(3, table.size());
    }

    @Test
    public void hydrate_indexing() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        Assert.assertEquals(4, table.getById(4).__id());
        Assert.assertNull(table.getById(500));
        Assert.assertEquals(3, table.size());
        Assert.assertEquals(3, table.getIndex((short)0).of(13).size());
    }

    @Test
    public void fast_create_and_delete_churn() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        MockRecord a = table.make();
        table.make().__delete();
        MockRecord b = table.make();
        {
            ObjectNode delta = Utility.createObjectNode();
            table.__commit("t", delta);
            Assert.assertEquals("{\"t\":{\"auto_key\":10,\"rows\":{\"7\":{},\"9\":{}}}}", delta.toString());
        }
        {
            ObjectNode delta = Utility.createObjectNode();
            table.__commit("t", delta);
            Assert.assertEquals("{}", delta.toString());
        }
        {
            Assert.assertEquals(5, table.size());
            a.__delete();
            Assert.assertEquals(4, table.size());
            ObjectNode delta = Utility.createObjectNode();
            table.__commit("t", delta);
            Assert.assertEquals("{\"t\":{\"auto_key\":10,\"rows\":{\"7\":null}}}", delta.toString());
        }
        {
            Assert.assertEquals(4, table.size());
            b.__delete();
            Assert.assertEquals(3, table.size());
            ObjectNode delta = Utility.createObjectNode();
            table.__commit("t", delta);
            Assert.assertEquals("{\"t\":{\"auto_key\":10,\"rows\":{\"9\":null}}}", delta.toString());
        }
    }

    @Test
    public void revert_item_changes() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        MockRecord r4 = table.getById(4);
        r4.index.set(23);
        Assert.assertEquals(23, (int) r4.index.get());
        table.__revert();
        Assert.assertEquals(13, (int) r4.index.get());
    }

    @Test
    public void revert_item_deletes() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        MockRecord r4 = table.getById(4);
        r4.__delete();
        Assert.assertTrue(r4.__isDying());
        table.__revert();
        Assert.assertFalse(r4.__isDying());
        Assert.assertNotNull(table.getById(4));
    }

    @Test
    public void revert_creates() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        MockRecord r7 = table.make();
        Assert.assertNotNull(table.getById(7));
        table.__revert();
        Assert.assertTrue(r7.__isDying());
        Assert.assertNull(table.getById(7));
    }

    @Test
    public void scanning_no_filter() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = false;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        ArrayList<MockRecord> records = new ArrayList<>();
        for (MockRecord mr : table.scan(null)) {
            records.add(mr);
        }
        Assert.assertEquals(3, records.size());
    }


    @Test
    public void scanning_dumb_filter() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = false;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        ArrayList<MockRecord> records = new ArrayList<>();
        for (MockRecord mr : table.scan(new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean test(MockRecord item) {
                return false;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
                // do nothing, therefere dumb
            }
        })) {
            records.add(mr);
        }
        Assert.assertEquals(3, records.size());
    }

    @Test
    public void scanning_empty_index_eliminates() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":13},\"6\":{\"index\":13}}}}"), "t", bridge);
        ArrayList<MockRecord> records = new ArrayList<>();
        for (MockRecord mr : table.scan(new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean test(MockRecord item) {
                return false;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
                __set.intersect(0, 10000);
            }
        })) {
            records.add(mr);
        }
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void scanning_use_the_index() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        ArrayList<MockRecord> records = new ArrayList<>();
        for (MockRecord mr : table.scan(new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean test(MockRecord item) {
                return false;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
                __set.intersect(0, 13);
            }
        })) {
            records.add(mr);
        }
        Assert.assertEquals(2, records.size());
    }


    @Test
    public void scanning_intersect_self() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        ArrayList<MockRecord> records = new ArrayList<>();
        for (MockRecord mr : table.scan(new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean test(MockRecord item) {
                return false;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
                __set.intersect(0, 13);
                __set.intersect(0, 12);
            }
        })) {
            records.add(mr);
        }
        Assert.assertEquals(0, records.size());
    }

    @Test
    public void scanning_intersect_exact_again() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        ArrayList<MockRecord> records = new ArrayList<>();
        for (MockRecord mr : table.scan(new WhereClause<MockRecord>() {
            @Override
            public int[] getIndices() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer getPrimaryKey() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean test(MockRecord item) {
                return false;
            }

            @Override
            public void scopeByIndicies(IndexQuerySet __set) {
                __set.intersect(0, 13);
                __set.intersect(0, 13);
            }
        })) {
            records.add(mr);
        }
        Assert.assertEquals(2, records.size());
    }

    @Test
    public void subscriberCleanUpOnTables() {
        MockLivingDocument document = new MockLivingDocument();
        MockRecordBridge bridge = new MockRecordBridge();
        bridge.index = true;
        RxLazy<Integer> common = new RxLazy<>(null, NativeBridge.INTEGER_NATIVE_SUPPORT, () -> 42);
        RxTable<MockRecord> table = RxFactory.makeRxTable(document, null, Utility.parseJsonObject("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":{\"index\":13},\"5\":{\"index\":12},\"6\":{\"index\":13}}}}"), "t", bridge);
        Assert.assertEquals(0, table.__getSubscriberCount());
        for (MockRecord mr : table) {
            common.__subscribe(mr);
        }
        common.__raiseInvalid();
        Assert.assertEquals(3, common.__getSubscriberCount());
        common.__raiseInvalid();
        Assert.assertEquals(3, common.__getSubscriberCount());
        for (MockRecord mr : table) {
            mr.__delete();
        }
        common.__raiseInvalid();
        Assert.assertEquals(3, common.__getSubscriberCount());
        ObjectNode delta = Utility.createObjectNode();
        table.__commit("t", delta);
        Assert.assertEquals("{\"t\":{\"auto_key\":7,\"rows\":{\"4\":null,\"5\":null,\"6\":null}}}", delta.toString());
        Assert.assertEquals(3, common.__getSubscriberCount());
        common.__raiseInvalid();
        Assert.assertEquals(0, common.__getSubscriberCount());
    }
}
