/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.edhtml.phases.generate;

/** parse special attributes like "annotation-present:large". We use this style rather than annotation-present="large" so we can have multiple of them */
public class AttributePair {
  public final String key;
  public final String value;

  public AttributePair(String attrKey) {
    int kColon = attrKey.indexOf(':');
    if (kColon > 0) {
      this.key = attrKey.substring(0, kColon);
      this.value = attrKey.substring(kColon + 1);
    } else {
      this.key = attrKey;
      this.value = null;
    }
  }
}
