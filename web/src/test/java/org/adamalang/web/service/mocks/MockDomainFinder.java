/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.runtime.sys.domains.DomainFinder;

public class MockDomainFinder implements DomainFinder {
  @Override
  public void find(String domain, Callback<Domain> callback) {
    callback.failure(new ErrorCodeException(-231));
  }
}
