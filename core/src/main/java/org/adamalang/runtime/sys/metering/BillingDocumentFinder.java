package org.adamalang.runtime.sys.metering;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

/** each space bills to an owner or an organization;
 * this contract which is highly cached is used to find where to send documents */
public interface BillingDocumentFinder {

  public void find(String space, Callback<Key> callback);
}
