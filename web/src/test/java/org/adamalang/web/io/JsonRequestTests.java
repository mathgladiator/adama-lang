/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.io;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class JsonRequestTests {

    private static ObjectNode of(String json) throws Exception {
        return Json.parseJsonObject(json);
    }


    @Test
    public void noMethod() throws Exception {
        JsonRequest request = new JsonRequest(of("{}"));
        try {
            request.method();
            Assert.fail();
        } catch (ErrorCodeException ece) {
            Assert.assertEquals(213708, ece.code);
        }
    }

    @Test
    public void noID() throws Exception {
        JsonRequest request = new JsonRequest(of("{}"));
        try {
            request.id();
            Assert.fail();
        } catch (ErrorCodeException ece) {
            Assert.assertEquals(233120, ece.code);
        }
    }

    @Test
    public void bothIdAndMethod() throws Exception {
        JsonRequest request = new JsonRequest(of("{\"id\":123,\"method\":\"m\"}"));
        Assert.assertEquals(123, request.id());
        Assert.assertEquals("m", request.method());
    }

    @Test
    public void getString() throws Exception {
        JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":42.5,\"w\":\"xyz\"}"));
        try {
            request.getString("x", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getString("x", false, 123));
        try {
            request.getString("y", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getString("y", false, 123));
        Assert.assertEquals("42.5", request.getString("z", true, 4));
        Assert.assertEquals("xyz", request.getString("w", true, 42));
        Assert.assertEquals("xyz", request.getString("w", false, 42));
        try {
            request.getString("t", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getString("t", false, 123));
    }

    @Test
    public void getInteger() throws Exception {
        JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":42.5,\"w\":4,\"w2\":\"5\",\"w3\":\"x\"}"));
        try {
            request.getInteger("x", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getInteger("x", false, 123));
        try {
            request.getInteger("y", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getInteger("y", false, 123));
        try {
            request.getInteger("z", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertEquals(4, (int) request.getInteger("w", true, 42));
        Assert.assertEquals(4, (int) request.getInteger("w", false, 42));
        Assert.assertEquals(5, (int) request.getInteger("w2", true, 42));
        Assert.assertEquals(5, (int) request.getInteger("w2", false, 42));
        try {
            request.getInteger("w3", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        try {
            request.getInteger("t", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getInteger("t", false, 123));
    }

    @Test
    public void getLong() throws Exception {
        JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":42.5,\"w\":4,\"w2\":\"5\",\"w3\":\"x\"}"));
        try {
            request.getLong("x", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getLong("x", false, 123));
        try {
            request.getLong("y", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getLong("y", false, 123));
        try {
            request.getLong("z", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertEquals(4, (long) request.getLong("w", true, 42));
        Assert.assertEquals(4, (long) request.getLong("w", false, 42));
        Assert.assertEquals(5, (long) request.getLong("w2", true, 42));
        Assert.assertEquals(5, (long) request.getLong("w2", false, 42));
        try {
            request.getLong("w3", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        try {
            request.getLong("t", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getLong("t", false, 123));
    }

    @Test
    public void getNode() throws Exception {
        JsonRequest request = new JsonRequest(of("{\"x\":true,\"y\":null,\"z\":{}}"));
        try {
            request.getObject("x", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getObject("x", false, 123));
        try {
            request.getObject("y", true, 123);
        } catch (ErrorCodeException exc) {
            Assert.assertEquals(123, exc.code);
        }
        Assert.assertNull(request.getObject("y", false, 123));
        Assert.assertNotNull(request.getObject("z", true, 2));
        Assert.assertNotNull(request.getObject("z", false, 2));
    }
}
