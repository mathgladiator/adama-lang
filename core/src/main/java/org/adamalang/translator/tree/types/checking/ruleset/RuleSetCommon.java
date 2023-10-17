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
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.privacy.PublicPolicy;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.properties.CanTestEqualityResult;
import org.adamalang.translator.tree.types.checking.properties.WrapInstruction;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.IsReactiveValue;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;

import java.util.Map;

public class RuleSetCommon {
  static boolean AreBothChannelTypesCompatible(final Environment environment, final TyType typeA, final TyType typeB) {
    final var resolveA = Resolve(environment, typeA, true);
    final var resolveB = Resolve(environment, typeB, true);
    if (resolveA != null && resolveB != null) {
      final var aChannel = resolveA instanceof TyNativeChannel;
      final var bChannel = resolveB instanceof TyNativeChannel;
      if (aChannel && bChannel) {
        final var aChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveA, true);
        final var bChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveB, true);
        return aChildType.getAdamaType().equals(bChildType.getAdamaType());
      }
    }
    return false;
  }

  public static TyType EnsureRegisteredAndDedupe(final Environment environment, final TyType type, final boolean silent) {
    if (type != null && environment.rules.IsMaybe(type, true)) {
      final var childType = environment.rules.ExtractEmbeddedType(type, silent);
      final var newChildType = EnsureRegisteredAndDedupe(environment, childType, silent);
      if (childType == newChildType) {
        return type;
      } else {
        return new TyNativeMaybe(type.behavior, null, null, new TokenizedItem<>(newChildType));
      }
    }
    if (type != null && environment.rules.IsNativeArrayOfStructure(type, true)) {
      final var childType = environment.rules.ExtractEmbeddedType(type, silent);
      final var newChildType = EnsureRegisteredAndDedupe(environment, childType, silent);
      if (childType == newChildType) {
        return type;
      } else {
        return new TyNativeArray(type.behavior, newChildType, null);
      }
    }
    if (type != null && environment.rules.IsNativeMessage(type, true)) {
      final var msg = (TyNativeMessage) type;
      if (msg.storage.anonymous) {
        var next = (TyType) environment.document.findPriorMessage(msg.storage, environment);
        if (next != null) {
          return next;
        }
        for (final Map.Entry<String, FieldDefinition> entry : msg.storage.fields.entrySet()) {
          entry.getValue().type = EnsureRegisteredAndDedupe(environment, entry.getValue().type, silent);
        }
        next = (TyType) environment.document.findPriorMessage(msg.storage, environment);
        if (next != null) {
          return next;
        }
        environment.document.add(msg);
        return msg;
      }
    }
    return type;
  }

  /** extract an embedded type */
  public static TyType ExtractEmbeddedType(final Environment environment, final TyType tyType, final boolean silent) {
    return tyType != null ? Resolve(environment, ((DetailContainsAnEmbeddedType) tyType).getEmbeddedType(environment), silent) : null;
  }

  /** given two types, pick the better type */
  public static TyType GetMaxType(final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    final var resolveA = Resolve(environment, typeA, silent);
    final var resolveB = Resolve(environment, typeB, silent);
    if (resolveA == null || resolveB == null) {
      return null;
    }
    final var aReactive = RuleSetCommon.TestReactive(resolveA) && resolveA instanceof DetailComputeRequiresGet;
    final var bReactive = RuleSetCommon.TestReactive(resolveB) && resolveB instanceof DetailComputeRequiresGet;
    if (aReactive && bReactive) {
      if (RuleSetEquality.CanTestEquality(environment, ((DetailComputeRequiresGet) resolveA).typeAfterGet(environment), ((DetailComputeRequiresGet) resolveB).typeAfterGet(environment), false) != CanTestEqualityResult.No) {
        return resolveA.makeCopyWithNewPosition(typeB, TypeBehavior.ReadOnlyNativeValue).withPosition(typeA);
      }
    }
    final var aRecord = resolveA instanceof TyReactiveRecord;
    final var bRecord = resolveB instanceof TyReactiveRecord;
    if (aRecord && bRecord) {
      if (((TyReactiveRecord) resolveA).name.equals(((TyReactiveRecord) resolveB).name)) {
        return resolveA.makeCopyWithNewPosition(typeB, TypeBehavior.ReadOnlyNativeValue).withPosition(typeA);
      }
    }
    if (AreBothChannelTypesCompatible(environment, resolveA, resolveB)) {
      return resolveA;
    }
    final var aInt = IsInteger(environment, resolveA, true);
    final var bInt = IsInteger(environment, resolveB, true);
    final var aLong = IsLong(environment, resolveA, true);
    final var bLong = IsLong(environment, resolveB, true);
    final var aDouble = IsDouble(environment, resolveA, true);
    final var bDouble = IsDouble(environment, resolveB, true);
    // both are integers
    if (aInt && bInt) {
      return new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(resolveA).withPosition(typeB);
    }
    if ((aInt || aLong) && (bInt || bLong)) {
      return new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(resolveA).withPosition(typeB);
    }
    // all of numeric
    if (aInt && bDouble || aDouble && bInt || aDouble && bDouble) {
      return new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(typeA).withPosition(typeB);
    }
    if (RuleSetEquality.CanTestEquality(environment, resolveA, resolveB, true) != CanTestEqualityResult.No) {
      return resolveA.makeCopyWithNewPosition(typeB, TypeBehavior.ReadOnlyNativeValue).withPosition(typeA);
    }
    final var aMessage = RuleSetMessages.IsNativeMessage(environment, resolveA, true);
    final var bMessage = RuleSetMessages.IsNativeMessage(environment, resolveB, true);
    if (aMessage && bMessage) {
      final var nextMessageType = messageUnion(environment, (TyNativeMessage) resolveA, (TyNativeMessage) resolveB, silent);
      if (nextMessageType != null) {
        return nextMessageType;
      }
    }
    final var aMaybe = RuleSetMaybe.IsMaybe(environment, resolveA, true);
    final var bMaybe = RuleSetMaybe.IsMaybe(environment, resolveB, true);
    if (aMaybe || bMaybe) {
      TyType maxType = null;
      if (aMaybe && bMaybe) {
        final var aChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveA, true);
        final var bChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveB, true);
        maxType = GetMaxType(environment, aChildType, bChildType, silent);
      } else if (aMaybe && !bMaybe) {
        final var aChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveA, true);
        maxType = GetMaxType(environment, aChildType, resolveB, silent);
      } else if (!aMaybe && bMaybe) {
        final var bChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveB, true);
        maxType = GetMaxType(environment, resolveA, bChildType, silent);
      }
      if (maxType != null) {
        return new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(maxType));
      }
    }
    final var aList = RuleSetLists.IsNativeList(environment, resolveA, true);
    final var bList = RuleSetLists.IsNativeList(environment, resolveB, true);
    if (aList && bList) {
      final var aChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveA, true);
      final var bChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveB, true);
      if (aChildType != null && bChildType != null && aChildType.getAdamaType().equals(bChildType.getAdamaType())) {
        return resolveA;
      }
    }

    final var aMap = RuleSetMap.IsNativeMap(environment, resolveA);
    final var bMap = RuleSetMap.IsNativeMap(environment, resolveB);
    if (aMap && bMap) {
      final var aDomainType = ((TyNativeMap) resolveA).getDomainType(environment);
      final var bDomainType = ((TyNativeMap) resolveB).getDomainType(environment);
      final var aRangeType = ((TyNativeMap) resolveA).getRangeType(environment);
      final var bRangeType = ((TyNativeMap) resolveB).getRangeType(environment);
      final var maxDomainType = GetMaxType(environment, aDomainType, bDomainType, silent);
      final var maxRangeType = GetMaxType(environment, aRangeType, bRangeType, silent);
      return new TyNativeMap(TypeBehavior.ReadOnlyNativeValue, null, null, null, maxDomainType, null, maxRangeType, null);
    }

    final var aPair = RuleSetMap.IsNativePair(environment, resolveA);
    final var bPair = RuleSetMap.IsNativePair(environment, resolveB);
    if (aPair && bPair) {
      final var aDomainType = ((TyNativePair) resolveA).getDomainType(environment);
      final var bDomainType = ((TyNativePair) resolveB).getDomainType(environment);
      final var aRangeType = ((TyNativePair) resolveA).getRangeType(environment);
      final var bRangeType = ((TyNativePair) resolveB).getRangeType(environment);
      final var maxDomainType = GetMaxType(environment, aDomainType, bDomainType, silent);
      final var maxRangeType = GetMaxType(environment, aRangeType, bRangeType, silent);
      return new TyNativePair(TypeBehavior.ReadOnlyNativeValue, null, null, null, maxDomainType, null, maxRangeType, null);
    }

    final var aArray = RuleSetArray.IsNativeArray(environment, resolveA, true);
    final var bArray = RuleSetArray.IsNativeArray(environment, resolveB, true);
    if (aArray && bArray) {
      final var aChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveA, true);
      final var bChildType = RuleSetCommon.ExtractEmbeddedType(environment, resolveB, true);
      final var maxType = GetMaxType(environment, aChildType, bChildType, silent);
      return new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, maxType, null);
    }
    SignalTypeCompatibility(environment, typeA, typeB, silent);
    return null;
  }

  public static WrapInstruction GetMaxTypeBasedWrappingInstruction(final Environment environment, final TyType typeA, final TyType typeB) {
    final var resolveA = Resolve(environment, typeA, true);
    final var resolveB = Resolve(environment, typeB, true);
    if (resolveA == null || resolveB == null) {
      return null;
    }
    final var aMaybe = RuleSetMaybe.IsMaybe(environment, resolveA, true);
    final var bMaybe = RuleSetMaybe.IsMaybe(environment, resolveB, true);
    if (aMaybe && !bMaybe) {
      return WrapInstruction.WrapBWithMaybe;
    } else if (!aMaybe && bMaybe) {
      return WrapInstruction.WrapAWithMaybe;
    }
    return WrapInstruction.None;
  }

  public static TyType Resolve(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      if (tyType instanceof DetailContainsAnEmbeddedType) {
        TyType childType = Resolve(environment, ((DetailContainsAnEmbeddedType) tyType).getEmbeddedType(environment), silent);
        if (childType == null) {
          if (!silent) {
            environment.document.createError(tyTypeOriginal, String.format("The type '%s' is using a type that was not found.", tyTypeOriginal.getAdamaType()));
          }
          return null;
        }
      }
      while (tyType instanceof DetailRequiresResolveCall) {
        tyType = ((DetailRequiresResolveCall) tyType).resolve(environment);
      }
      if (tyType == null) {
        SignalTypeNotFound(environment, tyTypeOriginal, silent);
      }
    }
    return tyType;
  }

  static void SignalTypeNotFound(final Environment environment, final TyType type, final boolean silent) {
    if (!silent) {
      environment.document.createError(type, String.format("Type not found: the type '%s' was not found.", type.getAdamaType()));
    }
  }

  public static boolean IsBoolean(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeBoolean || tyType instanceof TyReactiveBoolean) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  static void SignalTypeFailure(final Environment environment, final TyType expected, final TyType given, final boolean silent) {
    if (!silent && given != null) {
      environment.document.createError(given, String.format("Type check failure: must have a type of '%s', but the type is actually '%s'", expected.getAdamaType(), given.getAdamaType()));
    }
  }

  static boolean IsDouble(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeDouble || tyType instanceof TyReactiveDouble) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  static boolean IsComplex(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeComplex || tyType instanceof TyReactiveComplex) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeComplex(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsInteger(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeInteger || tyType instanceof TyReactiveInteger) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsText(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyReactiveText) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsLong(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeLong || tyType instanceof TyReactiveLong) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsPrincipal(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativePrincipal || tyType instanceof TyNativeSecurePrincipal || tyType instanceof TyReactivePrincipal) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsNumeric(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeInteger || tyType instanceof TyReactiveInteger || tyType instanceof TyNativeDouble || tyType instanceof TyReactiveDouble) {
        return true;
      }
      if (!silent && tyTypeOriginal != null) {
        environment.document.createError(tyTypeOriginal, String.format("Type check failure: Must have a type of '%s' or '%s', but the type is actually '%s'", //
                new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null).getAdamaType(), //
                new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null).getAdamaType(), tyTypeOriginal.getAdamaType()));
      }
    }
    return false;
  }

  public static boolean IsString(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeString || tyType instanceof TyReactiveString) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsDynamic(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeDynamic || tyType instanceof TyReactiveDynamic) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeDynamic(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsAsset(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeAsset || tyType instanceof TyReactiveAsset) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeAsset(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  private static TyType messageUnion(final Environment environment, final TyNativeMessage aActualMessage, final TyNativeMessage bActualMessage, final boolean silent) {
    if (aActualMessage == bActualMessage || aActualMessage.storage == bActualMessage.storage || aActualMessage.name.equals(bActualMessage.name)) { // they are the same
      return aActualMessage;
    }
    if (aActualMessage.storage.anonymous) {
      if (bActualMessage.storage.anonymous) {
        final var newStorage = new StructureStorage(Token.WRAP("Created_" + aActualMessage.name + "_" + bActualMessage.name), StorageSpecialization.Message, true, false, null);
        for (final Map.Entry<String, FieldDefinition> aEntry : aActualMessage.storage.fields.entrySet()) {
          final var bFd = bActualMessage.storage.fields.get(aEntry.getKey());
          final FieldDefinition toAdd;
          if (bFd != null) {
            PublicPolicy policy = new PublicPolicy(null);
            policy.ingest(bFd);
            toAdd = new FieldDefinition(policy, null, GetMaxType(environment, aEntry.getValue().type, bFd.type, false), bFd.nameToken, null, null, null, null, null, null);
            toAdd.ingest(bFd);
          } else {
            toAdd = aEntry.getValue();
          }
          newStorage.add(toAdd);
        }
        for (final Map.Entry<String, FieldDefinition> bEntry : bActualMessage.storage.fields.entrySet()) {
          final var aFd = aActualMessage.storage.fields.get(bEntry.getKey());
          if (aFd == null) {
            newStorage.add(bEntry.getValue());
          }
        }
        return new TyNativeMessage(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("_AutoMaxRecord_" + environment.autoVariable()), newStorage);
      } else {
        if (environment.rules.CanStructureAProjectIntoStructureB(bActualMessage, aActualMessage, silent)) {
          return bActualMessage;
        }
      }
    } else {
      if (bActualMessage.storage.anonymous) {
        if (environment.rules.CanStructureAProjectIntoStructureB(aActualMessage, bActualMessage, silent)) {
          return aActualMessage;
        }
      } else if (!silent) {
        environment.document.createError(aActualMessage, String.format("The message types `%s` and `%s` can not be joined.", aActualMessage.getAdamaType(), bActualMessage.getAdamaType()));
      }
    }
    return null;
  }

  public static TyType ResolvePtr(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType instanceof TyNativeReactiveRecordPtr) {
      return ((TyNativeReactiveRecordPtr) tyType).source;
    }
    return tyType;
  }

  static void SignalTypeCompatibility(final Environment environment, final TyType expected, final TyType given, final boolean silent) {
    if (!silent && given != null) {
      environment.document.createError(given, String.format("Type check failure: The types '%s' and '%s' are not compatible for type unification", expected.getAdamaType(), given.getAdamaType()));
    }
  }

  public static boolean IsDate(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeDate || tyType instanceof TyReactiveDate) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeDate(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsTime(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeTime || tyType instanceof TyReactiveTime) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeTime(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsTimeSpan(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeTimeSpan || tyType instanceof TyReactiveTimeSpan) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeTimeSpan(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsDateTime(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeDateTime || tyType instanceof TyReactiveDateTime) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeDateTime(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }

  public static boolean IsResult(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeResult) {
        return true;
      }
      SignalTypeFailure(environment, new TyNativeDateTime(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }


  static boolean TestReactive(final TyType tyType) {
    return tyType instanceof IsReactiveValue || tyType instanceof TyReactiveRecord || tyType instanceof TyReactiveTable || tyType instanceof TyReactiveMaybe || tyType instanceof TyReactiveLazy;
  }
}
