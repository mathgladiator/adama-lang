/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.contracts;

import org.adamalang.common.Callback;
import org.adamalang.mysql.data.Domain;

/** find a domain */
public interface DomainFinder {

  /** just find the domain */
  public void find(String domain, Callback<Domain> callback);
}
