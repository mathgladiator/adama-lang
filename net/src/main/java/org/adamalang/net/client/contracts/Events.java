/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.net.client.contracts;

/** event structure that clients will learn about what happens for a connection to a document */
public interface Events {
  /** the connection was successful, and we can talk to the document via the remote */
  void connected(Remote remote);

  /** a traffic signal has come from the document */
  void traffic(String traffic);

  /** a data change has occurred */
  void delta(String data);

  /** an error has occurred */
  void error(int code);

  /** the document was disconnected */
  void disconnected();
}
