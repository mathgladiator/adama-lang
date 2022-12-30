/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql;

public class Migrate {
  public static void copy(DataBase from, DataBase to) {
    // DON'T TOUCH 'capacity'
    // COPY 'directory'
    // COPY 'emails' with new ids
    // DON'T TOUCH 'initiations'
    // COPY/TRANSLATE 'email_keys'
    // COPY/TRANSLATE 'spaces'
    // COPY/TRANSLATE 'grants'
    // COPY/TRANSLATE 'authorities'
    // DON'T TOUCH 'metering'
    // DON'T TOUCH 'bills'
    // DON'T TOUCH 'hosts'
    // DON'T TOUCH 'secrets'
    // COPY/TRANSLATE 'domains'
    // DON'T TOUCH 'sentinel'
  }
}
