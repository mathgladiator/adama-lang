/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.commands.services;

/** Machine role */
public enum Role {
  Adama("adama", 8003), //
  Web("web", 8005), //
  Overlord("overlord", 8011);

  public final String name;
  public final int monitoringPort;

  private Role(String name, int monitoringPort) {
    this.name = name;
    this.monitoringPort = monitoringPort;
  }
}
