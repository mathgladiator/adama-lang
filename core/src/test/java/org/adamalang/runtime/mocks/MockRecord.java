/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.index.ReactiveIndex;
import org.adamalang.runtime.index.ReactiveIndexInvalidator;
import org.adamalang.runtime.reactives.*;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;

public class MockRecord extends RxRecordBase<MockRecord> {
    public int id;
    public final RxString data;
    public final RxInt32 index;
    public final ReactiveIndexInvalidator<MockRecord> inv;

    public MockRecord(ObjectNode node, RxParent p) {
        super(p);
        id = RxFactory.makeRxInt32(this, node, "id", 0).get();
        data = RxFactory.makeRxString(this, node, "data", "");
        index = RxFactory.makeRxInt32(this, node, "index", 42);
        if (p instanceof RxTable) {
            if (((RxTable) p).bridge.getNumberColumns() == 1) {
                RxTable<MockRecord> table = (RxTable<MockRecord>) p;
                inv = new ReactiveIndexInvalidator<>(table.getIndex((short) 0), this) {
                    @Override
                    public int pullValue() {
                        return index.get();
                    }
                };
            } else {
                inv = null;
            }
        } else {
            inv = null;
        }
    }

    public static MockRecord make(int id) {
        MockRecord mr = new MockRecord(Utility.createObjectNode(), null);
        mr.id = id;
        return mr;
    }

    @Override
    public int __id() {
        return id;
    }

    public boolean allowCache = false;
    @Override
    public boolean __privacyPolicyAllowsCache() {
        return allowCache;
    }

    @Override
    public String[] __getIndexColumns() {
        return new String[] { "index" };
    }

    @Override
    public int[] __getIndexValues() {
        return new int[] { index.get() };
    }

    @Override
    public String __name() {
        return null;
    }

    @Override
    public void __reindex() {
        if (inv != null) {
            inv.reindex();
        }
    }

    @Override
    public void __deindex() {
        if (inv != null) {
            inv.deindex();
        }
    }

    @Override
    public void __commit(String name, ObjectNode delta) {
        if (__isDirty()) {
            ObjectNode node = delta.putObject(name);
            data.__commit("data", node);
            index.__commit("index", node);
            __lowerDirtyCommit();
        }
    }

    @Override
    public void __revert() {
        if (__isDirty()) {
            __isDying = false;
            data.__revert();
            index.__revert();
            __lowerDirtyRevert();
        }
    }

    @Override
    public JsonNode getPrivateViewFor(NtClient __who) {
        ObjectNode val = Utility.createObjectNode();
        val.put("d", data.get());
        val.put("i", index.get());
        return val;
    }
}
