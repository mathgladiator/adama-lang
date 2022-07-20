/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.data;

/** tuple of resources that may go unbilled between accountant runs */
public class UnbilledResources {
  public final long storage;
  public final long bandwidth;
  public final long first_party_service_calls;
  public final long third_party_service_calls;

  public UnbilledResources(long storage, long bandwidth, long first_party_service_calls, long third_party_service_calls) {
    this.storage = storage;
    this.bandwidth = bandwidth;
    this.first_party_service_calls = first_party_service_calls;
    this.third_party_service_calls = third_party_service_calls;
  }
}
