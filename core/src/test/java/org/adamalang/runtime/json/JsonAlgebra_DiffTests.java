/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_DiffTests {
    @Test
    public void field_int() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":1}");
        ObjectNode b = Utility.parseJsonObject("{}");
        Assert.assertEquals("{\"x\":null}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":1}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void field_bool() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":true}");
        ObjectNode b = Utility.parseJsonObject("{}");
        Assert.assertEquals("{\"x\":null}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":true}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void field_string() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":\"z\"}");
        ObjectNode b = Utility.parseJsonObject("{}");
        Assert.assertEquals("{\"x\":null}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":\"z\"}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void transition() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":1}");
        ObjectNode b = Utility.parseJsonObject("{\"y\":1}");
        Assert.assertEquals("{\"y\":1,\"x\":null}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":1,\"y\":null}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void convert_to_object() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":1}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":{}}");
        Assert.assertEquals("{\"x\":{}}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":1}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void convert_to_array() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":1}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":[1]}");
        Assert.assertEquals("{\"x\":[{\"@n\":1}]}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":1}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void convert_between_arrays_of_consts() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":[1,2,3]}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":[3,1,2]}");
        Assert.assertEquals("{\"x\":[{\"@n\":3},{\"@n\":1},{\"@n\":2}]}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":[{\"@n\":1},{\"@n\":2},{\"@n\":3}]}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void convert_reorder_with_ids() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":[{\"id\":1},{\"id\":2},{\"id\":3}]}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":[{\"id\":3},{\"id\":1},{\"id\":2}]}");
        Assert.assertEquals("{\"x\":[{\"@c\":2},{\"@c\":0},{\"@c\":1}]}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":[{\"@c\":1},{\"@c\":2},{\"@c\":0}]}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void convert_reorder_with_changes_and_deletes() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":[{\"id\":1},{\"id\":2,\"x\":7},{\"id\":3}]}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":[{\"id\":3,\"x\":7},{\"id\":1}]}");
        Assert.assertEquals("{\"x\":[{\"@m\":{\"x\":7},\"@f\":2},{\"@c\":0}]}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":[{\"@c\":1},{\"@n\":{\"id\":2,\"x\":7}},{\"@m\":{\"x\":null},\"@f\":0}]}", JsonAlgebra.difference(b, a).toString());
    }

    @Test
    public void nulls() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":1}");
        Assert.assertEquals(null, JsonAlgebra.difference(null, null));
        Assert.assertEquals(null, JsonAlgebra.difference(a, null));
        Assert.assertEquals("{\"x\":1}", JsonAlgebra.difference(null, a).toString());
    }

    @Test
    public void change_bool() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":true}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":false}");
        Assert.assertEquals("{\"x\":false}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":true}", JsonAlgebra.difference(b, a).toString());
        Assert.assertEquals(null, JsonAlgebra.difference(a, a));
        ObjectNode c = Utility.parseJsonObject("{\"x\":\"z\"}");
        Assert.assertEquals("{\"x\":\"z\"}", JsonAlgebra.difference(a, c).toString());
    }

    @Test
    public void change_int() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":1}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":2}");
        Assert.assertEquals("{\"x\":2}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":1}", JsonAlgebra.difference(b, a).toString());
        Assert.assertEquals(null, JsonAlgebra.difference(a, a));
        ObjectNode c = Utility.parseJsonObject("{\"x\":\"z\"}");
        Assert.assertEquals("{\"x\":\"z\"}", JsonAlgebra.difference(a, c).toString());
        ObjectNode d = Utility.parseJsonObject("{\"x\":true}");
        Assert.assertEquals("{\"x\":true}", JsonAlgebra.difference(a, d).toString());
    }

    @Test
    public void change_double() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":1.5}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":2.5}");
        Assert.assertEquals("{\"x\":2.5}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":1.5}", JsonAlgebra.difference(b, a).toString());
        Assert.assertEquals(null, JsonAlgebra.difference(a, a));
        ObjectNode c = Utility.parseJsonObject("{\"x\":\"z\"}");
        Assert.assertEquals("{\"x\":\"z\"}", JsonAlgebra.difference(a, c).toString());
        ObjectNode d = Utility.parseJsonObject("{\"x\":true}");
        Assert.assertEquals("{\"x\":true}", JsonAlgebra.difference(a, d).toString());
    }

    @Test
    public void change_string() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":\"z\"}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":\"ez\"}");
        Assert.assertEquals("{\"x\":\"ez\"}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":\"z\"}", JsonAlgebra.difference(b, a).toString());
        Assert.assertEquals(null, JsonAlgebra.difference(a, a));
        ObjectNode c = Utility.parseJsonObject("{\"x\":1}");
        Assert.assertEquals("{\"x\":1}", JsonAlgebra.difference(a, c).toString());
        ObjectNode d = Utility.parseJsonObject("{\"x\":true}");
        Assert.assertEquals("{\"x\":true}", JsonAlgebra.difference(a, d).toString());
    }

    @Test
    public void deep_change() {
        ObjectNode a = Utility.parseJsonObject("{\"x\":{\"x\":{\"x\":1}}}");
        ObjectNode b = Utility.parseJsonObject("{\"x\":{\"x\":{\"x\":12}}}");
        Assert.assertEquals("{\"x\":{\"x\":{\"x\":12}}}", JsonAlgebra.difference(a, b).toString());
        Assert.assertEquals("{\"x\":{\"x\":{\"x\":1}}}", JsonAlgebra.difference(b, a).toString());
    }
}
