/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;

public class RuleSetMessages {
  public static TyNativeMessage FindMessageStructure(final Environment environment, final String name, final DocumentPosition position, final boolean silent) {
    final var type = environment.document.types.get(name);
    if (type != null) {
      if (type instanceof TyNativeMessage) {
        return (TyNativeMessage) type.makeCopyWithNewPosition(position, type.behavior);
      } else if (!silent) {
        environment.document.createError(position, String.format("Type incorrect: expecting '%s' to be a message type; instead, found a type of '%s'.", name, type.getAdamaType()));
      }
    } else if (!silent) {
      environment.document.createError(position, String.format("Type not found: a message named '%s' was not found.", name));
    }
    return null;
  }

  public static boolean IsNativeMessage(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (tyType instanceof TyNativeMessage) {
        return true;
      }
      if (!silent) {
        environment.document.createError(tyTypeOriginal, String.format("Type check failure: must have a type of 'message', but got a type of '%s'.", tyTypeOriginal.getAdamaType()));
      }
    }
    return false;
  }
}
