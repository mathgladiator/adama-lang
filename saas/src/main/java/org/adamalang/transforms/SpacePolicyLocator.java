/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.transforms;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.frontend.Session;
import org.adamalang.frontend.global.GlobalExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.transforms.results.SpacePolicy;

import java.util.concurrent.ConcurrentHashMap;

public interface SpacePolicyLocator {

  public void execute(Session session, String spaceName, Callback<SpacePolicy> callback);

  public static void logInto(SpacePolicy policy, ObjectNode node) {
    node.put("space-id", policy.id);
  }

}
