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

public class Constants {
    /** if someone recommends a deletion, then how many milliseconds should my copy be behind by to accept it. */
    public static long MILLISECONDS_FOR_DELETION_CANDIDATE = 5000;

    /** if I see a too candidate that is too old, then how old must it be to activately delete it */
    public static long MILLISECONDS_FOR_RECOMMEND_DELETION_CANDIDATE = 25000;

    /** item considered too old to be in the garbage collecting map */
    public static long MILLISECONDS_TO_SIT_IN_GARBAGE_MAP = 60000;
}
