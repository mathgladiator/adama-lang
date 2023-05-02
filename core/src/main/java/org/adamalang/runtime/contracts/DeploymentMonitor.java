/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.ErrorCodeException;

/** Monitor the progress of a deployment */
public interface DeploymentMonitor {
  /** a document was touch and then either changed to a different version or not */
  void bumpDocument(boolean changed);

  /** while deploying, an exception happened; oh no! */
  void witnessException(ErrorCodeException ex);

  /** the deployment finished */
  void finished(int ms);
}
