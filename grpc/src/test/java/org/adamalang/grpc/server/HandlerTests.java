/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.server;

import org.junit.Assert;
import org.junit.Test;

public class HandlerTests {
  @Test
  public void entropy() {
    Assert.assertEquals("120", Handler.fixEntropy("x"));
    Assert.assertNull(Handler.fixEntropy(""));
  }

  @Test
  public void slow_service_handler_disconnected() {
    // TODO: Handler 197
  }

  @Test
  public void handler_service_cant_attach_failure_fault() {
    // TODO: Handler 297
  }

  @Test
  public void handler_service_cant_attach_failure_too_many_queued() {
    // TODO: Handler 311
  }


  @Test
  public void handler_service_attach_failure_fault() {
    // TODO: Handler 368
  }

  @Test
  public void handler_service_attach_failure_too_many_queued() {
    // TODO: Handler 386
  }


  @Test
  public void handler_service_send_failure_fault() {
    // TODO: Handler 440
  }

  @Test
  public void handler_service_send_failure_too_many_queued() {
    // TODO: Handler 460
  }

  @Test
  public void handler_billing_exchange() {
    // TODO: Handler 550 + 560
  }

  @Test
  public void send_complete_echo() {
    // TODO: Handler 593
  }

  @Test
  public void disconnect_before_ready() {
    // TODO: Handler 644
  }

  @Test
  public void do_stuff_while_dead() {
    // TODO: Handler 644
  }
}
