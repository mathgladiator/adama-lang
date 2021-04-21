package org.adamalang.api.commands.contracts;

import org.adamalang.translator.jvm.LivingDocumentFactory;

/** the command requires the living document */
public interface CommandRequiresLivingDocumentFactory {
  public void onLivingDocumentFactory(LivingDocumentFactory factory);
}
