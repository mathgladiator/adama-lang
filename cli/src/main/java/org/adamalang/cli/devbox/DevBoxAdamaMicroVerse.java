/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.interactive.TerminalIO;
import org.adamalang.runtime.sys.CoreService;

/** a microverse is a local cosmos of an Adama machine that outlines everything needed to run Adama locally without a DB */
public class DevBoxAdamaMicroVerse {
  public final CoreService service;

  private DevBoxAdamaMicroVerse(CoreService service) {
    this.service = service;
  }

  public static DevBoxAdamaMicroVerse load(TerminalIO io, ObjectNode defn) {
    return null;
  }
}
