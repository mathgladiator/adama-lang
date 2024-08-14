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
package org.adamalang.rxhtml.routing;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Path {
  private final String name;
  private final TreeMap<String, Path> fixed;
  private final ArrayList<Path> numbers;
  private final ArrayList<Path> texts;
  private Path suffix;
  private Target target;

  public Path(String name) {
    this.name = name;
    this.fixed = new TreeMap<>();
    this.numbers = new ArrayList<>();
    this.texts = new ArrayList<>();
    this.suffix = null;
    this.target = null;
  }

  public long memory() {
    long result = 40L;
    if (name != null) {
      result += name.length() * 2;
    }
    for (Map.Entry<String, Path> e : fixed.entrySet()) {
      result += 64;
      result += e.getKey().length() * 2;
      result += e.getValue().memory();
    }
    for (Path n : numbers) {
      result += n.memory();
    }
    for (Path t : texts) {
      result += t.memory();
    }
    if (suffix != null) {
      result += suffix.memory();
    }
    return result;
  }

  public Path diveFixed(String path) {
    Path next = fixed.get(path);
    if (next == null) {
      next = new Path(null);
      fixed.put(path, next);
    }
    return next;
  }

  public Path newNumber(String name) {
    Path path = new Path(name);
    numbers.add(path);
    return path;
  }

  public Path newText(String name) {
    Path path = new Path(name);
    texts.add(path);
    return path;
  }

  public Path setSuffix(String name) {
    if (suffix == null) {
      suffix = new Path(name);
    }
    return suffix;
  }

  public boolean set(Target target) {
    if (this.target == null) {
      this.target = target;
      return true;
    }
    return false;
  }

  public Target route(int at, String[] parts, TreeMap<String, String> capture) {
    if (at == parts.length) {
      return target;
    }
    if (at < parts.length) {
      String part = parts[at];
      // Numbers
      try {
        Double.parseDouble(part);
        for (Path path : numbers) {
          capture.put(path.name, part);
          Target found = path.route(at + 1, parts, capture);
          if (found != null) {
            return found;
          }
          capture.remove(path.name);
        }
      } catch (NumberFormatException nfe) {
        // not a number
      }
      // Text
      for (Path path : texts) {
        capture.put(path.name, part);
        Target found = path.route(at + 1, parts, capture);
        if (found != null) {
          return found;
        }
        capture.remove(path.name);
      }
      // Fixed Values
      Path fix = fixed.get(part);
      if (fix != null) {
        Target found = fix.route(at + 1, parts, capture);
        if (found != null) {
          return found;
        }
      }
      // Suffix
      if (suffix != null) {
        StringBuilder captured = new StringBuilder();
        captured.append(part);
        for (int s = at + 1; s < parts.length; s++) {
          captured.append("/").append(parts[s]);
        }
        capture.put(suffix.name, captured.toString());
        return suffix.target;
      }
    }
    return null;
  }

  public static String[] parsePath(String path) {
    String uri = path;
    // truncate a trailing "/"
    while (uri.length() > 1 && uri.endsWith("/")) {
      uri = uri.substring(0, uri.length() - 1);
    }
    // remove prefix /
    if (uri.startsWith("/")) {
      uri = uri.substring(uri.indexOf('/') + 1);
    }
    return uri.split(Pattern.quote("/"));
  }
}
