/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

/** simple contract to define how to log requests; this is for monitoring both first and third party services */
public interface RequestLogger<In, Out> {
  public Callback<Out> wrap(In request, Callback<Out> callback);
}
