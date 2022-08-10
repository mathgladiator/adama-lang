/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.ops;

import org.adamalang.TestFrontEnd;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.data.Deployment;
import org.junit.Test;

public class DeploymentAgentTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      fe.deploymentAgent.witnessException(new ErrorCodeException(-1));
      fe.deploymentAgent.bumpDocument(true);
      fe.deploymentAgent.bumpDocument(false);
      fe.deploymentAgent.finished(1000);
      fe.deploymentAgent.accept("*");
      fe.deploymentAgent.accept("space-that-doesn't exist");
      fe.deploymentAgent.deploy(new Deployment("", "", "", ""));
      fe.deploymentAgent.convertedToErrorCode(null, 1);
      fe.deploymentAgent.deploy(new Deployment("space", "hash", "{\"plan\":[],\"default\":\"x\",\"versions\":{\"x\":\"public\"}}", ""));
      fe.deploymentAgent.deploy(new Deployment(null, null, null, null));
    }
  }
}
