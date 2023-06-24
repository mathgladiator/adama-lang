/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.checking;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanAssignResult;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativeComplex;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.reactive.TyReactiveComplex;

public class LocalTypeAssignmentResult {
  private final Environment environment;
  private final Expression expression;
  private final Expression ref;
  public CanAssignResult assignResult = CanAssignResult.No;
  public TyType ltype = null;
  public TyType rtype = null;

  public LocalTypeAssignmentResult(final Environment environment, final Expression ref, final Expression expression) {
    this.environment = environment;
    this.ref = ref;
    this.expression = expression;
  }

  public boolean bad() {
    return ltype == null || rtype == null;
  }

  public void ingest() {
    ltype = ref.typing(environment.scopeWithComputeContext(ComputeContext.Assignment), null);
    rtype = expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    environment.rules.CanAIngestB(ltype, rtype, false);
    assignResult = CanAssignResult.YesWithIngestionCodeGen;
  }

  public void set() {
    ltype = environment.rules.Resolve(ref.typing(environment.scopeWithComputeContext(ComputeContext.Assignment), null), false);
    rtype = environment.rules.Resolve(expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null), false);

    if (ltype != null && rtype != null) {
      if (ltype.behavior.isReadOnly) {
        environment.document.createError(DocumentPosition.sum(ltype, rtype), String.format("'%s' is unable to accept an set of '%s'.", ltype.getAdamaType(), rtype.getAdamaType()), "RuleSetAssignmentSet");
      }
    }

    if (ltype instanceof TyReactiveComplex) {
      if (environment.rules.IsNumeric(rtype, true) || rtype instanceof TyNativeLong) {
        assignResult = CanAssignResult.YesWithSetter;
        return;
      }
    }
    assignResult = environment.rules.CanAssignWithSet(ltype, rtype, false);
    environment.rules.CanTypeAStoreTypeB(ltype, rtype, StorageTweak.None, false);
  }
}
