/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.commands;

import org.adamalang.api.commands.document.Create;
import org.adamalang.api.mocks.MockResponder;
import org.adamalang.api.operations.CounterFactory;
import org.adamalang.api.session.UserSession;
import org.adamalang.api.util.Json;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class CommandFactoryTests {
  @Test
  public void create() throws Exception {
    CommandFactory cf = new CommandFactory(null, new CounterFactory());
    UserSession session = new UserSession(NtClient.NO_ONE);
    Assert.assertTrue(cf.findAndInstrument(new Request(Json.parseJsonObject("{\"method\":\"create\",\"space\":\"game\",\"key\":42,\"arg\":{}}")), session, new MockResponder()) instanceof Create);
  }
}
