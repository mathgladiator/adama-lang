/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern;

import org.adamalang.common.ExceptionLogger;
import org.adamalang.grpc.client.Client;
import org.adamalang.mysql.DataBase;

public class ExternNexus {

  public final Email email;
  public final DataBase dataBase;
  public final Client client;

  public ExternNexus(Email email, DataBase dataBase, Client client) {
    this.email = email;
    this.dataBase = dataBase;
    this.client = client;
  }

  public ExceptionLogger makeLogger(Class<?> clazz) {
    return new ExceptionLogger() {
      @Override
      public void convertedToErrorCode(Throwable t, int errorCode) {
        t.printStackTrace();
      }
    };
  }
}
