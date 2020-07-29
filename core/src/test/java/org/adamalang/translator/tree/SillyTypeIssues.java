/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree;

import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeChannel;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.natives.TyNativeReactiveRecordPtr;
import org.adamalang.translator.tree.types.natives.TyNativeRef;
import org.adamalang.translator.tree.types.natives.TyNativeTable;
import org.adamalang.translator.tree.types.natives.TyNativeVoid;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.TyReactiveClient;
import org.adamalang.translator.tree.types.reactive.TyReactiveDouble;
import org.adamalang.translator.tree.types.reactive.TyReactiveEnum;
import org.adamalang.translator.tree.types.reactive.TyReactiveLazy;
import org.adamalang.translator.tree.types.reactive.TyReactiveLong;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.reactive.TyReactiveRef;
import org.adamalang.translator.tree.types.reactive.TyReactiveStateMachineRef;
import org.adamalang.translator.tree.types.shared.EnumStorage;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.junit.Assert;
import org.junit.Test;

public class SillyTypeIssues {
  @Test
  public void channel() {
    final var channel = new TyNativeChannel(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)));
    channel.makeCopyWithNewPosition(channel, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void functional() {
    final var functional = new TyNativeFunctional("x", null, FunctionStyleJava.InjectNameThenArgs);
    functional.getAdamaType();
    try {
      functional.getJavaConcreteType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    try {
      functional.getJavaBoxType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    try {
      functional.emit(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    functional.makeCopyWithNewPosition(functional, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void glob() {
    new TyNativeGlobalObject(null, null).typing(null);
    new TyNativeGlobalObject(null, null).makeCopyWithNewPosition(new TyNativeGlobalObject(null, null), TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void global() {
    final var ngo = new TyNativeGlobalObject("X", "y");
    try {
      ngo.emit(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    try {
      ngo.getJavaBoxType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    try {
      ngo.getJavaConcreteType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    try {
      ngo.getAdamaType();
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    ngo.makeCopyWithNewPosition(ngo, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void lazy() {
    final var lazy = new TyReactiveLazy(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("int")));
    try {
      lazy.emit(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    lazy.getJavaConcreteType(null);
    lazy.getAdamaType();
    lazy.getJavaBoxType(null);
    lazy.makeCopyWithNewPosition(lazy, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void longcov() {
    final var rl = new TyReactiveLong(null);
    final var nl = new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null);
    rl.makeCopyWithNewPosition(rl, TypeBehavior.ReadOnlyNativeValue);
    nl.makeCopyWithNewPosition(nl, TypeBehavior.ReadOnlyNativeValue);
    Assert.assertEquals("long", rl.getAdamaType());
    Assert.assertEquals("long", nl.getAdamaType());
  }

  @Test
  public void nttable() {
    final var tnt = new TyNativeTable(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(Token.WRAP("FOO")));
    tnt.makeCopyWithNewPosition(tnt, TypeBehavior.ReadOnlyNativeValue);
    Assert.assertEquals(tnt.getAdamaType(), "table<FOO>");
  }

  @Test
  public void ptr() {
    final var env = Environment.fresh(new Document(), new EnvironmentState(GlobalObjectPool.createPoolWithStdLib(), CompilerOptions.start().make()));
    final var ss = new StructureStorage(StorageSpecialization.Record, false, Token.WRAP("{"));
    ss.end(Token.WRAP("}"));
    final var record = new TyReactiveRecord(Token.WRAP("R"), Token.WRAP("X"), ss);
    final var ptr = new TyNativeReactiveRecordPtr(TypeBehavior.ReadOnlyNativeValue, record);
    ptr.emit(t -> {});
    ptr.getAdamaType();
    ptr.getJavaBoxType(env);
    ptr.getJavaConcreteType(env);
    ptr.makeCopyWithNewPosition(ptr, TypeBehavior.ReadOnlyNativeValue);
    ptr.typing(env);
    ptr.getEmbeddedType(env);
    ptr.lookupMethod("foo", env);
  }

  @Test
  public void reactive() {
    final var reactiveClient = new TyReactiveClient(Token.WRAP("X"));
    reactiveClient.makeCopyWithNewPosition(reactiveClient, TypeBehavior.ReadOnlyNativeValue);
    reactiveClient.getAdamaType();
    final var reactiveDouble = new TyReactiveDouble(Token.WRAP("X"));
    reactiveDouble.getAdamaType();
    final var reactiveEnum = new TyReactiveEnum(Token.WRAP("E"), new EnumStorage("E"));
    reactiveEnum.getAdamaType();
    reactiveEnum.makeCopyWithNewPosition(reactiveClient, TypeBehavior.ReadOnlyNativeValue);
    reactiveEnum.storage();
    final var reactiveStateMachineRef = new TyReactiveStateMachineRef(Token.WRAP("X"));
    reactiveStateMachineRef.getAdamaType();
    reactiveStateMachineRef.makeCopyWithNewPosition(reactiveStateMachineRef, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void ref1() {
    final var ref = new TyNativeRef(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("x"));
    try {
      ref.getJavaBoxType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    try {
      ref.getJavaConcreteType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
  }

  @Test
  public void ref2() {
    final var ref = new TyReactiveRef(Token.WRAP("x"));
    try {
      ref.getJavaBoxType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    try {
      ref.getJavaConcreteType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {}
    ref.makeCopyWithNewPosition(ref, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void voidvoid() {
    final var v = new TyNativeVoid();
    v.emit(x -> {});
    Assert.assertEquals("void", v.getAdamaType());
    Assert.assertEquals("void", v.getJavaBoxType(null));
    Assert.assertEquals("void", v.getJavaConcreteType(null));
    v.makeCopyWithNewPosition(null, TypeBehavior.ReadOnlyNativeValue);
    v.typing(null);
  }
}
