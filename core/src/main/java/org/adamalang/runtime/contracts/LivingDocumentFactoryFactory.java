/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.translator.jvm.LivingDocumentFactory;

public interface LivingDocumentFactoryFactory {
  public static final int USERLAND_CANT_COMPILE_ADAMA_SCRIPT = 40200;

  public void load(String space, Callback<LivingDocumentFactory> callback);
}
