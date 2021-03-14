/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.translator.env.CompilerOptions;

/** a mapping of files to their game spaces */
public class GameSpaceDB {
  private static void sanityCheckDataDirectory(final File file) throws ErrorCodeException {
    if (!file.exists()) {
      file.mkdir();
    }
    if (!(file.exists() && file.isDirectory())) { throw new ErrorCodeException(ErrorCodeException.CONFIGURATION_MALFORMED_NO_SOURCE_DIRECTORY); }
  }

  private int classId;
  private final File dataRoot;
  private final HashMap<String, GameSpace> map;
  private final CompilerOptions options;
  private final File schemaRoot;
  private final TimeSource time;

  public GameSpaceDB(final File schemaRoot, final File dataRoot, final CompilerOptions options, final TimeSource time) throws Exception {
    this.schemaRoot = schemaRoot;
    this.options = options;
    if (!schemaRoot.exists()) { throw new Exception("Schema root: `" + dataRoot.getName() + "` does not exist"); }
    sanityCheckDataDirectory(dataRoot);
    this.dataRoot = dataRoot;
    this.time = time;
    map = new HashMap<>();
    classId = 0;
  }

  public synchronized void close() throws Exception {
    for (final Map.Entry<String, GameSpace> entry : map.entrySet()) {
      entry.getValue().close();
    }
    map.clear();
  }

  /** get a gamespace (via filename) */
  public synchronized GameSpace getOrCreate(final String gamespace) throws ErrorCodeException {
    var gs = map.get(gamespace);
    if (gs != null) { return gs; }
    final var gameSource = new File(schemaRoot, gamespace);
    if (!gameSource.exists()) { throw new ErrorCodeException(ErrorCodeException.USERLAND_CANT_FIND_GAMESPACE); }
    final var gameData = new File(dataRoot, gamespace);
    sanityCheckDataDirectory(gameData);
    final var factory = GameSpace.buildLivingDocumentFactory(schemaRoot, options, gamespace, "Game" + classId++);
    gs = new GameSpace(gamespace, factory, time, gameData);
    map.put(gamespace, gs);
    return gs;
  }
}
