/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

/** a space that has been deleted via an owner change to 0 */
public class DeletedSpace {
  public final int id;
  public final String name;

  public DeletedSpace(int id, String name) {
    this.id = id;
    this.name = name;
  }
}
