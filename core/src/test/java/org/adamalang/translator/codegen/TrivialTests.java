/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.codegen;

import org.junit.Test;

public class TrivialTests {
  @Test
  public void coverage() {
    new CodeGenConfig();
    new CodeGenConstructor();
    new CodeGenDeltaClass();
    new CodeGenDocument();
    new CodeGenEnums();
    new CodeGenEventHandlers();
    new CodeGenFunctions();
    new CodeGenIngestion();
    new CodeGenMessage();
    new CodeGenMessageHandling();
    new CodeGenRecords();
    new CodeGenStateMachine();
    new CodeGenTests();
  }
}
