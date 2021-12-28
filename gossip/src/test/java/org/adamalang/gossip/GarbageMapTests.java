package org.adamalang.gossip;

import org.junit.Assert;
import org.junit.Test;

public class GarbageMapTests {
    @Test
    public void flow() {
        GarbageMap<String> map = new GarbageMap<>();
        Assert.assertEquals(0, map.keys().size());
        map.put("x", "f(x)", 0);
        Assert.assertEquals(1, map.keys().size());
        Assert.assertEquals("f(x)", map.get("x"));
        Assert.assertEquals(1, map.keys().size());
        Assert.assertEquals("f(x)", map.remove("x"));
        Assert.assertEquals(0, map.keys().size());
        map.put("x", "f(x)", 0);
        Assert.assertEquals(0, map.gc(0));
        Assert.assertEquals(1, map.keys().size());
        Assert.assertEquals("f(x)", map.get("x"));
        Assert.assertEquals(0, map.gc(Constants.MILLISECONDS_TO_SIT_IN_GARBAGE_MAP - 1));
        Assert.assertEquals(1, map.keys().size());
        Assert.assertEquals("f(x)", map.get("x"));
        Assert.assertEquals(0, map.gc(Constants.MILLISECONDS_TO_SIT_IN_GARBAGE_MAP));
        Assert.assertEquals(1, map.keys().size());
        Assert.assertEquals("f(x)", map.get("x"));
        Assert.assertEquals(1, map.gc(Constants.MILLISECONDS_TO_SIT_IN_GARBAGE_MAP + 1));
        Assert.assertEquals(0, map.keys().size());
        Assert.assertNull(map.get("x"));
        Assert.assertNull(map.remove("x"));
    }
}
