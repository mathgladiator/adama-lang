/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.codegen.*;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.TopLevelDocumentHandler;
import org.adamalang.translator.parser.exceptions.ParseException;
import org.adamalang.translator.parser.exceptions.ScanException;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.common.DocumentError;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.*;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeEnum;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailTypeProducesRootLevelCode;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class Document implements TopLevelDocumentHandler {
  public final HashSet<String> channelsThatAreFutures;
  public final HashMap<String, String> channelToMessageType;
  public final ArrayList<DefineDocumentEvent> events;
  public final ArrayList<DefineConstructor> constructors;
  public final ArrayList<DefineFunction> functionDefinitions;
  public final HashMap<String, TyNativeFunctional> functionTypes;
  public final ArrayList<DefineHandler> handlers;
  public final TyReactiveRecord root;
  public final ArrayList<DefineTest> tests;
  public final LinkedHashMap<String, DefineStateTransition> transitions;
  public final LinkedHashMap<String, TyType> types;
  public final TyNativeMessage viewerType;
  private final TreeMap<String, LatentCodeSnippet> dedupedLatentCodeSnippets;
  private final ArrayList<DocumentError> errorLists;
  private final HashSet<String> functionsDefines;
  private final ArrayList<LatentCodeSnippet> latentCodeSnippets;
  private final ArrayList<File> searchPaths;
  private final ArrayList<Consumer<Environment>> typeCheckOrder;
  private int autoClassId;
  private String className;

  public Document() {
    autoClassId = 0;
    errorLists = new ArrayList<>();
    typeCheckOrder = new ArrayList<>();
    root =
        new TyReactiveRecord(
            null,
            Token.WRAP("Root"),
            new StructureStorage(StorageSpecialization.Record, false, null));
    types = new LinkedHashMap<>();
    handlers = new ArrayList<>();
    transitions = new LinkedHashMap<>();
    tests = new ArrayList<>();
    events = new ArrayList<>();
    channelToMessageType = new HashMap<>();
    channelsThatAreFutures = new HashSet<>();
    searchPaths = new ArrayList<>();
    constructors = new ArrayList<>();
    latentCodeSnippets = new ArrayList<>();
    dedupedLatentCodeSnippets = new TreeMap<>();
    className = "DemoDocument";
    functionDefinitions = new ArrayList<>();
    functionTypes = new HashMap<>();
    functionsDefines = new HashSet<>();
    viewerType =
        new TyNativeMessage(
            TypeBehavior.ReadOnlyNativeValue,
            null,
            Token.WRAP("__ViewerType"),
            new StructureStorage(StorageSpecialization.Message, true, null));
    types.put("__ViewerType", viewerType);
  }

  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();

    // types
    writer.writeObjectFieldIntro("types");
    writer.beginObject();
    writer.writeObjectFieldIntro("#root");
    root.writeTypeReflectionJson(writer);
    for (Map.Entry<String, TyType> type : types.entrySet()) {
      writer.writeObjectFieldIntro(type.getKey());
      type.getValue().writeTypeReflectionJson(writer);
    }
    writer.endObject();

    writer.writeObjectFieldIntro("channels");
    writer.beginObject();
    for (Map.Entry<String, String> mapping : channelToMessageType.entrySet()) {
      writer.writeObjectFieldIntro(mapping.getKey());
      writer.writeString(mapping.getValue());
    }
    writer.endObject();

    String unified_constructor = null;
    writer.writeObjectFieldIntro("constructors");
    writer.beginArray();
    for (DefineConstructor dc : constructors) {
      if (dc.messageNameToken != null) {
        writer.writeString(dc.messageNameToken.text);
      }
      if (dc.unifiedMessageTypeNameToUse != null) {
        unified_constructor = dc.unifiedMessageTypeNameToUse;
      }
    }
    writer.endArray();

    if (unified_constructor != null) {
      writer.writeObjectFieldIntro("constructor");
      writer.writeString(unified_constructor);
    }

    writer.writeObjectFieldIntro("labels");
    writer.beginArray();
    for (String label : transitions.keySet()) {
      writer.writeString(label);
    }
    writer.endArray();

    // TODO: rpc (once I sort them out)
    writer.endObject();
  }

  @Override
  public void add(final BubbleDefinition bd) {
    if (root.storage.has(bd.nameToken.text)) {
      typeCheckOrder.add(
          env -> {
            env.document.createError(
                bd,
                String.format("Global field '%s' was already defined", bd.nameToken.text),
                "GlobalDefine");
          });
      return;
    }
    root.storage().add(bd, typeCheckOrder);
  }

  @Override
  public void add(final DefineConstructor dc) {
    constructors.add(dc);
  }

  @Override
  public void add(final DefineCustomPolicy customPolicy) {
    if (root.storage.policies.containsKey(customPolicy.name.text)) {
      typeCheckOrder.add(
          env -> {
            env.document.createError(
                customPolicy,
                String.format("Global policy '%s' was already defined", customPolicy.name.text),
                "GlobalDefine");
          });
      return;
    }
    root.storage.policies.put(customPolicy.name.text, customPolicy);
  }

  @Override
  public void add(final DefineDispatcher dd) {
    final var type = types.get(dd.enumNameToken.text);
    if (type != null && type instanceof TyNativeEnum) {
      typeCheckOrder.add(
          env -> {
            dd.typing(env);
          });
      ((TyNativeEnum) type).storage.associate(dd);
    } else {
      if (type == null) {
        typeCheckOrder.add(
            env -> {
              env.document.createError(
                  dd,
                  String.format(
                      "Dispatcher '%s' was unable to find the given enumeration type of '%s'",
                      dd.functionName.text, dd.enumNameToken.text),
                  "DocumentDefine");
            });
      } else {
        typeCheckOrder.add(
            env -> {
              env.document.createError(
                  dd,
                  String.format(
                      "Dispatcher '%s' found '%s', but it was '%s'",
                      dd.functionName.text, dd.enumNameToken.text, type.getAdamaType()),
                  "DocumentDefine");
            });
      }
    }
  }

  @Override
  public void add(final DefineDocumentEvent dce) {
    typeCheckOrder.add(env -> dce.typing(env));
    events.add(dce);
  }

  @Override
  public void add(DefineStatic ds) {
    typeCheckOrder.add((env) -> ds.typing(env));
    events.addAll(ds.events);
  }

  @Override
  public void add(final DefineFunction func) {
    functionsDefines.add(func.name);
    if (channelsThatAreFutures.contains(func.name)) {
      typeCheckOrder.add(
          env -> {
            env.document.createError(
                func,
                String.format(
                    "The %s '%s' was already defined as a channel.",
                    func.specialization == FunctionSpecialization.Pure ? "function" : "procedure",
                    func.name),
                "DocumentDefine");
          });
    }
    functionDefinitions.add(func);
    typeCheckOrder.add(
        env -> {
          func.typing(env);
        });
  }

  @Override
  public void add(final DefineHandler handler) {
    handlers.add(handler);
    channelToMessageType.put(handler.channel, handler.typeName);
    if (handler.behavior == MessageHandlerBehavior.EnqueueItemIntoNativeChannel) {
      if (functionsDefines.contains(handler.channel)) {
        typeCheckOrder.add(
            env -> {
              env.document.createError(
                  handler,
                  String.format("Handler '%s' was already defined as a function.", handler.channel),
                  "DocumentDefine");
            });
      }
      channelsThatAreFutures.add(handler.channel);
    }
    typeCheckOrder.add(
        env -> {
          handler.typing(env);
        });
  }

  @Override
  public void add(final DefineStateTransition transition) {
    transitions.put(transition.name, transition);
    typeCheckOrder.add(
        env -> {
          transition.typing(env);
        });
  }

  @Override
  public void add(final DefineTest test) {
    tests.add(test);
    typeCheckOrder.add(
        env -> {
          test.typing(env);
        });
  }

  @Override
  public void add(final FieldDefinition fd) {
    if (root.storage.has(fd.name)) {
      typeCheckOrder.add(
          env -> {
            env.document.createError(
                fd,
                String.format("Global field '%s' was already defined", fd.nameToken.text),
                "GlobalDefine");
          });
      return;
    }
    root.storage().add(fd, typeCheckOrder);
  }

  @Override
  public void add(final ImportDocument importDocument) {
    importFile(importDocument.filename, importDocument);
  }

  @Override
  public void add(final IsEnum storage) {
    if (storage instanceof TyType) {
      if (types.containsKey(storage.name())) {
        TyType prior = types.get(storage.name());
        typeCheckOrder.add(
            env -> {
              env.document.createError(
                  (TyType) storage,
                  String.format("The enumeration '%s' was already defined.", storage.name()),
                  "DocumentDefine");
              env.document.createError(
                  prior,
                  String.format("The enumeration '%s' was defined here.", storage.name()),
                  "DocumentDefine");
            });
        return;
      }
      typeCheckOrder.add(
          env -> {
            storage.storage().typing(env);
          });
      typeCheckOrder.add(
          env -> {
            for (final String s : storage.storage().duplicates) {
              env.document.createError(
                  (TyType) storage,
                  String.format(
                      "The enumeration '%s' has duplicates for '%s' defined.", storage.name(), s),
                  "DocumentDefine");
            }
          });
      types.put(storage.name(), (TyType) storage);
    }
  }

  @Override
  public void add(final IsStructure storage) {
    if (storage instanceof TyType) {
      if (types.containsKey(storage.name())) {
        TyType prior = types.get(storage.name());
        typeCheckOrder.add(
            env -> {
              env.document.createError(
                  (TyType) storage,
                  String.format(
                      "The %s '%s' was already defined.",
                      storage instanceof TyNativeMessage ? "message" : "record", storage.name()),
                  "DocumentDefine");
              env.document.createError(
                  prior,
                  String.format(
                      "The %s '%s' was defined here.",
                      prior instanceof TyNativeMessage ? "message" : "record", storage.name()),
                  "DocumentDefine");
            });
      }
      types.put(storage.name(), (TyType) storage);
      typeCheckOrder.add(
          env -> {
            storage.typing(env.scope());
          });
    }
  }

  @Override
  public void add(final Token token) {
    // no-op
  }

  @Override
  public void add(AugmentViewerState avs) {
    viewerType.storage.add(
        new FieldDefinition(null, null, avs.type, avs.name, null, null, null, avs.semicolon));
    typeCheckOrder.add((env) -> avs.typing(env));
  }

  public void add(DefineRPC rpc) {
    TyNativeMessage nativeMessageType = rpc.genTyNativeMessage();
    DefineHandler handler = rpc.genHandler();
    types.put(rpc.genMessageTypeName(), nativeMessageType);
    channelToMessageType.put(rpc.name.text, rpc.genMessageTypeName());
    handlers.add(handler);
    typeCheckOrder.add(
        env -> {
          rpc.typing(env);
          if (env.document.errorLists.size() == 0) {
            nativeMessageType.typing(env);
            handler.typing(env);
          }
        });
  }

  /**
   * @param filename the filename to import
   * @param position the position within the document (can't be null, use DocumentPosition.ZERO for
   *     initial import)
   */
  public void importFile(final String filename, final DocumentPosition position) {
    final var file = search(filename);
    if (!file.exists()) {
      createError(position, String.format("File '%s' was not found", filename), "ImportIssue");
      return;
    }
    try {
      final var tokenEngine =
          new TokenEngine(filename, Files.readString(file.toPath()).codePoints().iterator());
      final var parser = new Parser(tokenEngine);
      parser.document().accept(this);
    } catch (final ScanException e) {
      createError(
          position,
          String.format("File '%s' failed to lex: %s", filename, e.getMessage()),
          "ParseException");
      createError(
          position, String.format("Import failed (Lex): %s", e.getMessage()), "ImportIssue");
    } catch (final ParseException e) {
      createError(
          e.toDocumentPosition(),
          String.format("File '%s' failed to parse: %s", filename, e.getMessage()),
          "ParseException");
      createError(
          position, String.format("Import failed (Parse): %s", e.getMessage()), "ImportIssue");
    } catch (final Exception e) {
      createError(
          position, String.format("File '%s' failed to import due", filename), "ImportIssue");
      createError(position, String.format("Import failed (Unknown)"), "ImportIssue");
    }
  }

  /**
   * search for the given filename in the search paths; consumer must check if file exists or not
   */
  private File search(final String filename) {
    var file = new File(filename);
    final var search = searchPaths.iterator();
    while (!file.exists() && search.hasNext()) {
      file = new File(search.next(), filename);
    }
    return file;
  }

  /** create an error with a reference to a tutorial */
  public DocumentError createError(
      final DocumentPosition position, final String message, final String tutorial) {
    final var err = new DocumentError(position, message, tutorial);
    errorLists.add(err);
    return err;
  }

  public void add(final LatentCodeSnippet snippet) {
    latentCodeSnippets.add(snippet);
  }

  public void add(final String key, final LatentCodeSnippet snippet) {
    dedupedLatentCodeSnippets.put(key, snippet);
  }

  /** add a search path for importing files */
  public void addSearchPath(final File path) {
    searchPaths.add(path);
  }

  /** check the document is valid */
  public boolean check(final EnvironmentState state) {
    final var environment = Environment.fresh(this, state);
    // we wall all functions to give them their ID and then index them
    final var functionIndex = new HashMap<String, ArrayList<DefineFunction>>();
    for (final DefineFunction df : functionDefinitions) {
      df.getFuncId(environment);
      var index = functionIndex.get(df.name);
      if (index == null) {
        index = new ArrayList<>();
        functionIndex.put(df.name, index);
      }
      index.add(df);
    }
    for (final DefineDocumentEvent de : events) {
      if (de.which == DocumentEvent.AskInvention) {
        for (DefineConstructor c : constructors) {
          if (c.messageTypeToken != null) {
            createError(de, "Invention requires all constructors to not accept messages", "INVENT");
          }
        }
      }
    }
    for (final Map.Entry<String, ArrayList<DefineFunction>> entry : functionIndex.entrySet()) {
      final var instances = new ArrayList<FunctionOverloadInstance>();
      for (final DefineFunction df : entry.getValue()) {
        instances.add(df.toFunctionOverloadInstance());
      }
      final var functional =
          new TyNativeFunctional(entry.getKey(), instances, FunctionStyleJava.InjectNameThenArgs);
      typeCheckOrder.add(env -> functional.typing(environment));
      functionTypes.put(entry.getKey(), functional);
    }
    while (typeCheckOrder.size() > 0) {
      final var cloneTypeChecks = new ArrayList<>(typeCheckOrder);
      typeCheckOrder.clear();
      for (final Consumer<Environment> checkInOrder : cloneTypeChecks) {
        checkInOrder.accept(environment);
      }
    }
    TyType constructorMessageType = null;
    for (final DefineConstructor dc : constructors) {
      if (dc.messageTypeToken != null) {
        TyType currentType =
            environment.rules.FindMessageStructure(dc.messageTypeToken.text, dc, false);
        if (currentType != null && currentType instanceof TyNativeMessage) {
          currentType = ((TyNativeMessage) currentType).makeAnonymousCopy();
          if (constructorMessageType != null) {
            constructorMessageType =
                environment.rules.GetMaxType(constructorMessageType, currentType, false);
          } else {
            constructorMessageType = currentType;
          }
        }
      }
    }
    if (constructorMessageType != null) {
      constructorMessageType =
          environment.rules.EnsureRegisteredAndDedupe(constructorMessageType, false);
    }
    for (final DefineConstructor dc : constructors) {
      dc.unifiedMessageType = constructorMessageType;
      dc.typing(environment);
    }
    return !hasErrors();
  }

  /** does the document have errors */
  public boolean hasErrors() {
    return errorLists.size() > 0;
  }

  /** compile the document to java */
  public String compileJava(final EnvironmentState state) {
    var environment = Environment.fresh(this, state);
    if (state.options.disableBillingCost) {
      environment = environment.scopeAsNoCost();
    }
    final var sb = new StringBuilderWithTabs();
    CodeGenDocument.writePrelude(sb, environment);
    sb.append("public class " + className + " extends LivingDocument {").tabUp().writeNewline();
    CodeGenRecords.writeRootDocument(root.storage, sb, environment);
    for (final TyType ty : types.values()) {
      if (ty instanceof DetailTypeProducesRootLevelCode) {
        ((DetailTypeProducesRootLevelCode) ty).compile(sb, environment);
      }
    }
    CodeGenFunctions.writeFunctionsJava(sb, environment);
    CodeGenMessageHandling.writeMessageHandlers(sb, environment);
    CodeGenStateMachine.writeStateMachine(sb, environment);
    CodeGenEventHandlers.writeEventHandlers(sb, environment);
    CodeGenTests.writeTests(sb, environment);
    CodeGenConstructor.writeConstructors(sb, environment);
    // code snippets which are done after everything
    for (final LatentCodeSnippet lcs : latentCodeSnippets) {
      lcs.writeLatentJava(sb);
    }
    for (final LatentCodeSnippet lcs : dedupedLatentCodeSnippets.values()) {
      lcs.writeLatentJava(sb);
    }
    sb.append("/* end of file */").tabDown().writeNewline();
    sb.append("}").writeNewline(); // end file
    return sb.toString();
  }

  public IsStructure findPriorMessage(
      final StructureStorage search, final Environment environment) {
    for (final TyType other : types.values()) {
      if (other instanceof TyNativeMessage) {
        if (((IsStructure) other).storage().match(search, environment)) {
          return (IsStructure) other;
        }
      }
    }
    return null;
  }

  /** get the class name */
  public String getClassName() {
    return className;
  }

  /** set the class name */
  public void setClassName(final String className) {
    this.className = className;
  }

  /** invent a class id */
  public int inventClassId() {
    return autoClassId++;
  }

  /** export errors in LSP format */
  public String errorsJson() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginArray();
    errorLists.forEach(
        err -> {
          writer.injectJson(err.json());
        });
    writer.endArray();
    return writer.toString();
  }
}
