/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.adamalang.netty.ErrorCodes;
import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.jvm.LivingDocumentFactory;

/** a mapping of files to their game spaces */
public class GameSpaceDB {
  private static void sanityCheckDataDirectory(final File file) throws ErrorCodeException {
    try {
      if (!file.exists()) {
        file.mkdir();
      }
      if (!(file.exists() && file.isDirectory())) {
        throw new ErrorCodeException(ErrorCodes.E5_CONFIGURATION_MALFORMED_NO_SOURCE_DIRECTORY, new IOException(file.toString() + " is not a source directory"));
      }
    } catch (Throwable ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.E5_CONFIGURATION_CRASHED, ex);
    }
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

  public synchronized String getCode(String gamespace) throws Exception {
    final var gameSource = new File(schemaRoot, gamespace);
    return Files.readString(gameSource.toPath(), StandardCharsets.UTF_8);
  }

  public synchronized void saveCode(String gamespace, String code) throws Exception {
    final var gameSource = new File(schemaRoot, gamespace);
    Files.writeString(gameSource.toPath(), code, StandardCharsets.UTF_8);
  }

  /** get a gamespace (via filename) */
  public synchronized GameSpace getOrCreate(final String gamespace) throws ErrorCodeException {
    try {
      var gs = map.get(gamespace);
      if (gs != null) {
        return gs;
      }
      final var gameSource = new File(schemaRoot, gamespace);
      if (!gameSource.exists()) {
        throw new ErrorCodeException(ErrorCodes.USERLAND_RESOURCE_CANT_FIND_GAMESPACE, new RuntimeException("gamespace: " + gamespace));
      }
      final var gameData = new File(dataRoot, gamespace);
      sanityCheckDataDirectory(gameData);
      final Supplier<LivingDocumentFactory> deployer = () -> {
        try {
          return GameSpace.buildLivingDocumentFactory(schemaRoot, options, gamespace, "Game" + classId++);
        } catch (ErrorCodeException ex) {
          throw new RuntimeException(ex);
        }
      };
      final var factory = deployer.get();
      gs = new GameSpace(gamespace, factory, time, gameData, deployer);
      map.put(gamespace, gs);
      return gs;
    } catch (Throwable ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.E5_GAMESPACE_DB_CRASHED, ex);
    }
  }
}
