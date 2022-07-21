package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;

/** interface for a signel Adama stream/connection to a single document */
public interface AdamaStream {

  /** update the viewer state */
  void update(String newViewerState);

  /** send the document a message on the given channel */
  void send(String channel, String marker, String message, Callback<Integer> callback);

  /** can attachments be made with the owner of the connection */
  void canAttach(Callback<Boolean> callback);

  /** attach an asset to the stream */
  void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback);

  /** close the stream */
  void close();
}