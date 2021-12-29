/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.ErrorCodeException;

/** Monitor the progress of a deployment */
public interface DeploymentMonitor {
  /** a document was touch and then either changed to a different version or not */
  public void bumpDocument(boolean changed);

  /** while deploying, an exception happened; oh no! */
  public void witnessException(ErrorCodeException ex);
}
