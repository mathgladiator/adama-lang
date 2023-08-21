/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services.region;

import org.adamalang.cli.Config;
import org.adamalang.cli.services.Role;
import org.adamalang.cli.services.common.CloudBoot;
import org.adamalang.cli.services.common.EveryMachine;
import org.adamalang.region.RegionInit;

public class RegionFrontend {
  public static void run(Config config) throws Exception {
    EveryMachine em = new EveryMachine(config, Role.Web);
    CloudBoot cb = new CloudBoot(em.alive, em.metricsFactory, em.webBase, config.get_or_create_child("aws"), em.logsPrefix, em.system);
    // TODO: REGION-SYNC HERE
    // public static RegionInit init(SelfClient client, String identity, String region, String machine, String role, String publicKey) throws Exception {
    // RegionInit ri = RegionInit.init(__client, regionalIdentity, em.region, em.machine, "web", em.publicKey);
  }
}
