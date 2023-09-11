/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.frontend.Session;

public interface DocumentStreamHandler {
  public void bind();

  public void handle(ConnectionSendRequest request, SeqResponder responder);

  public void handle(ConnectionPasswordRequest request, SimpleResponder responder);

  public void handle(ConnectionSendOnceRequest request, SeqResponder responder);

  public void handle(ConnectionCanAttachRequest request, YesResponder responder);

  public void handle(ConnectionAttachRequest request, SeqResponder responder);

  public void handle(ConnectionUpdateRequest request, SimpleResponder responder);

  public void handle(ConnectionEndRequest request, SimpleResponder responder);

  public void logInto(ObjectNode node);

  public void disconnect(long id);

}
