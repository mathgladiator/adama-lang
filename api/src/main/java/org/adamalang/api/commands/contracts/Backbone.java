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

import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.TimeSource;

/** forms the backbone connecting common operations between all operations; note: this has the job of ensuring threads are handled appropriately */
public interface Backbone {
  /** get the time source which allows granular testing */
  public TimeSource getTimeSource();

  /** product a monitor for the given space, key combo */
  public DocumentMonitor monitorFor(String space, long key);

  /** look up the data service for the given space */
  public void findDataService(String space, CommandRequiresDataService cmd, CommandResponder responder);

  /** find the given document for the given key within the given space */
  public void findDocument(String space, long key, CommandRequiresDocument cmd, CommandResponder responder);

  /** find the appropriate living document for the space and key pair */
  public void findLivingDocumentFactory(String space, long key, CommandRequiresLivingDocumentFactory cmd, CommandResponder responder);

  /** invalidate and schedule an update for the given document */
  public void invalidateAndSchedule(DurableLivingDocument document);
}
