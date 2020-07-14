/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

/** generic way of getting a value and setting a value within a class */
public interface CanGetAndSet<T> {
  public T get();
  public void set(T value);
}
