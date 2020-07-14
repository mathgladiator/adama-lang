/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;

public class RuleSetConversion {
  public static void SignalConversionIssue(final Environment environment, final TyType given, final boolean silent) {
    if (!silent && given != null) {
      environment.document.createError(given, String.format("Type check failure: the type `%s` is unable to be converted. Only list<S>, S[], maybe<S> can be converted where S is either a record or a message.", given.getAdamaType()),
          "TypeCheckFailures");
    }
  }
}
