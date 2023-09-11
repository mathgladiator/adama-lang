/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.contracts.data;

import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class SpacePolicyTests {

  private static SpacePolicy of(String policy) {
    TreeSet<Integer> developers = new TreeSet<>();
    developers.add(4);
    SpaceInfo info = new SpaceInfo(1, 2, developers, true, 42, policy);
    return new SpacePolicy(info);
  }
  public static AuthenticatedUser simple(int id) {
    return new AuthenticatedUser(id, new NtPrincipal(id + "", "adama"), null);
  }

  public static AuthenticatedUser simple(String agent, String authority) {
    return new AuthenticatedUser(-1, new NtPrincipal(agent, authority), null);
  }

  @Test
  public void defaults() {
    SpacePolicy policy = of("{}");
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(2)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(4)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(2)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(4)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(3)));
  }

  @Test
  public void defaults_x() {
    SpacePolicy policy = of("{\"x\":{}}");
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(2)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(4)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(2)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(4)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(3)));
  }

  @Test
  public void defaults_bs() {
    SpacePolicy policy = of("{\"x\":null}");
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(2)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(4)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(2)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(4)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(3)));
  }

  @Test
  public void defaults_null_authorities() {
    SpacePolicy policy = of("{\"allowed-authorities\":null}");
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(2)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(4)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(2)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(4)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(3)));
  }

  @Test
  public void defaults_bad_authorities() {
    SpacePolicy policy = of("{\"allowed-authorities\":{}}}");
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(2)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(4)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(2)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(4)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(3)));
  }

  @Test
  public void developers_on_owner_default() {
    SpacePolicy policy = of("{\"x\":{\"developers\":true}}");
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(2)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(4)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(2)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(4)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(3)));
  }

  @Test
  public void provide_authority() {
    SpacePolicy policy = of("{\"x\":{\"allowed-authorities\":[1, \"cake\", false, null]}}");
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(2)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.Owner, simple(4)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(2)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(4)));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple(3)));
    Assert.assertTrue(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple("x", "cake")));
    Assert.assertFalse(policy.checkPolicy("x", DefaultPolicyBehavior.OwnerAndDevelopers, simple("x", "ninja")));
  }
}
