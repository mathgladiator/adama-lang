/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.deploy;

import org.adamalang.runtime.json.JsonStreamReader;

import java.util.HashMap;
import java.util.Objects;

/** a version of an Adama script with imports included */
public class DeployedVersion {
  public final String main;
  public final HashMap<String, String> includes;

  public DeployedVersion(JsonStreamReader reader) {
    String _main = "";
    includes = new HashMap<>();
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "main":
            _main = reader.readString();
            break;
          case "includes":
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                String name = reader.fieldName();
                includes.put(name, reader.readString());
              }
            } else {
              reader.skipValue();
            }
            break;
          default:
            reader.skipValue();
        }
      }
    } else {
      _main = reader.readString();
    }
    this.main = _main;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeployedVersion that = (DeployedVersion) o;
    return Objects.equals(main, that.main) && Objects.equals(includes, that.includes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(main, includes);
  }
}
