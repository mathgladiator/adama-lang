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
