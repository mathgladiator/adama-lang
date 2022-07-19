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
