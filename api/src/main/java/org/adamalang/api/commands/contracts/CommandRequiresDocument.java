package org.adamalang.api.commands.contracts;

import org.adamalang.runtime.DurableLivingDocument;

/** the command requires looking up a document */
public interface CommandRequiresDocument {
  public void onDurableDocumentFound(DurableLivingDocument document);
}
