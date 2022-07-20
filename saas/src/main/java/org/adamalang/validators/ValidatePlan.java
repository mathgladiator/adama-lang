/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.validators;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.remote.Deliverer;

import java.util.concurrent.atomic.AtomicInteger;

public class ValidatePlan {
  private static final AtomicInteger validationClassId = new AtomicInteger(0);

  public static void validate(ObjectNode node) throws ErrorCodeException {
    DeploymentPlan localPlan = new DeploymentPlan(node.toString(), (t, c) -> t.printStackTrace());
    new DeploymentFactory("name", "prefix", validationClassId, null, localPlan, Deliverer.FAILURE);
  }
}
