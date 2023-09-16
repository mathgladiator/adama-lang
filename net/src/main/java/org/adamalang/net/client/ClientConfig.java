/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
