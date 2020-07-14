/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree;

import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.*;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.shared.EnumStorage;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.junit.Assert;
import org.junit.Test;

public class SillyTypeIssues {
    @Test
    public void functional() {
        TyNativeFunctional functional = new TyNativeFunctional("x", null, FunctionStyleJava.InjectNameThenArgs);
        functional.getAdamaType();
        try {
            functional.getJavaConcreteType(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        try {
            functional.getJavaBoxType(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        try {
            functional.emit(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        functional.makeCopyWithNewPosition(functional);
    }

    @Test
    public void global() {
        TyNativeGlobalObject ngo = new TyNativeGlobalObject("X", "y");
        try {
            ngo.emit(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        try {
            ngo.getJavaBoxType(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        try {
            ngo.getJavaConcreteType(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        try {
            ngo.getAdamaType();
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        ngo.makeCopyWithNewPosition(ngo);
    }

    @Test
    public void ref1() {
        TyNativeRef ref = new TyNativeRef(Token.WRAP("x"));
        try {
            ref.getJavaBoxType(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        try {
            ref.getJavaConcreteType(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
    }

    @Test
    public void ref2() {
        TyReactiveRef ref = new TyReactiveRef(Token.WRAP("x"));
        try {
            ref.getJavaBoxType(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        try {
            ref.getJavaConcreteType(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        ref.makeCopyWithNewPosition(ref);
    }

    @Test
    public void reactive() {
        TyReactiveClient reactiveClient = new TyReactiveClient(Token.WRAP("X"));
        reactiveClient.makeCopyWithNewPosition(reactiveClient);
        reactiveClient.getAdamaType();

        TyReactiveDouble reactiveDouble = new TyReactiveDouble(Token.WRAP("X"));
        reactiveDouble.getAdamaType();

        TyReactiveEnum reactiveEnum = new TyReactiveEnum(Token.WRAP("E"), new EnumStorage("E"));
        reactiveEnum.getAdamaType();
        reactiveEnum.makeCopyWithNewPosition(reactiveClient);
        reactiveEnum.storage();

        TyReactiveStateMachineRef reactiveStateMachineRef = new TyReactiveStateMachineRef(Token.WRAP("X"));
        reactiveStateMachineRef.getAdamaType();
        reactiveStateMachineRef.makeCopyWithNewPosition(reactiveStateMachineRef);
    }

    @Test
    public void lazy() {
        TyReactiveLazy lazy = new TyReactiveLazy(new TyNativeInteger(Token.WRAP("int")));
        try {
            lazy.emit(null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
        }
        lazy.getJavaConcreteType(null);
        lazy.getAdamaType();
        lazy.getJavaBoxType(null);
        lazy.makeCopyWithNewPosition(lazy);
    }

    @Test
    public void channel() {
        TyNativeChannel channel = new TyNativeChannel(null, new TokenizedItem<>(new TyNativeInteger(null)));
        channel.makeCopyWithNewPosition(channel);
    }

    @Test
    public void longcov() {
        TyReactiveLong rl = new TyReactiveLong(null);
        TyNativeLong nl = new TyNativeLong(null);
        rl.makeCopyWithNewPosition(rl);
        nl.makeCopyWithNewPosition(nl);
        Assert.assertEquals("long", rl.getAdamaType());
        Assert.assertEquals("long", nl.getAdamaType());
    }

    @Test
    public void nttable() {
        TyNativeTable tnt = new TyNativeTable(null, new TokenizedItem<>(Token.WRAP("FOO")));
        tnt.makeCopyWithNewPosition(tnt);
        Assert.assertEquals(tnt.getAdamaType(), "table<FOO>");
    }

    @Test
    public void glob() {
        new TyNativeGlobalObject(null, null).typing(null);
        new TyNativeGlobalObject(null, null).makeCopyWithNewPosition(new TyNativeGlobalObject(null, null));
    }

    @Test
    public void voidvoid() {
        TyNativeVoid v = new TyNativeVoid();
        v.emit((x) -> {});
        Assert.assertEquals("void", v.getAdamaType());
        Assert.assertEquals("void", v.getJavaBoxType(null));
        Assert.assertEquals("void", v.getJavaConcreteType(null));
        v.makeCopyWithNewPosition(null);
        v.typing(null);
    }

    @Test
    public void ptr() {
        Environment env = Environment.fresh(new Document(), new EnvironmentState(GlobalObjectPool.createPoolWithStdLib(), CompilerOptions.start().make()));
        StructureStorage ss = new StructureStorage(StorageSpecialization.Record, false, Token.WRAP("{"));
        ss.end(Token.WRAP("}"));
        TyReactiveRecord record = new TyReactiveRecord(Token.WRAP("R"), Token.WRAP("X"), ss);
        TyNativeReactiveRecordPtr ptr = new TyNativeReactiveRecordPtr(record);
        ptr.emit((t) -> {});
        ptr.getAdamaType();
        ptr.getJavaBoxType(env);
        ptr.getJavaConcreteType(env);
        ptr.makeCopyWithNewPosition(ptr);
        ptr.typing(env);
        ptr.getEmbeddedType(env);
        ptr.lookupMethod("foo", env);
    }
}
