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
