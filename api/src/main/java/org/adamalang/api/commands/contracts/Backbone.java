/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.commands.contracts;

import org.adamalang.runtime.sys.DurableLivingDocument;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.jvm.LivingDocumentFactory;

/** forms the backbone connecting common operations between all operations; note: this has the job of ensuring threads are handled appropriately */
public interface Backbone {
  /** look up the data service for the given space */
  public void findDataService(String space, CommandRequiresDataService cmd, CommandResponder responder);

  /** find the given document for the given key within the given space */
  public void findDocument(String space, String key, CommandRequiresDocument cmd, CommandResponder responder);

  /** make the document */
  public void makeDocument(String space, String key, NtClient who, String arg, String entropy, DataService service, LivingDocumentFactory factory, CommandCreatesDocument cmd, CommandResponder responder);

  /** find the appropriate living document for the space and key pair */
  public void findLivingDocumentFactory(String space, String key, CommandRequiresLivingDocumentFactory cmd, CommandResponder responder);

  /** invalidate and schedule an update for the given document */
  public void invalidateAndSchedule(DurableLivingDocument document);
}
