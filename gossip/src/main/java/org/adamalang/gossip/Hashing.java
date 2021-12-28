package org.adamalang.gossip;

import java.security.MessageDigest;
import java.util.Base64;

public class Hashing {
    public static MessageDigest forKnownAlgorithm(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static MessageDigest md5() {
        return forKnownAlgorithm("MD5");
    }

    public static String finishAndEncode(MessageDigest digest) {
        return new String(Base64.getEncoder().encode(digest.digest()));
    }



}
