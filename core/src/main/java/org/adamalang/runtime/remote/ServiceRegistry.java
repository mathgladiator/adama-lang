/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.remote;

import java.util.TreeMap;

/** a service registry maps service names to services */
public class ServiceRegistry {
  private final ServiceRegistry parent;
  private final TreeMap<String, Service> services;

  /** we scope the registry such that there are
   * (1) global services that are internal,
   * (2) user defined services per space
   */
  public ServiceRegistry(ServiceRegistry parent) {
    this.parent = parent;
    this.services = new TreeMap<>();
  }

  /** add the service to this registry */
  public void add(String name, Service service) {
    services.put(name, service);
  }

  /** find a service */
  public Service find(String name) {
    Service local = services.get(name);
    if (local == null && parent != null) {
      return parent.find(name);
    }
    return local;
  }
}
