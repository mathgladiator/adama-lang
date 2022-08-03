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
