/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.contracts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.frontend.Session;
import org.adamalang.contracts.data.DomainWithPolicy;

/** lookup a domain */
public interface DomainWithPolicyResolver {
  public void execute(Session session, String domain, Callback<DomainWithPolicy> callback);

  public static void logInto(DomainWithPolicy domain, ObjectNode node) {
    if (domain != null) {
      if (domain.domain != null) {
        node.put("domain", domain.domain.domain);
        if (domain.domain.space != null) {
          node.put("space", domain.domain.space);
        }
      }
    }
  }
}
