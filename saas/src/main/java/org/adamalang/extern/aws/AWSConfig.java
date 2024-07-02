/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
  public final String userDataBucket;
  public final String archivePath;
  public final String queue;
  public final String logBucket;
  public final String backupBucket;

  public AWSConfig(ConfigObject config) throws Exception {
    this.credential = new Credential(config.strOfButCrash("access-key", "AWS Access Key not found"), config.strOfButCrash("secret-key", "AWS Secret Key not found"));
    this.region = config.strOfButCrash("region", "AWS Region");
    this.fromEmailAddressForInit = config.strOfButCrash("init-from-email", "No sender email address set for init");
    this.replyToEmailAddressForInit = config.strOfButCrash("init-reply-email", "No reply email address set for init");
    this.userDataBucket = config.strOfButCrash("bucket", "No bucket for assets");
    this.logBucket = config.strOfButCrash("log-bucket", "No bucket for logs");
    this.backupBucket = config.strOfButCrash("backup-bucket", "No bucket for backups");
    this.archivePath = config.strOfButCrash("archive", "No archive path for backups/restore");
    this.queue = config.strOfButCrash("queue", "No queue for control hand-off");
  }
}
