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

import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.api.commands.document.Create;
import org.adamalang.api.commands.mocks.MockBackbone;
import org.adamalang.api.operations.CounterFactory;
import org.adamalang.api.session.UserSession;
import org.adamalang.api.util.Json;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;

public class Scaffold {
  public final MockBackbone backbone;
  public final CounterFactory counters;
  public final CommandFactory commands;
  public final UserSession session;

  public Scaffold() {
    try {
      this.backbone = new MockBackbone();
      this.counters = new CounterFactory();
      this.commands = new CommandFactory(backbone, counters);
      this.session = new UserSession(NtClient.NO_ONE);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public void dispatch(String json, CommandResponder responder) {
    try {
      Request request = new Request(Json.parseJsonObject(json));
      Command cmd = commands.findAndInstrument(request, session, responder);
      cmd.execute();
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}
