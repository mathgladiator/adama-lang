/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.contracts;

import org.adamalang.netty.api.AdamaSession;

public interface AuthCallback {
  public void failure();
  public void success(AdamaSession session);
}
