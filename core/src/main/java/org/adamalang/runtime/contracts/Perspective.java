/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.contracts;

/**
 * a perspective is a consumer of a stream of delta updates from the vantage point of a viewer on a
 * device.
 */
public interface Perspective {
  /** a dead perspective, not useful except for people that don't care about the data */
  Perspective DEAD = new Perspective() {
    @Override
    public void data(String data) {
    }

    @Override
    public void disconnect() {
    }
  };

  /** new data for the user */
  void data(String data);

  /** the server disconnected the stream */
  void disconnect();
}
