package org.adamalang.bald.contracts;

/** for reading a list of byte[] */
public interface ByteArrayStream {

  // a new append was discovered
  void next(int appendIndex, byte[] value);

  // no more appends were found
  void finished();

  // a failure occurred
  void failure(Exception ex);
}
