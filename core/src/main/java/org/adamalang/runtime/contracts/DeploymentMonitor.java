/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
