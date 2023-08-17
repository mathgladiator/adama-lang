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
