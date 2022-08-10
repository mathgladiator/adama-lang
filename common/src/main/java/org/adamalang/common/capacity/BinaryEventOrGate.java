package org.adamalang.common.capacity;

/** Two events are deduped into one */
public abstract class BinaryEventOrGate {
  private boolean a;
  private boolean b;
  private boolean result;
  public BinaryEventOrGate() {
    this.a = false;
    this.b = false;
    this.result = false;
  }

  public void a(boolean value) {
    this.a = value;
    update();
  }

  public void b(boolean value) {
    this.b = value;
    update();
  }

  private void update() {
    boolean next = a || b;
    if (result != next) {
      this.result = next;
      if (this.result) {
        start();
      } else {
        stop();
      }
    }
  }

  public abstract void start();
  public abstract void stop();
}
