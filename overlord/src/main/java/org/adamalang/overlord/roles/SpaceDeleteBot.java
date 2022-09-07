/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord.roles;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.DeletedSpace;
import org.adamalang.mysql.model.Spaces;

public class SpaceDeleteBot {
  private final DataBase dataBase;
  private SpaceDeleteBot(DataBase dataBase) {
    this.dataBase = dataBase;
  }

  public void round() throws Exception {
    for (DeletedSpace ds : Spaces.listDeletedSpaces(dataBase)) {
      // TODO: make sure the space is empty; this is tricky -> make it empty
      // TODO: delete space's IDE (if it exists)
      // TODO: delete space's assets within IDE
    }
  }

  public static void kickOff(DataBase dataBase) {
    SpaceDeleteBot bot = new SpaceDeleteBot(dataBase);
  }
}
