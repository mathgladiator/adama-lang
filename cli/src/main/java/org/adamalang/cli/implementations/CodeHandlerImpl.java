/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Util;
import org.adamalang.cli.commands.Code;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.CodeHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.lsp.LanguageServer;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CodeHandlerImpl implements CodeHandler {
    @Override
    public void lsp(Arguments.CodeLspArgs args, Output.YesOrError output) throws Exception {
        int port = Integer.parseInt(args.port);
        LanguageServer.singleThread(port);
    }

    public static Code.CompileResult sharedCompileCode(String filename, String code, HashMap<String, String> includes) {
        try {
            final var options = CompilerOptions.start().make();
            final var globals = GlobalObjectPool.createPoolWithStdLib();
            final var state = new EnvironmentState(globals, options);
            final var document = new Document();
            FirstPartyServices.install(new NoOpMetricsFactory(), null, null, null);
            document.setClassName("TempClass");
            final var tokenEngine = new TokenEngine(filename, code.codePoints().iterator());
            final var parser = new Parser(tokenEngine);
            document.setIncludes(includes);
            parser.document().accept(document);
            boolean result = document.check(state);
            if (result) {
                String javaCode = document.compileJava(state);
                JsonStreamWriter reflect = new JsonStreamWriter();
                document.writeTypeReflectionJson(reflect);
                return new Code.CompileResult(javaCode, reflect.toString());
            }
            ArrayNode parsed = (ArrayNode) (new JsonMapper().readTree(document.errorsJson()));
            for (int k = 0; k < parsed.size(); k++) {
                ObjectNode errorItem = (ObjectNode) parsed.get(k);
                int startLine = errorItem.get("range").get("start").get("line").intValue();
                int endLine = errorItem.get("range").get("end").get("line").intValue();
                int startCharacter = errorItem.get("range").get("start").get("character").intValue();
                int endCharacter = errorItem.get("range").get("end").get("character").intValue();
                System.err.println(Util.prefix("[start=" + startLine + ":" + startCharacter + ", end=" + endLine + ":" + endCharacter + "]", Util.ANSI.Red) + " :" + errorItem.get("message").textValue());
            }
            return null;
        } catch (Exception ex) {
            System.err.println(Util.prefix("Parsing exception:" + ex.getMessage(), Util.ANSI.Red));
            return null;
        }
    }

    public static boolean sharedValidatePlan(File file) {
        ObjectNode node;
        try {
            node = Json.parseJsonObject(Files.readString(file.toPath()));
        } catch (Exception ex) {
            System.err.println("Plan is not json; stopping");
            return false;
        }
        JsonNode versionsNode = node.get("versions");
        JsonNode defaultNode = node.get("default");
        JsonNode planNode = node.get("plan");

        boolean success = true;
        if (versionsNode == null || !versionsNode.isObject()) {
            System.err.println("'versions' node in root plan json is not an object or doesn't exist");
            success = false;
        } else {
            Iterator<Map.Entry<String, JsonNode>> it = ((ObjectNode) versionsNode).fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (entry.getValue() == null) {
                    System.err.println("version '"+entry.getKey()+"' didn't exist or wasn't text");
                    success = false;
                } else if(entry.getValue().isTextual()) {
                    if (sharedCompileCode(entry.getKey(), entry.getValue().textValue(), new HashMap<>()) == null) {
                        System.err.println("version '"+entry.getValue()+"' failed to validate");
                        success = false;
                    }
                } else if(entry.getValue().isObject()) {
                    ObjectNode bundle = (ObjectNode) entry.getValue();
                    JsonNode main = bundle.get("main");
                    if (main == null || !main.isTextual()) {
                        System.err.println("bundle '"+bundle.toPrettyString() + "' doesn't have a main or if it wasn't text");
                        success = false;
                    } else {
                        JsonNode includesNode = bundle.get("includes");
                        HashMap<String, String> includes = new HashMap<>();
                        if (includesNode == null || !includesNode.isObject()) {
                            System.err.println("bundle '"+bundle.toPrettyString() + "' doesn't have an includes that is an object");
                            success = false;
                        } else {
                            Iterator<Map.Entry<String, JsonNode>> inf = includesNode.fields();
                            while (inf.hasNext()) {
                                Map.Entry<String, JsonNode> infe = inf.next();
                                if (infe.getValue() == null | !infe.getValue().isTextual()) {
                                    System.err.println("bundle '"+ infe.getKey() + "' was either null or not text when it should be text");
                                    success = false;
                                } else {
                                    includes.put(infe.getKey(), infe.getValue().textValue());
                                }
                            }
                        }
                        if (sharedCompileCode(entry.getKey(), main.textValue(), includes) == null) {
                            System.err.println("version '" + entry.getKey() + "' failed to validate");
                            success = false;
                        }
                    }
                }
            }
        }

        if (defaultNode == null || !defaultNode.isTextual()) {
            System.err.println("'default' node in root plan json is not text or doesn't exist");
            success = false;
        } else {
            JsonNode planVersion = versionsNode.get(defaultNode.textValue());
            if (planVersion == null || planVersion.isNull()) {
                System.err.println("default version of '" + defaultNode.textValue() + "' pointed to a version which was not found");
                success = false;
            }
        }

        if (planNode == null || !planNode.isArray()) {
            System.err.println("'plan' node in root plan json is not an array (or doesn't exist)");
            success = false;
        } else {
            for (int k = 0; k < planNode.size(); k++) {
                if (!planNode.get(k).isTextual()) {
                    System.err.println("'plan' element " + k + " is not text");
                    success = false;
                } else {
                    JsonNode planVersion = versionsNode.get(planNode.get(k).textValue());
                    if (planVersion == null || planVersion.isNull()) {
                        System.err.println("'plan' element " + k + " pointed to a version which was not found");
                        success = false;
                    }
                }
            }
        }
        return success;
    }
    @Override
    public void validatePlan(Arguments.CodeValidatePlanArgs args, Output.YesOrError output) throws Exception {
        File planFile = new File(args.plan);
        if (!planFile.exists()) {
            System.err.println(Util.prefix("Plan file does not exist; stopping", Util.ANSI.Red));
            return;
        }
        if (planFile.isDirectory()) {
            System.err.println(Util.prefix("Plan file is a directory; stopping", Util.ANSI.Red));
            return;
        }
        if (sharedValidatePlan(planFile)) {
            System.out.println(Util.prefix("Validated!", Util.ANSI.Green));
        } else {
            System.err.println(Util.prefix("Plan file failed to validate", Util.ANSI.Red));
        }
    }

    private static HashMap<String, String> getImports(String imports) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        File fileImports = new File(imports);
        if (fileImports.exists() && fileImports.isDirectory()) {
            for (File f : fileImports.listFiles((dir, name) -> name.endsWith(".adama"))) {
                String name = f.getName().substring(0, f.getName().length() - 6);
                map.put(name, Files.readString(f.toPath()));
            }
        }
        return map;
    }
    @Override
    public void bundlePlan(Arguments.CodeBundlePlanArgs args, Output.YesOrError output) throws Exception {
        ObjectNode plan = Json.newJsonObject();
        ObjectNode version = plan.putObject("versions").putObject("file");
        version.put("main", Files.readString(new File(args.main).toPath()));
        ObjectNode includes = version.putObject("includes");
        for (Map.Entry<String, String> entry : getImports(args.imports).entrySet()) {
            includes.put(entry.getKey(), entry.getValue());
        }
        plan.put("default", "file");
        plan.putArray("plan");
        Files.writeString(new File(args.output).toPath(), plan.toPrettyString());
    }

    @Override
    public void compileFile(Arguments.CodeCompileFileArgs args, Output.YesOrError output) throws Exception {
        String dumpTo = args.dumpTo;
        //TODO
//        Code.CompileResult result = sharedSetup(config, args);
//        if (result != null) {
//            System.out.println(Util.prefix("Compiled!", Util.ANSI.Green));
//            if (dumpTo != null) {
//                System.out.println("Dumped: " + dumpTo);
//                Files.writeString(new File(dumpTo).toPath(), result.code);
//            }
//            return true;
//        } else {
//            System.err.println(Util.prefix("File failed to compile", Util.ANSI.Red));
//            return false;
//        }
    }

    @Override
    public void reflectDump(Arguments.CodeReflectDumpArgs args, Output.YesOrError output) throws Exception {
        //TODO
        String dumpTo = args.dumpTo;
//        Code.CompileResult result = sharedSetup(config, args);
//        if (result != null) {
//            System.out.println(Util.prefix("Compiled!", Util.ANSI.Green));
//            if (dumpTo != null) {
//                System.out.println("Dumped: " + dumpTo);
//                Files.writeString(new File(dumpTo).toPath(), result.reflection);
//            }
//        } else {
//            System.err.println(Util.prefix("File failed to compile", Util.ANSI.Red));
//        }
    }
}
