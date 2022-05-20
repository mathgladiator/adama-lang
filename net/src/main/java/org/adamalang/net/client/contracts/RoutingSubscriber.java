package org.adamalang.net.client.contracts;

/** what happens when you subscribe to a key */
public interface RoutingSubscriber {

  /** routing found the document in another region */
  public void onRegion(String region);

  /** routing found the document within the current region on a specific machine */
  public void onMachine(String machine);
}
