/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.tree.definitions.DocumentEvent;
import org.adamalang.translator.tree.definitions.FunctionSpecialization;
import org.adamalang.translator.tree.definitions.MessageHandlerBehavior;
import org.adamalang.translator.tree.expressions.EnvLookupName;
import org.adamalang.translator.tree.expressions.MessageConversionStyle;
import org.adamalang.translator.tree.operands.AssignmentOp;
import org.adamalang.translator.tree.operands.BinaryOp;
import org.adamalang.translator.tree.operands.PostfixMutateOp;
import org.adamalang.translator.tree.operands.PrefixMutateOp;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.control.AlterControlFlowMode;
import org.adamalang.translator.tree.types.checking.properties.*;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.traits.details.IndexLookupStyle;
import org.junit.Assert;
import org.junit.Test;

public class SillyEnumCoverageTests {
    @Test
    public void coverage() {
        Assert.assertNull(BinaryOp.fromText("NOPEx"));
        PostfixMutateOp.fromText("!x");
        PrefixMutateOp.fromText("!x");
        AssignmentOp.fromText("!x");
        WrapInstruction.None.toString();
        WrapInstruction.valueOf("WrapBWithMaybe");
        MessageHandlerBehavior.EnqueueItemIntoNativeChannel.toString();
        MessageHandlerBehavior.valueOf("EnqueueItemIntoNativeChannel");
        DocumentEvent.ClientConnected.toString();
        DocumentEvent.valueOf("ClientConnected");
        FunctionSpecialization.Pure.toString();
        FunctionSpecialization.valueOf("Pure");
        MessageConversionStyle.Maybe.toString();
        MessageConversionStyle.valueOf("Maybe");
        AlterControlFlowMode.Abort.toString();
        AlterControlFlowMode.valueOf("Abort");
        ControlFlow.Open.toString();
        ControlFlow.valueOf("Open");
    }

    @Test
    public void coverageSimple() {
        AssignableEmbedType.None.toString();
        CanAssignResult.No.toString();
        CanBumpResult.No.toString();
        CanMathResult.No.toString();
        CanTestEqualityResult.No.toString();
        StorageTweak.None.toString();
        WrapInstruction.None.toString();
        StorageSpecialization.Message.toString();
        ComputeContext.Assignment.toString();
        EnvLookupName.Blocked.toString();
        IndexLookupStyle.Method.toString();
        ComputeContext.Computation.toString();
    }
}
