/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
