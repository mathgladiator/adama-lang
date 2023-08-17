/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.ops.capacity;

import org.adamalang.common.Callback;

import java.util.List;

/** an interface for managing capacity */
public interface CapacityOverseer {

  /** list all the capacity for a space in the world */
  public void listAllSpace(String space, Callback<List<CapacityInstance>> callback);

  /** list all the capacity for a space within a region */
  public void listWithinRegion(String space, String region, Callback<List<CapacityInstance>> callback);

  /** list all the spaces bound to a machine within a region */
  public void listAllOnMachine(String region, String machine, Callback<List<CapacityInstance>> callback);

  /** add capacity for a space to a host within a region */
  public void add(String space, String region, String machine, Callback<Void> callback);

  /** remove capacity for a space from a host and region */
  public void remove(String space, String region, String machine, Callback<Void> callback);

  /** nuke all capacity for a space */
  public void nuke(String space, Callback<Void> callback);

  /** pick a new host for a space within a region (that is stable) */
  public void pickHostForSpace(String space, String region, Callback<String> callback);
}
