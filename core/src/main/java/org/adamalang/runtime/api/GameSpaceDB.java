/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import java.io.File;
import java.util.HashMap;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedException;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedReason;
import org.adamalang.translator.env.CompilerOptions;

/** a mapping of files to their game spaces */
public class GameSpaceDB {
  private static void sanityCheckDataDirectory(final File file) throws Exception {
    if (!file.exists()) {
      file.mkdir();
    }
    if (!(file.exists() && file.isDirectory())) { throw new Exception("Data root: " + file.getName() + " either does not exist or is a file"); }
  }

  private int classId;
  private final File dataRoot;
  private final HashMap<String, GameSpace> map;
  private final File sourceRoot;
  private final TimeSource time;

  public GameSpaceDB(final File sourceRoot, final File dataRoot, final TimeSource time) throws Exception {
    this.sourceRoot = sourceRoot;
    if (!sourceRoot.exists()) { throw new Exception("Source root: `" + dataRoot.getName() + "` does not exist"); }
    sanityCheckDataDirectory(dataRoot);
    this.dataRoot = dataRoot;
    this.time = time;
    map = new HashMap<>();
    classId = 0;
  }

  /** get a gamespace (via filename) */
  public synchronized GameSpace getOrCreate(final String game) throws Exception {
    var gs = map.get(game);
    if (gs != null) { return gs; }
    final var gameSource = new File(sourceRoot, game);
    if (!gameSource.exists()) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.GamespaceNotFound); }
    final var gameData = new File(dataRoot, game);
    sanityCheckDataDirectory(gameData);
    final var factory = GameSpace.buildLivingDocumentFactory(sourceRoot, CompilerOptions.start().make(), game, "Game" + classId++);
    gs = new GameSpace(factory, time, gameData);
    map.put(game, gs);
    return gs;
  }
}
