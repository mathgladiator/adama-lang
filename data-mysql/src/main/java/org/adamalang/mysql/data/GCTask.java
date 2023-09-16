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
package org.adamalang.mysql.data;

/** a task for asset GC algorithm to work on */
public class GCTask {
  public final int id;
  public final String space;
  public final String key;
  public final int seq;
  public final String archiveKey;

  public GCTask(int id, String space, String key, int seq, String archiveKey) {
    this.id = id;
    this.space = space;
    this.key = key;
    this.seq = seq;
    this.archiveKey = archiveKey;
  }

  @Override
  public String toString() {
    return "GCTask{" + "id=" + id + ", space='" + space + '\'' + ", key='" + key + '\'' + ", seq=" + seq + '}';
  }
}
