/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
        String cmd = phrase.substring(0, kColon);
        String body = phrase.substring(kColon + 1).trim();
        switch (cmd) {
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
            commands.add(new Raise(body));
            break;
          }
          case "lower": {
            commands.add(new Lower(body));
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
          }
        }
      } else {
        if ("reset".equals(phrase)) {
          commands.add(new Reset());
        } else if ("submit".equals(phrase)) {
          commands.add(new Submit());
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
