/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.metering;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

/** each space bills to an owner or an organization;
 * this contract which is highly cached is used to find where to send documents */
public interface BillingDocumentFinder {

  public void find(String space, Callback<Key> callback);
}
