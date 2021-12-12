package org.adamalang.web.io;

import org.junit.Assert;
import org.junit.Test;

public class JsonTests {
    @Test
    public void coverage() {
        Json.newJsonObject();
        Json.parseJsonObject("{}");
        boolean failure = true;
        try {
            Json.parseJsonObjectThrows("x");
            failure = false;
        } catch (Exception ex) {

        }
        try {
            Json.parseJsonObjectThrows("[]");
            failure = false;
        } catch (Exception ex) {

        }
        try {
            Json.parseJsonObject("x");
            failure = false;
        } catch (RuntimeException ex) {
        }
        try {
            Json.parseJsonObject("[]");
            failure = false;
        } catch (RuntimeException ex) {

        }
        Assert.assertTrue(failure);
    }
}
