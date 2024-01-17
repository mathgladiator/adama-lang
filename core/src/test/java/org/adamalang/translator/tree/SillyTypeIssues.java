/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.translator.tree;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.translator.env.*;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TypeBehavior;
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
  public void channel() {
    final var channel =
        new TyNativeChannel(
            TypeBehavior.ReadOnlyNativeValue,
            null,
            null,
            new TokenizedItem<>(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)));
    channel.makeCopyWithNewPosition(channel, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void functional() {
    final var functional = new TyNativeFunctional("x", null, FunctionStyleJava.InjectNameThenArgs);
    functional.getAdamaType();
    functional.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
    try {
      functional.getJavaConcreteType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    try {
      functional.getJavaBoxType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    try {
      functional.emit(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    functional.makeCopyWithNewPosition(functional, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void glob() {
    new TyNativeGlobalObject(null, null, true).typing(null);
    new TyNativeGlobalObject(null, null, true)
        .makeCopyWithNewPosition(
            new TyNativeGlobalObject(null, null, true), TypeBehavior.ReadOnlyNativeValue);
    new TyNativeGlobalObject(null, null, true).writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void global() {
    final var ngo = new TyNativeGlobalObject("X", "y", true);
    try {
      ngo.emit(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    try {
      ngo.getJavaBoxType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    try {
      ngo.getJavaConcreteType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    try {
      ngo.getAdamaType();
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    ngo.makeCopyWithNewPosition(ngo, TypeBehavior.ReadOnlyNativeValue);
  }

  @Test
  public void lazy() {
    final var lazy =
        new TyReactiveLazy(
            new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("int")));
    try {
      lazy.emit(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    lazy.getJavaConcreteType(null);
    lazy.getAdamaType();
    lazy.getJavaBoxType(null);
    lazy.makeCopyWithNewPosition(lazy, TypeBehavior.ReadOnlyNativeValue);
    lazy.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void longcov() {
    final var rl = new TyReactiveLong(false, null);
    final var nl = new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null);
    rl.makeCopyWithNewPosition(rl, TypeBehavior.ReadOnlyNativeValue);
    nl.makeCopyWithNewPosition(nl, TypeBehavior.ReadOnlyNativeValue);
    Assert.assertEquals("r<long>", rl.getAdamaType());
    Assert.assertEquals("long", nl.getAdamaType());
  }

  @Test
  public void nttable() {
    final var tnt =
        new TyNativeTable(
            TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(Token.WRAP("FOO")));
    tnt.makeCopyWithNewPosition(tnt, TypeBehavior.ReadOnlyNativeValue);
    Assert.assertEquals(tnt.getAdamaType(), "table<FOO>");
    tnt.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void rxtable() {
    final var rxt = new TyReactiveTable(false, null, new TokenizedItem<>(Token.WRAP("FOO")));
    rxt.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void ptr() {
    final var env =
        Environment.fresh(
            new Document(),
            new EnvironmentState(
                GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling), CompilerOptions.start().make()));
    final var ss = new StructureStorage(Token.WRAP("s"), StorageSpecialization.Record, false, false, Token.WRAP("{"));
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
    ptr.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void reactive() {
    final var reactiveClient = new TyReactivePrincipal(false, Token.WRAP("X"));
    reactiveClient.makeCopyWithNewPosition(reactiveClient, TypeBehavior.ReadOnlyNativeValue);
    reactiveClient.getAdamaType();
    final var reactiveDouble = new TyReactiveDouble(false, Token.WRAP("X"));
    reactiveDouble.getAdamaType();
    final var reactiveEnum = new TyReactiveEnum(false, Token.WRAP("E"), new EnumStorage("E"));
    reactiveEnum.getAdamaType();
    reactiveEnum.makeCopyWithNewPosition(reactiveClient, TypeBehavior.ReadOnlyNativeValue);
    reactiveEnum.storage();
    final var reactiveStateMachineRef = new TyReactiveStateMachineRef(false, Token.WRAP("X"));
    reactiveStateMachineRef.getAdamaType();
    reactiveStateMachineRef.makeCopyWithNewPosition(
        reactiveStateMachineRef, TypeBehavior.ReadOnlyNativeValue);
    reactiveStateMachineRef.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
    final var reactiveMap =
        new TyReactiveMap(
            false,
            Token.WRAP("X"),
            Token.WRAP("X"),
            reactiveStateMachineRef,
            Token.WRAP("X"),
            reactiveStateMachineRef,
            Token.WRAP("X"));
    reactiveMap.getAdamaType();
    reactiveMap.makeCopyWithNewPosition(reactiveClient, TypeBehavior.ReadWriteNative);
  }

  @Test
  public void native_enum() {
    TyNativeEnum e =
        new TyNativeEnum(
            TypeBehavior.ReadWriteWithSetGet,
            null,
            Token.WRAP("E"),
            null,
            new EnumStorage("E"),
            null);
    e.getRxStringCodexName();
  }

  @Test
  public void ref1() {
    final var ref = new TyNativeRef(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("x"));
    try {
      ref.getJavaBoxType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    try {
      ref.getJavaConcreteType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
  }

  @Test
  public void ref2() {
    final var ref = new TyReactiveRef(false, Token.WRAP("x"));
    try {
      ref.getJavaBoxType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    try {
      ref.getJavaConcreteType(null);
      Assert.fail();
    } catch (final UnsupportedOperationException uoe) {
    }
    ref.makeCopyWithNewPosition(ref, TypeBehavior.ReadOnlyNativeValue);
    ref.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
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
    v.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void ntprincipal() {
    new TyNativePrincipal(null, null, null).writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void future() {
    new TyNativeFuture(null, null, null, new TokenizedItem<>(new TyNativeVoid()))
        .writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void securentprincipal() {
    new TyNativeSecurePrincipal(null, null, null, null, null, null).writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
  }

  @Test
  public void ntcomplex() {
    new TyNativeComplex(null, null, null).writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
    Assert.assertEquals("complex", new TyNativeComplex(null, null, null).getAdamaType());
    Assert.assertEquals(
        "r<complex>",
        new TyReactiveComplex(false, null)
            .makeCopyWithNewPosition(DocumentPosition.ZERO, TypeBehavior.ReadWriteWithSetGet)
            .getAdamaType());
  }

  @Test
  public void dynamics() {
    TyReactiveDynamic rdyn = new TyReactiveDynamic(false, Token.WRAP("dynamic"));
    rdyn.makeCopyWithNewPosition(rdyn, TypeBehavior.ReadWriteNative);
    rdyn.getAdamaType();
    TyNativeDynamic ndyn =
        new TyNativeDynamic(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("dynamic"));
    ndyn.getAdamaType();
  }

  @Test
  public void asset() {
    TyNativeAsset na =
        new TyNativeAsset(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("asset"));
    TyReactiveAsset ra = new TyReactiveAsset(false, Token.WRAP("asset"));
    ra.makeCopyWithNewPosition(ra, TypeBehavior.ReadOnlyNativeValue);
    ra.getAdamaType();
    na.getAdamaType();
  }

  public static class X {
    public String x;
    public int z;
    public NtPrincipal w;
  }

  @Test
  public void internalType() {
    TyInternalReadonlyClass ity = new TyInternalReadonlyClass(X.class);
    ity.makeCopyWithNewPosition(ity, null);
    ity.getAdamaType();
    ity.getJavaBoxType(null);
    ity.getJavaConcreteType(null);
    try {
      ity.emit(null);
    } catch (UnsupportedOperationException uoe) {
    }
    try {
      ity.emit(null);
    } catch (UnsupportedOperationException uoe) {
    }
    try {
      ity.writeTypeReflectionJson(null, ReflectionSource.Root);
    } catch (UnsupportedOperationException uoe) {
    }
    Environment env = Environment.fresh(new Document(), new EnvironmentState(GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling), CompilerOptions.start().make()));
    ity.getLookupType(env, "x");
    ity.getLookupType(env, "z");
    ity.getLookupType(env, "w");
    ity.getLookupType(env, "a");
  }

  @Test
  public void maps() {
    TyNativeMap map = new TyNativeMap(TypeBehavior.ReadOnlyNativeValue, null, null, null, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), null, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), null);
  }

  @Test
  public void pairs() {
    TyNativePair pair = new TyNativePair(TypeBehavior.ReadOnlyNativeValue, null, null, null, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), null, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), null);
  }
}
