/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
