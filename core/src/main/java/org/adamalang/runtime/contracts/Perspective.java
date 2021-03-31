/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

/** a perspective is a consumer of a stream of delta updates from the vantage point of a viewer on a device. */
public interface Perspective {
    public void data(String data);

    public void disconnect();
}
