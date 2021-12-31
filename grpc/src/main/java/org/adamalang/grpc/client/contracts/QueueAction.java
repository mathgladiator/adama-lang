package org.adamalang.grpc.client.contracts;

/** an unit of work that sits within a queue for processing */
public abstract class QueueAction<T> {
  private boolean alive;

  public QueueAction() {
    this.alive = true;
  }

  /** is the queue action alive */
  public boolean isAlive() {
    return alive;
  }

  protected abstract void executeNow(T item);

  /** execute the item if it is still valid */
  public void execute(T item) {
    if (alive) {
      executeNow(item);
    }
  }

  /** the queue action did not finish in time */
  protected abstract void timeOut();

  /** the queue action was rejected */
  protected abstract void rejected();

  /** how the timeout is executed */
  public void killDueToTimeout() {
    alive = false;
    timeOut();
  }

  public void killDueToReject() {
    alive = false;
    rejected();
  }
}
