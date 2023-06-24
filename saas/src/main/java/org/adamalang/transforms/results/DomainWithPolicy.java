/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.transforms.results;

import org.adamalang.mysql.data.Domain;

/** a domain with an associated space policy */
public class DomainWithPolicy {
  public final Domain domain;
  public final SpacePolicy policy;

  public DomainWithPolicy(Domain domain, SpacePolicy policy) {
    this.domain = domain;
    this.policy = policy;
  }
}
