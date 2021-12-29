/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.model.*;

import java.util.Collections;

public class SES implements Email {
  // TODO: need to invest in how Amazon does credential management
  private final AmazonSimpleEmailServiceAsync client;

  public SES(AmazonSimpleEmailServiceAsync client) {
    this.client = client;
  }

  @Override
  public void sendCode(String email, String code) {
    SendEmailRequest request = new SendEmailRequest();
    request.setReplyToAddresses(Collections.singleton("nope@jeffrey.io"));
    request.setDestination(new Destination().withToAddresses(email));
    request.setMessage(
        new Message()
            .withSubject(
                new Content()
                    .withData("Your Super Secret Code for Adama Platform")
                    .withCharset("UTF-8"))
            .withBody(
                new Body()
                    .withText(
                        new Content().withData("Your code is " + code).withCharset("UTF-8"))));
    request.setSource("boss@jeffrey.io");
    client.sendEmail(request);
  }
}
