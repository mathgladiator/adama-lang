/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_PatchTests {
    private Object of(String json) {
        return new JsonStreamReader(json).readJavaTree();
    }

    private void is(String json, Object result) {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(result);
        Assert.assertEquals(json, writer.toString());
    }
    
    @Test
    public void patch_empties() {
        Object target = of("{}");
        Object patch = of("{}");
        Object result = JsonAlgebra.merge(target, patch);
        is("{}", result);
    }
    @Test
    public void patch_unchanged() {
        Object target = of("{\"x\":123}");
        Object patch = of("{}");
        Object result = JsonAlgebra.merge(target, patch);
        is("{\"x\":123}", result);
    }
    @Test
    public void patch_introduce() {
        Object target = of("{}");
        Object patch = of("{\"x\":123}");
        Object result = JsonAlgebra.merge(target, patch);
        is("{\"x\":123}", result);
    }
    @Test
    public void patch_delete() {
        Object target = of("{\"x\":123}");
        Object patch = of("{\"x\":null}");
        Object result = JsonAlgebra.merge(target, patch);
        is("{}", result);
    }
    @Test
    public void patch_obj_overwrite() {
        Object target = of("{\"x\":123}");
        Object patch = of("{\"x\":{}}");
        Object result = JsonAlgebra.merge(target, patch);
        is("{\"x\":{}}", result);
    }
}
