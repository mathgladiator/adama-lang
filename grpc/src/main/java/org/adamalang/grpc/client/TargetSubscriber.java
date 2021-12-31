package org.adamalang.grpc.client;

import java.util.Collection;
import java.util.TreeSet;
import java.util.function.Consumer;

/** has the job of getting targets from some fleet management system which yields the entire set of targets to connect to */
public class TargetSubscriber implements Consumer<Collection<String>> {
  private final TreeSet<String> targets;

  public TargetSubscriber() {
    this.targets = new TreeSet<>();
  }

  @Override
  public void accept(Collection<String> targets) {
    this.targets.clear();
    this.targets.addAll(targets);
    //

  }
}
