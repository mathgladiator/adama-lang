/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.contracts;

public interface ClientCallback {
  public void failed(Throwable exception);
  public void failedToConnect();
  public void successfulResponse(String data);
}
