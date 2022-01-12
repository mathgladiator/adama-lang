package org.adamalang.canary;

public class CanaryLoadGenTool {
  public static void execute() {
    String canaryURL = ""; // TODO: stand up a ELB instance backed by some capacity
    String canarySpace = ""; // TODO: create and define a space, maybe have a tool for it
    String canaryIdentity = ""; // TODO: maybe automate this, maybe not... This requires an init flow OR an authorities setup for the space. Either way
    // TODO: create a scalable WebSocket client and use it here to spin up; this will need to be a MUCH better version than what the CLI uses as we intend to create SERIOUS load from a dev machine instance
    int distinctSockets = 100; // TODO: define as a parameter
    int connectionsPerSocket = 20; // TODO: define as a parameter
    int sendRatePerConnectionPerSecond = 5; // TODO: define as a parameter

  }
}
