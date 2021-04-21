/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.commands.mocks;

import org.adamalang.api.commands.contracts.*;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.util.HashMap;

public class MockBackbone implements Backbone {

  public final HashMap<String, DataService> dataservices;

  public MockBackbone() {
    this.dataservices = new HashMap<>();
  }

  @Override
  public TimeSource getTimeSource() {
    return null;
  }

  @Override
  public DocumentMonitor monitorFor(String space, long key) {
    return null;
  }

  @Override
  public void findDataService(String space, CommandRequiresDataService cmd, CommandResponder responder) {

  }

  @Override
  public void findDocument(String space, long key, CommandRequiresDocument cmd, CommandResponder responder) {

  }

  @Override
  public void findLivingDocumentFactory(String space, long key, CommandRequiresLivingDocumentFactory cmd, CommandResponder responder) {
  }

  @Override
  public void invalidateAndSchedule(DurableLivingDocument document) {
  }
}
