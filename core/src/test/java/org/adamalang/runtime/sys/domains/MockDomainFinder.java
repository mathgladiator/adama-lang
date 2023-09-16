/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
