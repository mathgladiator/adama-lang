/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.DefineStateTransition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeStateMachineRef;
import org.adamalang.translator.tree.types.reactive.TyReactiveStateMachineRef;

public class RuleSetStateMachine {
  public static DefineStateTransition FindStateMachineStep(final Environment environment, final String name, final DocumentPosition position, final boolean silent) {
    final var defineStateTransition = environment.document.transitions.get(name);
    if (defineStateTransition != null) {
      return defineStateTransition;
    } else if (!silent) {
      environment.document.createError(position, String.format("State machine transition not found: a state machine label '%s' was not found.", name), "StateMachineLabels");
    }
    return null;
  }

  public static boolean IsStateMachineRef(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = RuleSetCommon.Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeStateMachineRef || tyType instanceof TyReactiveStateMachineRef) { return true; }
      RuleSetCommon.SignalTypeFailure(environment, new TyNativeStateMachineRef(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }
}
