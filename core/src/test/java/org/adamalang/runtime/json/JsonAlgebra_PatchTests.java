/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_PatchTests {
    @Test
    public void patch_empties() {
        ObjectNode target = Utility.parseJsonObject("{}");
        ObjectNode patch = Utility.parseJsonObject("{}");
        JsonNode result = JsonAlgebra.patch(target, patch);
        Assert.assertEquals("{}", result.toString());
    }
    @Test
    public void patch_unchanged() {
        ObjectNode target = Utility.parseJsonObject("{\"x\":123}");
        ObjectNode patch = Utility.parseJsonObject("{}");
        JsonNode result = JsonAlgebra.patch(target, patch);
        Assert.assertEquals("{\"x\":123}", result.toString());
    }
    @Test
    public void patch_introduce() {
        ObjectNode target = Utility.parseJsonObject("{}");
        ObjectNode patch = Utility.parseJsonObject("{\"x\":123}");
        JsonNode result = JsonAlgebra.patch(target, patch);
        Assert.assertEquals("{\"x\":123}", result.toString());
    }
    @Test
    public void patch_delete() {
        ObjectNode target = Utility.parseJsonObject("{\"x\":123}");
        ObjectNode patch = Utility.parseJsonObject("{\"x\":null}");
        JsonNode result = JsonAlgebra.patch(target, patch);
        Assert.assertEquals("{}", result.toString());
    }
    @Test
    public void patch_obj_overwrite() {
        ObjectNode target = Utility.parseJsonObject("{\"x\":123}");
        ObjectNode patch = Utility.parseJsonObject("{\"x\":{}}");
        JsonNode result = JsonAlgebra.patch(target, patch);
        Assert.assertEquals("{\"x\":{}}", result.toString());
    }
}
