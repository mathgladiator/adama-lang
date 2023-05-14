/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.aws;

import org.adamalang.aws.Credential;
import org.adamalang.common.ConfigObject;

/** configuration for using AWS from the SaaS  */
public class AWSConfig {
  public final Credential credential;
  public final String region;
  public final String fromEmailAddressForInit;
  public final String replyToEmailAddressForInit;
  public final String bucket;
  public final String archivePath;
  public final String queue;

  public AWSConfig(ConfigObject config) throws Exception {
    this.credential = new Credential(config.strOfButCrash("access-key", "AWS Access Key not found"), config.strOfButCrash("secret-key", "AWS Secret Key not found"));
    this.region = config.strOfButCrash("region", "AWS Region");
    this.fromEmailAddressForInit = config.strOfButCrash("init-from-email", "No sender email address set for init");
    this.replyToEmailAddressForInit = config.strOfButCrash("init-reply-email", "No reply email address set for init");
    this.bucket = config.strOfButCrash("bucket", "No bucket for assets");
    this.archivePath = config.strOfButCrash("archive", "No archive path for backups/restore");
    this.queue = config.strOfButCrash("queue", "No queue for control hand-off");
  }
}
