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
package org.adamalang.common.gossip;

/** this version of gossip depends on some magic numbers */
public class Constants {
  /**
   * if someone recommends a deletion, then how many milliseconds should my copy be behind by to
   * accept it.
   */
  public static long MILLISECONDS_FOR_DELETION_CANDIDATE = 7500;

  /** if I see a too candidate that is too old, then how old must it be to activately delete it */
  public static long MILLISECONDS_FOR_RECOMMEND_DELETION_CANDIDATE = 10000;

  /** item considered too old to be in the garbage collecting map */
  public static long MILLISECONDS_TO_SIT_IN_GARBAGE_MAP = 60000;

  /** maximum new entries to hold onto for recent map */
  public static int MAX_RECENT_ENTRIES = 100;

  /** maximum delete entries to hold onto */
  public static int MAX_DELETES = 50;

  /** maximum history to hold onto */
  public static int MAX_HISTORY = 25;
}
