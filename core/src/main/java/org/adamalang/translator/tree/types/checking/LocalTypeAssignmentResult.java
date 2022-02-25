/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.checking;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanAssignResult;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;

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
    ltype = ref.typing(environment.scopeWithComputeContext(ComputeContext.Assignment), null);
    rtype = expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    assignResult = environment.rules.CanAssignWithSet(ltype, rtype, false);
    environment.rules.CanTypeAStoreTypeB(ltype, rtype, StorageTweak.None, false);
  }
}
