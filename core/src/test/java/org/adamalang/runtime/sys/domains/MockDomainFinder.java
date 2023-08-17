/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.domains;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.HashMap;
import java.util.HashSet;

public class MockDomainFinder implements DomainFinder {
  public HashMap<String, Domain> mapping;
  public HashSet<String> bad;

  public MockDomainFinder() {
    this.mapping = new HashMap<>();
    this.bad = new HashSet<>();
  }

  public MockDomainFinder with(String host, Domain domain) {
    this.mapping.put(host, domain);
    return this;
  }

  @Override
  public void find(String domain, Callback<Domain> callback) {
    if (bad.contains(domain)) {
      callback.failure(new ErrorCodeException(-404));
      return;
    }
    callback.success(mapping.get(domain));
  }
}
