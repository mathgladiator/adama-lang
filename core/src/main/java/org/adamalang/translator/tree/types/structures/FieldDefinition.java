/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.privacy.Policy;
import org.adamalang.translator.tree.privacy.PublicPolicy;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.reactive.*;

import java.util.LinkedHashSet;
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
  public final LinkedHashSet<String> variablesToWatch;
  public final LinkedHashSet<String> servicesToWatch;

  public Token lossyOrRequiredToken;
  public TyType type;

  public FieldDefinition(final Policy policy, final Token introToken, final TyType type, final Token nameToken, final Token equalsToken, final Expression computeExpression, final Expression defaultValueOverride, final Token lossyOrRequiredToken, final Token semicolonToken) {
    this.policy = policy;
    this.introToken = introToken;
    this.type = type;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.computeExpression = computeExpression;
    this.equalsToken = equalsToken;
    this.defaultValueOverride = defaultValueOverride;
    this.lossyOrRequiredToken = lossyOrRequiredToken;
    this.semicolonToken = semicolonToken;
    if (policy != null) {
      ingest(policy);
    }
    if (introToken != null) {
      ingest(introToken);
    }
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
    servicesToWatch = new LinkedHashSet<>();
    variablesToWatch = new LinkedHashSet<>();
  }

  public boolean isLossy() {
    if (lossyOrRequiredToken != null) {
      return "lossy".equals(lossyOrRequiredToken.text);
    }
    return false;
  }

  public boolean isRequired() {
    if (lossyOrRequiredToken != null) {
      return "required".equals(lossyOrRequiredToken.text);
    }
    return false;
  }

  public static FieldDefinition invent(final TyType type, final String name) {
    return new FieldDefinition(new PublicPolicy(null), null, type, Token.WRAP(name), null, null, null, null, null);
  }


  public static FieldDefinition inventId(DocumentPosition dp) {
    return new FieldDefinition(null, null, new TyReactiveInteger(null).withPosition(dp), Token.WRAP("id"), null, null, null, null, null);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (policy != null) {
      policy.emit(yielder);
    }
    if (introToken != null) {
      yielder.accept(introToken);
    }
    if (type != null) {
      type.emit(yielder);
    }
    yielder.accept(nameToken);
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
    yielder.accept(semicolonToken);
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
    if (type != null) {
      type = environment.rules.Resolve(type, false);
    }
    if (type == null && computeExpression != null) {
      type = computeExpression.typing(environment.scopeReactiveExpression().scopeWithCache("__c" + name).scopeWithComputeContext(ComputeContext.Computation), null /* no suggestion makes sense */);
      if (type != null) {
        type = new TyReactiveLazy(type).withPosition(type);
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
