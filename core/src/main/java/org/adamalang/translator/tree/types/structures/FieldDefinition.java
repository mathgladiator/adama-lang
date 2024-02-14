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
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.WatchSet;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.privacy.Policy;
import org.adamalang.translator.tree.privacy.PublicPolicy;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.traits.DetailNeverPublic;

import java.util.function.Consumer;

/** the definition for a field */
public class FieldDefinition extends StructureComponent {
  public final Expression computeExpression;
  public final Expression defaultValueOverride;
  public final Token equalsToken;
  public final Token introToken;
  public final String name;
  public final Token nameToken;
  public final Policy policy;
  public final Token semicolonToken;
  public final WatchSet watching;
  public final boolean readonly;

  public Token lossyOrRequiredToken;
  public Token uniqueToken;
  public TyType type;
  private Formatter.FirstAndLastToken fal;
  private CachePolicy cachePolicy;

  public final Token invalidationRuleToken;
  public final InvalidationRule invalidationRule;

  public FieldDefinition(final Policy policy, final Token introToken, final TyType type, final Token nameToken, Token invalidationRuleToken, final Token equalsToken, final Expression computeExpression, final Expression defaultValueOverride, final Token lossyOrRequiredToken, final Token uniqueToken, final Token semicolonToken) {
    this.policy = policy;
    this.introToken = introToken;
    this.type = type;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.computeExpression = computeExpression;
    this.equalsToken = equalsToken;
    this.defaultValueOverride = defaultValueOverride;
    this.lossyOrRequiredToken = lossyOrRequiredToken;
    this.uniqueToken = uniqueToken;
    this.semicolonToken = semicolonToken;
    if (policy != null) {
      ingest(policy);
    }
    this.invalidationRuleToken = invalidationRuleToken;
    if (invalidationRuleToken != null) {
      switch (invalidationRuleToken.text) {
        case "@updated":
          invalidationRule = InvalidationRule.DateTimeUpdated;
          break;
        case "@bump":
          invalidationRule = InvalidationRule.IntegerBump;
          break;
        default:
          this.invalidationRule = null;
      }
    } else {
      this.invalidationRule = null;
    }
    boolean _readonly = false;
    if (introToken != null) {
      ingest(introToken);
      _readonly = "readonly".equals(introToken.text);
    }
    this.readonly = _readonly;
    if (type != null) {
      ingest(type);
    }
    if (computeExpression != null) {
      ingest(computeExpression);
    }
    if (defaultValueOverride != null) {
      ingest(defaultValueOverride);
    }
    if (semicolonToken != null) {
      ingest(semicolonToken);
    }
    watching = new WatchSet();
  }

  public boolean isLossy() {
    if (lossyOrRequiredToken != null) {
      return "lossy".equals(lossyOrRequiredToken.text);
    }
    return false;
  }

  public void enableCache(CachePolicy cachePolicy) {
    this.cachePolicy = cachePolicy;
  }

  public boolean hasCachePolicy() {
    return cachePolicy != null;
  }

  public CachePolicy getCachePolicy() {
    return this.cachePolicy;
  }

  public boolean isRequired() {
    if (lossyOrRequiredToken != null) {
      return "required".equals(lossyOrRequiredToken.text);
    }
    return false;
  }

  public static FieldDefinition invent(final TyType type, final String name) {
    PublicPolicy policy = null;
    if (type != null) {
      policy = new PublicPolicy(null);
      policy.ingest(type);
    }
    FieldDefinition fd = new FieldDefinition(policy, null, type, Token.WRAP(name), null, null, null, null, null, null, null);
    fd.ingest(type);
    return fd;
  }

  public static FieldDefinition inventId(DocumentPosition dp) {
    FieldDefinition fd = new FieldDefinition(null, null, new TyReactiveInteger(false, null).withPosition(dp), Token.WRAP("id"), null, null, null, null, null, null, null);
    fd.ingest(dp);
    return fd;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (policy != null) {
      policy.emit(yielder);
    }
    if (introToken != null) {
      yielder.accept(introToken);
    }
    if (cachePolicy != null) {
      cachePolicy.emit(yielder);
    }
    if (type != null) {
      type.emit(yielder);
    }
    yielder.accept(nameToken);
    if (invalidationRuleToken != null) {
      yielder.accept(invalidationRuleToken);
    }
    if (equalsToken != null) {
      yielder.accept(equalsToken);
      if (computeExpression != null) {
        computeExpression.emit(yielder);
      }
      if (defaultValueOverride != null) {
        defaultValueOverride.emit(yielder);
      }
    }
    if (lossyOrRequiredToken != null) {
      yielder.accept(lossyOrRequiredToken);
    }
    if (uniqueToken != null) {
      yielder.accept(uniqueToken);
    }
    yielder.accept(semicolonToken);
  }

  @Override
  public void format(Formatter formatter) {
    if (policy != null) {
      policy.format(formatter);
    }
    if (type != null) {
      type.format(formatter);
    }
    if (cachePolicy != null) {
      cachePolicy.format(formatter);
    }
    if (equalsToken != null) {
      if (computeExpression != null) {
        computeExpression.format(formatter);
      }
      if (defaultValueOverride != null) {
        defaultValueOverride.format(formatter);
      }
    }
    Formatter.FirstAndLastToken fal = new Formatter.FirstAndLastToken();
    emit(fal);
    if (fal.first != null) {
      formatter.startLine(fal.first);
      formatter.endLine(fal.last);
    }
  }

  @Override
  public boolean equals(final Object otherRaw) {
    if (otherRaw instanceof FieldDefinition) {
      final var other = (FieldDefinition) otherRaw;
      if (name.equals(other.name)) {
        if (type != null && other.type != null) {
          return type.getAdamaType().equals(other.type.getAdamaType());
        }
      }
    }
    return false;
  }

  private Environment envOf(Environment priorEnv, final StructureStorage owningStructureStorage) {
    Environment env = priorEnv.scopeWithComputeContext(ComputeContext.Computation);
    if (owningStructureStorage.specialization == StorageSpecialization.Record) {
      env = env.scopeRecord(owningStructureStorage.name.text);
    }
    return env;
  }

  public void typing(final Environment priorEnv, final StructureStorage owningStructureStorage) {
    final var environment = envOf(priorEnv, owningStructureStorage);
    if (policy != null) {
      policy.typing(environment, owningStructureStorage);
    }
    if (invalidationRule != null) {
      if (owningStructureStorage.root) {
        environment.document.createError(this, String.format("The field '%s' has an invalidation event which is not valid for the root document", name));
      }
    }
    if (type != null) {
      type = environment.rules.Resolve(type, false);
    }
    if (type == null && computeExpression != null) {
      type = computeExpression.typing(environment.scopeReactiveExpression().scopeWithCache("__c" + name).scopeWithComputeContext(ComputeContext.Computation), null /* no suggestion makes sense */);
      if (type != null) {
        type = new TyReactiveLazy(type, hasCachePolicy()).withPosition(type);
      }
    }
    if (type != null) {
      type.typing(environment);
      if (defaultValueOverride != null) {
        final var defType = defaultValueOverride.typing(environment, null /* no suggestion makes sense */);
        if (defType != null) {
          environment.rules.CanTypeAStoreTypeB(type, defType, StorageTweak.None, false);
        }
      }
    }
    if (type instanceof DetailNeverPublic) {
      environment.document.createError(this, String.format("Field has a type that is not allowed: %s", type.getAdamaType()));
    }
    if (type == null) {
      environment.document.createError(this, String.format("The field '%s' has no type", name));
    }
  }

  private static String cycleType(TyType type) {
    if (type instanceof TyReactiveRef) {
      return ((TyReactiveRef) type).ref;
    } else if (type instanceof TyReactiveRecord) {
      return ((TyReactiveRecord) type).name;
    } else if (type instanceof TyReactiveMaybe) {
      return cycleType(((TyReactiveMaybe) type).tokenizedElementType.item);
    } else if (type instanceof TyNativeList) {
      return cycleType(((TyNativeList) type).elementType);
    } else if (type instanceof TyNativeMaybe) {
      return cycleType(((TyNativeMaybe) type).tokenElementType.item);
    } else if (type instanceof TyReactiveTable) {
      return ((TyReactiveTable) type).recordName;
    } else if (type instanceof TyReactiveLazy) {
      return cycleType(((TyReactiveLazy) type).computedType);
    }
    return null;
  }

  public String getCycleType() {
    return cycleType(type);
  }
}
