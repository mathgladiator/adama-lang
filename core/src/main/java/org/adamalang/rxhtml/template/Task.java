/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

/** because every framework should have task management built in */
public class Task {
  public final String id;
  public final String section;
  public final String description;

  public Task(String id, String section, String description) {
    this.id = id;
    this.section = section;
    this.description = description;
  }
}
