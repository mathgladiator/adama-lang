/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary;

public class CanaryConfig {
  public final String endpoint;
  public final String[] identities;
  public final int connections;
  public final int documentsPerConnectionMinimum;
  public final int documentsPerConnectionMaxmimum;

  public CanaryConfig() {
    this.endpoint = "http://adama-lb-us-east-2-2073537616.us-east-2.elb.amazonaws.com/s";
    // TODO: need to measure the heat of the web tier because I suspect there is a cost in not caching tokens
    this.identities = new String[] {"eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiIxIiwiaXNzIjoiYWRhbWEifQ.eqo02oPRxALrmHUKRaUNHZWyr2cPLkP470gzuE1EjYEn1-VZDlYlh5cz-osZbdBSxuwC2nBKA7-_399kfCO-2A"};
    this.connections = 250;
    this.documentsPerConnectionMinimum = 1;
    this.documentsPerConnectionMaxmimum = 1;
  }
}
