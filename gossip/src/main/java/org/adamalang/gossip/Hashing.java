/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
