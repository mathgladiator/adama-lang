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

public class TestClientConfig extends ClientConfig {
  public TestClientConfig() {
    super();
    this.clientQueueSize = 1024;
    this.clientQueueTimeoutMS = 500;
    this.connectionQueueSize = 8;
    this.connectionQueueTimeoutMS = 2500;
    this.connectionMaximumBackoffConnectionFailuresMS = 750;
    this.connectionMaximumBackoffFindingClientFailuresMS = 750;
    this.sendRetryDelayMS = 50;
  }
}
