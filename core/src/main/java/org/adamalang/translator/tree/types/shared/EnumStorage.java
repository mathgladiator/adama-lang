/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.shared;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.DefineDispatcher;
import org.adamalang.translator.tree.types.TypeCheckerProxy;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

import java.util.*;
import java.util.function.Consumer;

public class EnumStorage extends DocumentPosition {
  public final LinkedHashMap<String, HashMap<String, ArrayList<DefineDispatcher>>> dispatchersByNameThenSignature;
  public final HashSet<String> duplicates;
  public final LinkedHashMap<String, Integer> options;
  private final ArrayList<Consumer<Consumer<Token>>> emissions;
  private final String name;
  private final HashMap<String, Integer> signatureToNameAndId;
  private String defaultLabel;
  private int defaultValue;
  private boolean seenDefaultYet;
  private int signatureIdSource;

  public EnumStorage(final String name) {
    this.name = name;
    options = new LinkedHashMap<>();
    defaultLabel = null;
    emissions = new ArrayList<>();
    seenDefaultYet = false;
    dispatchersByNameThenSignature = new LinkedHashMap<>();
    signatureIdSource = 0;
    signatureToNameAndId = new HashMap<>();
    duplicates = new HashSet<>();
    defaultValue = 0;
  }

  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("values");
    writer.beginObject();
    for (Map.Entry<String, Integer> option : options.entrySet()) {
      writer.writeObjectFieldIntro(option.getKey());
      writer.writeInteger(option.getValue());
    }
    writer.endObject();
    writer.writeObjectFieldIntro("names");
    writer.beginObject();
    for (Map.Entry<String, Integer> option : options.entrySet()) {
      writer.writeObjectFieldIntro(option.getValue());
      writer.writeString(option.getKey());
    }
    writer.endObject();
    writer.writeObjectFieldIntro("default");
    writer.writeString(defaultLabel);
    writer.endObject();
  }

  public void add(final Token isDefault, final Token optionToken, final Token colonToken, final Token valueToken, final int value) {
    ingest(isDefault);
    emissions.add(yielder -> {
      if (isDefault != null) {
        yielder.accept(isDefault);
      }
      yielder.accept(optionToken);
      if (colonToken != null) {
        yielder.accept(colonToken);
        yielder.accept(valueToken);
      }
    });
    if (options.containsKey(optionToken.text) || options.containsValue(value)) {
      duplicates.add(optionToken.text);
    }
    options.put(optionToken.text, value);
    if (isDefault != null || !seenDefaultYet) {
      defaultLabel = optionToken.text;
      defaultValue = value;
      seenDefaultYet = true;
    }
  }

  public void associate(final DefineDispatcher dispatcher) {
    var others = dispatchersByNameThenSignature.get(dispatcher.functionName.text);
    if (others == null) {
      others = new HashMap<>();
      dispatchersByNameThenSignature.put(dispatcher.functionName.text, others);
    }
    final var signature = dispatcher.signature();
    var list = others.get(signature);
    int signatureId;
    final var key = dispatcher.functionName.text + "/" + signature;
    if (list == null) {
      list = new ArrayList<>();
      others.put(signature, list);
      signatureId = signatureIdSource;
      signatureToNameAndId.put(key, signatureId);
      signatureIdSource++;
    } else {
      signatureId = signatureToNameAndId.get(key);
    }
    dispatcher.positionIndex = list.size();
    dispatcher.signatureId = signatureId;
    list.add(dispatcher);
  }

  public TyNativeFunctional computeDispatcherType(final String name) {
    final var possible = dispatchersByNameThenSignature.get(name);
    if (possible == null) {
      return null;
    }
    final var overloads = new ArrayList<FunctionOverloadInstance>();
    for (final Map.Entry<String, ArrayList<DefineDispatcher>> dispatchers : possible.entrySet()) {
      overloads.add(dispatchers.getValue().get(0).computeFunctionOverloadInstance());
    }
    return new TyNativeFunctional(name, overloads, FunctionStyleJava.InjectNameThenExpressionAndArgs);
  }

  public void emit(final Consumer<Token> yielder) {
    for (final Consumer<Consumer<Token>> toEmit : emissions) {
      toEmit.accept(yielder);
    }
  }

  public String getDefaultLabel() {
    return defaultLabel;
  }

  public int getDefaultValue() {
    return defaultValue;
  }

  public int getId(final String name, final String signature) {
    return signatureToNameAndId.get(name + "/" + signature);
  }

  public void typing(final TypeCheckerProxy checker) {
    checker.register(Collections.EMPTY_SET, (environment) -> {
      if (options.size() == 0) {
        environment.document.createError(this, String.format("enum '%s' has no values", name), "EnumStorage");
      }
      for (final Map.Entry<String, HashMap<String, ArrayList<DefineDispatcher>>> dispatcherMap : dispatchersByNameThenSignature.entrySet()) {
        for (final Map.Entry<String, ArrayList<DefineDispatcher>> dispatchers : dispatcherMap.getValue().entrySet()) {
          final var firstDispatcher = dispatchers.getValue().get(0);
          // validate the values
          final var coverage = new HashSet<String>();
          for (final DefineDispatcher dd : dispatchers.getValue()) {
            if (!valueFound(dd, coverage)) {
              environment.document.createError(dd, String.format("Dispatcher '%s' has a value prefix '%s' which does not relate to any value within enum '%s'", dd.functionName.text, dd.valueToken.text, dd.enumNameToken.text), "EnumStorage");
            }
          }
          // make sure we have coverage
          StringBuilder missing = null;
          for (final String value : options.keySet()) {
            if (!coverage.contains(value)) {
              if (missing == null) {
                missing = new StringBuilder();
                missing.append(value);
              } else {
                missing.append(", ").append(value);
              }
            }
          }
          for (final DefineDispatcher other : dispatchers.getValue()) {
            if (firstDispatcher.returnType != null && other.returnType != null) {
              final var checkF2Oret = environment.rules.CanTypeAStoreTypeB(firstDispatcher.returnType, other.returnType, StorageTweak.None, true);
              final var checkO2Fret = environment.rules.CanTypeAStoreTypeB(other.returnType, firstDispatcher.returnType, StorageTweak.None, true);
              if (!checkF2Oret || !checkO2Fret) {
                environment.document.createError(firstDispatcher, String.format("Dispatcher '%s' do not agree on return type.", firstDispatcher.functionName.text), "EnumStorage");
                environment.document.createError(other, String.format("Dispatcher '%s' do not agree on return type.", firstDispatcher.functionName.text), "EnumStorage");
              }
            } else if (!(firstDispatcher.returnType == null && other.returnType == null)) {
              environment.document.createError(firstDispatcher, String.format("Dispatcher '%s' do not agree on return type.", firstDispatcher.functionName.text), "EnumStorage");
              environment.document.createError(other, String.format("Dispatcher '%s' do not agree on return type.", firstDispatcher.functionName.text), "EnumStorage");
            }
          }
          if (firstDispatcher.returnType != null) {
            for (final String value : options.keySet()) {
              if (findFindingDispatchers(dispatchers.getValue(), value, false).size() > 1) {
                environment.document.createError(firstDispatcher, String.format("Dispatcher '%s' returns and matches too many for '%s'", firstDispatcher.functionName.text, value), "EnumStorage");
              }
            }
          }
          if (missing != null) {
            environment.document.createError(this, String.format("Enum '%s' has a dispatcher '%s' which is incomplete and lacks: %s.", name, dispatcherMap.getKey(), missing), "EnumStorage");
          }
        }
      }
    });
  }

  private boolean valueFound(final DefineDispatcher dd, final HashSet<String> coverage) {
    var result = false;
    for (final String value : options.keySet()) {
      if (dd.starToken != null) {
        if (dd.valueToken == null) {
          result = true;
          coverage.add(value);
        } else if (value.startsWith(dd.valueToken.text)) {
          result = true;
          coverage.add(value);
        }
      } else if (dd.valueToken.text.equals(value)) {
        result = true;
        coverage.add(value);
      }
    }
    return result;
  }

  public TreeMap<String, DefineDispatcher> findFindingDispatchers(final ArrayList<DefineDispatcher> dispatchers, final String option, final boolean includeCatchAll) {
    final var matches = new TreeMap<String, DefineDispatcher>();
    for (final DefineDispatcher dispatcher : dispatchers) {
      if (dispatcher.starToken == null) {
        if (option.equals(dispatcher.valueToken.text)) {
          matches.put(option, dispatcher);
        }
      } else {
        if (dispatcher.valueToken == null) {
          if (includeCatchAll) {
            matches.put(option + " *", dispatcher);
          }
        } else {
          if (option.startsWith(dispatcher.valueToken.text)) {
            matches.put(dispatcher.valueToken.text + " ", dispatcher);
          }
        }
      }
    }
    return matches;
  }
}
