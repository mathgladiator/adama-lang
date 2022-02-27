package org.adamalang.disk;

@FunctionalInterface
public interface BadConsumer<T> {
  public void accept(T output) throws Exception;
}
