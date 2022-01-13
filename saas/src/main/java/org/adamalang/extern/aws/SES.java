/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern.aws;

import org.adamalang.extern.Email;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public class SES implements Email {
  private final AWSConfig config;
  private final SesClient client;

  public SES(AWSConfig config) {
    this.config = config;
    this.client = SesClient.builder().credentialsProvider(config).region(Region.of(config.region)).build();
  }

  @Override
  public void sendCode(String email, String code) {
    SendEmailRequest.Builder builder = SendEmailRequest.builder();
    builder.replyToAddresses(config.replyToEmailAddressForInit);
    builder.destination(Destination.builder().toAddresses(email).build());

    Content subject = Content.builder().data("Your Super Secret Code for Adama Platform").charset("UTF-8").build();
    Content text = Content.builder().data("Your code is " + code).charset("UTF-8").build();
    Body body = Body.builder().text(text).build();
    builder.message(Message.builder().subject(subject).body(body).build());
    builder.returnPath(config.fromEmailAddressForInit);
    client.sendEmail(builder.build());
  }
}
