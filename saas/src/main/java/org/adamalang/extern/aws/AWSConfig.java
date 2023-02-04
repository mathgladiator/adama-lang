/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.aws;

import org.adamalang.common.ConfigObject;

public class AWSConfig {
  public final String accessKeyId;
  public final String secretKey;
  public final String fromEmailAddressForInit;
  public final String replyToEmailAddressForInit;
  public final String region;
  public final String bucket;
  public final String archivePath;
  public final String queue;

  public AWSConfig(ConfigObject config) throws Exception {
    this.accessKeyId = config.strOfButCrash("access-key", "AWS Access Key not found");
    this.secretKey = config.strOfButCrash("secret-key", "AWS Secret Key not found");
    this.region = config.strOfButCrash("region", "AWS Region");
    this.fromEmailAddressForInit = config.strOfButCrash("init-from-email", "No sender email address set for init");
    this.replyToEmailAddressForInit = config.strOfButCrash("init-reply-email", "No reply email address set for init");
    this.bucket = config.strOfButCrash("bucket", "No bucket for assets");
    this.archivePath = config.strOfButCrash("archive", "No archive path for backups/restore");
    this.queue = config.strOfButCrash("queue", "No queue for control hand-off");
  }
}
