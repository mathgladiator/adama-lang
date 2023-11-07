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
package org.adamalang.validators;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.deploy.SyncCompiler;
import org.adamalang.runtime.remote.Deliverer;

import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ValidatePlan {
  private static final AtomicInteger validationClassId = new AtomicInteger(0);

  public static void validate(String space, ObjectNode node) throws ErrorCodeException {
    DeploymentPlan localPlan = new DeploymentPlan(node.toString(), (t, c) -> t.printStackTrace());
    SyncCompiler.forge(space, space + "prefix", validationClassId, null, localPlan, Deliverer.FAILURE, new TreeMap<>());
  }
}
