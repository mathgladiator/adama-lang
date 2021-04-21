/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class JsonAlgebra_RollUndoForward {
    @SuppressWarnings("unchecked")
    private HashMap<String, Object> of(String json) {
        return (HashMap<String, Object>) new JsonStreamReader(json).readJavaTree();
    }

    private void is(String json, HashMap<String, Object> result) {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(result);
        Assert.assertEquals(json, writer.toString());
    }
    
    @Test
    public void conflictFreeSimple() {
        HashMap<String, Object> undo = of("{\"x\":42}");
        HashMap<String, Object> redo = of("{\"y\":69}");
        JsonAlgebra.rollUndoForward(undo, redo);
        is("{\"x\":42}", undo);
    }

    @Test
    public void simpleOverwriteNegatesUndo1() {
        HashMap<String, Object> undo = of("{\"x\":42}");
        HashMap<String, Object> redo = of("{\"x\":69}");
        JsonAlgebra.rollUndoForward(undo, redo);
        is("{}", undo);
    }

    @Test
    public void simpleOverwriteNegatesUndo2() {
        HashMap<String, Object> undo = of("{\"x\":42}");
        HashMap<String, Object> redo = of("{\"x\":null}");
        JsonAlgebra.rollUndoForward(undo, redo);
        is("{}", undo);
    }

    @Test
    public void simpleOverwriteNegatesUndo3() {
        HashMap<String, Object> undo = of("{\"x\":null}");
        HashMap<String, Object> redo = of("{\"x\":42}");
        JsonAlgebra.rollUndoForward(undo, redo);
        is("{}", undo);
    }

    @Test
    public void recurseNoConflict() {
        HashMap<String, Object> undo = of("{\"z\":{\"x\":42}}");
        HashMap<String, Object> redo = of("{\"z\":{\"y\":69}}");
        JsonAlgebra.rollUndoForward(undo, redo);
        is("{\"z\":{\"x\":42}}", undo);
    }

    @Test
    public void recurseConflict1() {
        HashMap<String, Object> undo = of("{\"z\":{\"x\":42}}");
        HashMap<String, Object> redo = of("{\"z\":{\"x\":69}}");
        JsonAlgebra.rollUndoForward(undo, redo);
        is("{}", undo);
    }

    @Test
    public void recurseConflict2() {
        HashMap<String, Object> undo = of("{\"z\":{\"x\":42,\"y\":50}}");
        HashMap<String, Object> redo = of("{\"z\":{\"x\":69}}");
        JsonAlgebra.rollUndoForward(undo, redo);
        is("{\"z\":{\"y\":50}}", undo);
    }
}
