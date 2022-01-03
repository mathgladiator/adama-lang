package org.adamalang.common;

/** for complex executor bouncing, this helps understand what is going on */
public abstract class NamedRunnable implements Runnable {
  public final String name;

  public NamedRunnable(String first, String... tail) {
    StringBuilder sb = new StringBuilder();
    sb.append(first);
    for (String fragment : tail) {
      sb.append("/");
      sb.append(fragment);
    }
    this.name = sb.toString();
  }

  public abstract void execute() throws Exception;

  @Override
  public void run() {
    try {
      execute();
    } catch (Exception ex) {
      // TODO: LOG THIS SHIT
      ex.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return name;
  }
}
