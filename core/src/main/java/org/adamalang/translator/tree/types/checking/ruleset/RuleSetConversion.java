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
import org.adamalang.translator.tree.types.TyType;

public class RuleSetConversion {
  public static void SignalConversionIssue(final Environment environment, final TyType given, final boolean silent) {
    if (!silent && given != null) {
      environment.document.createError(given, String.format("Type check failure: the type `%s` is unable to be converted. Only list<S>, S[], maybe<S> can be converted where S is either a record or a message.", given.getAdamaType()), "TypeCheckFailures");
    }
  }
}
