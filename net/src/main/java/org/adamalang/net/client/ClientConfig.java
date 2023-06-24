/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client;

public class ClientConfig {
  protected int clientQueueSize;
  protected int clientQueueTimeoutMS;

  protected int connectionQueueSize;
  protected int connectionQueueTimeoutMS;
  protected int connectionMaximumBackoffConnectionFailuresMS;
  protected int connectionMaximumBackoffFindingClientFailuresMS;
  protected int sendRetryDelayMS;

  public ClientConfig() {
    this.clientQueueSize = 1024;
    this.clientQueueTimeoutMS = 1250;
    this.connectionQueueSize = 32;
    this.connectionQueueTimeoutMS = 1250;
    this.connectionMaximumBackoffConnectionFailuresMS = 2500;
    this.connectionMaximumBackoffFindingClientFailuresMS = 2500;
    this.sendRetryDelayMS = 100;
  }

  public int getClientQueueSize() {
    return clientQueueSize;
  }

  public int getConnectionQueueSize() {
    return connectionQueueSize;
  }

  public int getConnectionQueueTimeoutMS() {
    return connectionQueueTimeoutMS;
  }

  public int getClientQueueTimeoutMS() {
    return clientQueueTimeoutMS;
  }

  public int getConnectionMaximumBackoffConnectionFailuresMS() {
    return connectionMaximumBackoffConnectionFailuresMS;
  }

  public int connectionMaximumBackoffFindingClientFailuresMS() {
    return connectionMaximumBackoffFindingClientFailuresMS;
  }

  public int getSendRetryDelayMS() {
    return sendRetryDelayMS;
  }
}
