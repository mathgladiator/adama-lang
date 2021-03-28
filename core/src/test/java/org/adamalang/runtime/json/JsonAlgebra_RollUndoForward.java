/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_RollUndoForward {
    @Test
    public void conflictFreeSimple() {
        ObjectNode undo = Utility.parseJsonObject("{\"x\":42}");
        ObjectNode redo = Utility.parseJsonObject("{\"y\":69}");
        JsonAlgebra.rollUndoForward(undo, redo);
        Assert.assertEquals("{\"x\":42}", undo.toString());
    }

    @Test
    public void simpleOverwriteNegatesUndo1() {
        ObjectNode undo = Utility.parseJsonObject("{\"x\":42}");
        ObjectNode redo = Utility.parseJsonObject("{\"x\":69}");
        JsonAlgebra.rollUndoForward(undo, redo);
        Assert.assertEquals("{}", undo.toString());
    }

    @Test
    public void simpleOverwriteNegatesUndo2() {
        ObjectNode undo = Utility.parseJsonObject("{\"x\":42}");
        ObjectNode redo = Utility.parseJsonObject("{\"x\":null}");
        JsonAlgebra.rollUndoForward(undo, redo);
        Assert.assertEquals("{}", undo.toString());
    }

    @Test
    public void simpleOverwriteNegatesUndo3() {
        ObjectNode undo = Utility.parseJsonObject("{\"x\":null}");
        ObjectNode redo = Utility.parseJsonObject("{\"x\":42}");
        JsonAlgebra.rollUndoForward(undo, redo);
        Assert.assertEquals("{}", undo.toString());
    }

    @Test
    public void recurseNoConflict() {
        ObjectNode undo = Utility.parseJsonObject("{\"z\":{\"x\":42}}");
        ObjectNode redo = Utility.parseJsonObject("{\"z\":{\"y\":69}}");
        JsonAlgebra.rollUndoForward(undo, redo);
        Assert.assertEquals("{\"z\":{\"x\":42}}", undo.toString());
    }

    @Test
    public void recurseConflict1() {
        ObjectNode undo = Utility.parseJsonObject("{\"z\":{\"x\":42}}");
        ObjectNode redo = Utility.parseJsonObject("{\"z\":{\"x\":69}}");
        JsonAlgebra.rollUndoForward(undo, redo);
        Assert.assertEquals("{}", undo.toString());
    }

    @Test
    public void recurseConflict2() {
        ObjectNode undo = Utility.parseJsonObject("{\"z\":{\"x\":42, \"y\": 50}}");
        ObjectNode redo = Utility.parseJsonObject("{\"z\":{\"x\":69}}");
        JsonAlgebra.rollUndoForward(undo, redo);
        Assert.assertEquals("{\"z\":{\"y\":50}}", undo.toString());
    }
}
