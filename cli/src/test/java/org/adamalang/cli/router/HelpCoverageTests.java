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
package org.adamalang.cli.router;

import org.adamalang.cli.Main;
import org.junit.Test;

public class HelpCoverageTests {
  @Test
  public void coverage() {
    Main.testMain(new String[]{"space", "--help"});
    Main.testMain(new String[]{"space", "create", "--help"});
    Main.testMain(new String[]{"space", "delete", "--help"});
    Main.testMain(new String[]{"space", "deploy", "--help"});
    Main.testMain(new String[]{"space", "developers", "--help"});
    Main.testMain(new String[]{"space", "encrypt-priv", "--help"});
    Main.testMain(new String[]{"space", "encrypt-secret", "--help"});
    Main.testMain(new String[]{"space", "generate-key", "--help"});
    Main.testMain(new String[]{"space", "generate-policy", "--help"});
    Main.testMain(new String[]{"space", "get", "--help"});
    Main.testMain(new String[]{"space", "get-policy", "--help"});
    Main.testMain(new String[]{"space", "get-rxhtml", "--help"});
    Main.testMain(new String[]{"space", "list", "--help"});
    Main.testMain(new String[]{"space", "metrics", "--help"});
    Main.testMain(new String[]{"space", "reflect", "--help"});
    Main.testMain(new String[]{"space", "set-policy", "--help"});
    Main.testMain(new String[]{"space", "set-role", "--help"});
    Main.testMain(new String[]{"space", "set-rxhtml", "--help"});
    Main.testMain(new String[]{"space", "upload", "--help"});
    Main.testMain(new String[]{"authority", "--help"});
    Main.testMain(new String[]{"authority", "append-local", "--help"});
    Main.testMain(new String[]{"authority", "create", "--help"});
    Main.testMain(new String[]{"authority", "create-local", "--help"});
    Main.testMain(new String[]{"authority", "destroy", "--help"});
    Main.testMain(new String[]{"authority", "get", "--help"});
    Main.testMain(new String[]{"authority", "list", "--help"});
    Main.testMain(new String[]{"authority", "set", "--help"});
    Main.testMain(new String[]{"authority", "sign", "--help"});
    Main.testMain(new String[]{"account", "--help"});
    Main.testMain(new String[]{"account", "set-password", "--help"});
    Main.testMain(new String[]{"code", "--help"});
    Main.testMain(new String[]{"code", "benchmark-message", "--help"});
    Main.testMain(new String[]{"code", "bundle-plan", "--help"});
    Main.testMain(new String[]{"code", "compile-file", "--help"});
    Main.testMain(new String[]{"code", "diagram", "--help"});
    Main.testMain(new String[]{"code", "format", "--help"});
    Main.testMain(new String[]{"code", "lsp", "--help"});
    Main.testMain(new String[]{"code", "reflect-dump", "--help"});
    Main.testMain(new String[]{"code", "validate-plan", "--help"});
    Main.testMain(new String[]{"contrib", "--help"});
    Main.testMain(new String[]{"contrib", "bundle-js", "--help"});
    Main.testMain(new String[]{"contrib", "copyright", "--help"});
    Main.testMain(new String[]{"contrib", "make-api", "--help"});
    Main.testMain(new String[]{"contrib", "make-book", "--help"});
    Main.testMain(new String[]{"contrib", "make-cli", "--help"});
    Main.testMain(new String[]{"contrib", "make-codec", "--help"});
    Main.testMain(new String[]{"contrib", "make-embed", "--help"});
    Main.testMain(new String[]{"contrib", "make-et", "--help"});
    Main.testMain(new String[]{"contrib", "str-temp", "--help"});
    Main.testMain(new String[]{"contrib", "tests-adama", "--help"});
    Main.testMain(new String[]{"contrib", "tests-rxhtml", "--help"});
    Main.testMain(new String[]{"contrib", "version", "--help"});
    Main.testMain(new String[]{"database", "--help"});
    Main.testMain(new String[]{"database", "configure", "--help"});
    Main.testMain(new String[]{"database", "install", "--help"});
    Main.testMain(new String[]{"database", "make-reserved", "--help"});
    Main.testMain(new String[]{"database", "migrate", "--help"});
    Main.testMain(new String[]{"document", "--help"});
    Main.testMain(new String[]{"document", "attach", "--help"});
    Main.testMain(new String[]{"document", "connect", "--help"});
    Main.testMain(new String[]{"document", "create", "--help"});
    Main.testMain(new String[]{"document", "delete", "--help"});
    Main.testMain(new String[]{"document", "download-archive", "--help"});
    Main.testMain(new String[]{"document", "download-backup", "--help"});
    Main.testMain(new String[]{"document", "list", "--help"});
    Main.testMain(new String[]{"document", "list-backups", "--help"});
    Main.testMain(new String[]{"document", "list-push-tokens", "--help"});
    Main.testMain(new String[]{"ops", "--help"});
    Main.testMain(new String[]{"ops", "compact", "--help"});
    Main.testMain(new String[]{"ops", "explain", "--help"});
    Main.testMain(new String[]{"ops", "forensics", "--help"});
    Main.testMain(new String[]{"ops", "summarize", "--help"});
    Main.testMain(new String[]{"ops", "test-firebase-push", "--help"});
    Main.testMain(new String[]{"domain", "--help"});
    Main.testMain(new String[]{"domain", "configure", "--help"});
    Main.testMain(new String[]{"domain", "list", "--help"});
    Main.testMain(new String[]{"domain", "map", "--help"});
    Main.testMain(new String[]{"domain", "unmap", "--help"});
    Main.testMain(new String[]{"frontend", "--help"});
    Main.testMain(new String[]{"frontend", "bundle", "--help"});
    Main.testMain(new String[]{"frontend", "decrypt-product-config", "--help"});
    Main.testMain(new String[]{"frontend", "dev-server", "--help"});
    Main.testMain(new String[]{"frontend", "enable-encryption", "--help"});
    Main.testMain(new String[]{"frontend", "encrypt-product-config", "--help"});
    Main.testMain(new String[]{"frontend", "make-200", "--help"});
    Main.testMain(new String[]{"frontend", "measure", "--help"});
    Main.testMain(new String[]{"frontend", "mobile-capacitor", "--help"});
    Main.testMain(new String[]{"frontend", "push-generate", "--help"});
    Main.testMain(new String[]{"frontend", "rxhtml", "--help"});
    Main.testMain(new String[]{"frontend", "set-libadama", "--help"});
    Main.testMain(new String[]{"frontend", "tailwind-kick", "--help"});
    Main.testMain(new String[]{"frontend", "validate", "--help"});
    Main.testMain(new String[]{"frontend", "wrap-css", "--help"});
    Main.testMain(new String[]{"services", "--help"});
    Main.testMain(new String[]{"services", "auto", "--help"});
    Main.testMain(new String[]{"services", "backend", "--help"});
    Main.testMain(new String[]{"services", "dashboards", "--help"});
    Main.testMain(new String[]{"services", "frontend", "--help"});
    Main.testMain(new String[]{"services", "overlord", "--help"});
    Main.testMain(new String[]{"services", "prepare", "--help"});
    Main.testMain(new String[]{"services", "probe", "--help"});
    Main.testMain(new String[]{"services", "solo", "--help"});
    Main.testMain(new String[]{"canary", "--help"});
    Main.testMain(new String[]{"deinit", "--help"});
    Main.testMain(new String[]{"devbox", "--help"});
    Main.testMain(new String[]{"dumpenv", "--help"});
    Main.testMain(new String[]{"init", "--help"});
    Main.testMain(new String[]{"kickstart", "--help"});
    Main.testMain(new String[]{"version", "--help"});
  }
}
