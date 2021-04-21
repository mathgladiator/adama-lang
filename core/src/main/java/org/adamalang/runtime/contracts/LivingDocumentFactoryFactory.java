package org.adamalang.runtime.contracts;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.translator.jvm.LivingDocumentFactory;

public interface LivingDocumentFactoryFactory {
  public static final int USERLAND_CANT_COMPILE_ADAMA_SCRIPT = 40200;

  public void load(String space, Callback<LivingDocumentFactory> callback);
}
