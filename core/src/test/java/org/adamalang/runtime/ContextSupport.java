/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime;

import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreRequestContext;

public class ContextSupport {
  public static CoreRequestContext WRAP(NtClient who) {
    return new CoreRequestContext(who, "test", "123.0.0.1", "key");
  }
}
