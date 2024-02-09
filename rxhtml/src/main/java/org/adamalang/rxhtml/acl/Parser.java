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
package org.adamalang.rxhtml.acl;

import org.adamalang.rxhtml.acl.commands.*;
import org.adamalang.rxhtml.atl.ParseException;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Parser {

  public static ArrayList<Command> parse(String command) throws ParseException  {
    ArrayList<Command> commands = new ArrayList<>();
    for (String phrase : fragmentize(command)) {
      int kColon = phrase.indexOf(':');
      if (kColon > 0) {
        String cmd = phrase.substring(0, kColon).trim().toLowerCase();
        String body = phrase.substring(kColon + 1).trim();
        switch (cmd) {
          case "submit": {
            commands.add(new SubmitById(body.trim()));
            break;
          }
          case "toggle": {
            commands.add(new Toggle(body));
            break;
          }
          case "increment":
          case "inc": {
            commands.add(new Increment(body));
            break;
          }
          case "custom":
            commands.add(new Custom(body));
            break;
          case "decrement":
          case "dec": {
            commands.add(new Decrement(body));
            break;
          }
          case "raise": {
            commands.add(new Set(body, "true"));
            break;
          }
          case "lower": {
            commands.add(new Set(body, "false"));
            break;
          }
          case "fire": {
            commands.add(new Fire(body));
          }
          break;
          case "decide": {
            String[] parts = body.split(Pattern.quote("|"));
            commands.add(new Decide(parts[0], parts.length > 1 ? parts[1] : "id", parts.length > 2 ? parts[2] : "id"));
            break;
          }
          case "choose": {
            String[] parts = body.split(Pattern.quote("|"));
            commands.add(new Choose(parts[0], parts.length > 1 ? parts[1] : "id", parts.length > 2 ? parts[2] : "id"));
            break;
          }
          case "finalize": {
            commands.add(new Finalize(body));
            break;
          }
          case "goto": {
            commands.add(new Goto(body));
            break;
          }
          case "set": {
            int kEq = body.indexOf('=');
            if (kEq > 0) {
              commands.add(new Set(body.substring(0, kEq).trim(), body.substring(kEq + 1).trim()));
            }
            break;
          }
          case "ot":
          case "order-toggle": {
            int kEq = body.indexOf('=');
            if (kEq > 0) {
              commands.add(new OrderToggle(body.substring(0, kEq).trim(), body.substring(kEq + 1).trim()));
            }
            break;
          }
          case "te": {
            commands.add(new TransferError(body));
            break;
          }
          case "tm": {
            String[] parts = body.split(Pattern.quote("|"));
            int offX = parts.length > 1 ? TransferMouse.parseIntOrZero(parts[0]) : 0;
            int offY = parts.length > 2 ? TransferMouse.parseIntOrZero(parts[1]) : 0;
            commands.add(new TransferMouse(parts[0], offX, offY));
            break;
          }
          case "force-auth": {
            int kEq = body.indexOf('=');
            if (kEq > 0) {
              commands.add(new ForceAuth(body.substring(0, kEq).trim(), body.substring(kEq + 1).trim()));
            }
            break;
          }
          case "manifest-add": { // value is a web manifest URL
            commands.add(new ManifestBaseCommand("a", body));
            break;
          }
          case "manifest-use": { // value is an ID
            commands.add(new ManifestBaseCommand("u", body));
            break;
          }
          case "manifest-delete":
          case "manifest-del": { // value is an ID
            commands.add(new ManifestBaseCommand("d", body));
            break;
          }
        }
      } else {
        if ("reset".equals(phrase)) {
          commands.add(new Reset());
        } else if ("submit".equals(phrase)) {
          commands.add(new Submit());
        } else if ("resume".equals(phrase)) {
          commands.add(new Resume());
        } else if ("nuke".equals(phrase)) {
          commands.add(new Nuke());
        } else if ("uncheck".equals(phrase)) {
          commands.add(new Uncheck());
        } else if ("toggle-password".equals(phrase)) {
          commands.add(new TogglePassword());
        } else if ("reload".equals(phrase)) {
          commands.add(new Reload());
        }
      }
    }
    return commands;
  }

  public static ArrayList<String> fragmentize(String command) {
    ArrayList<String> fragments = new ArrayList<>();
    String tail = command;
    while (tail.length() > 0) {
      int kCut = tail.indexOf(' ');
      if (kCut > 0) {
        int kQuote = tail.indexOf('\'');
        if (kQuote < kCut) {
          int kEnd = tail.indexOf('\'', kQuote + 1);
          if (kEnd > kCut) {
            kCut = kEnd;
          }
        }
        String add = tail.substring(0, kCut).replaceAll(Pattern.quote("'"), "");
        fragments.add(add);
        tail = tail.substring(kCut + 1).trim();
      } else {
        fragments.add(tail);
        return fragments;
      }
    }
    return fragments;
  }
}
