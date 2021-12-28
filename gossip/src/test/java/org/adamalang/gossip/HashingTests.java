package org.adamalang.gossip;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashingTests {
    @Test
    public void ok() {
        MessageDigest digest = Hashing.md5();
        digest.update("X".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals("AhKbuGEGHRoFLFkuLcazgw==", Hashing.finishAndEncode(digest));
    }
    @Test
    public void fail() {
        try {
            Hashing.forKnownAlgorithm("SHA9000!");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertEquals("java.security.NoSuchAlgorithmException: SHA9000! MessageDigest not available", ex.getMessage());
        }
    }
}
