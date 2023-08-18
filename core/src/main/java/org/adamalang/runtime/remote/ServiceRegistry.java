/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.common.keys.PrivateKeyBundle;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/** a service registry maps service names to services */
public class ServiceRegistry {
  public static TreeMap<String, ServiceConstructor> REGISTRY = new TreeMap<>();
  public static ServiceRegistry NOT_READY = new ServiceRegistry() {
    @Override
    public Service find(String name) {
      return Service.NOT_READY;
    }

    @Override
    public void resolve(String space, HashMap<String, HashMap<String, Object>> servicesConfig, TreeMap<Integer, PrivateKeyBundle> keys) {
    }
  };
  private static final TreeMap<String, Class<?>> INCLUDED_SERVICES = new TreeMap<>();
  private final TreeMap<String, Service> services;

  public ServiceRegistry() {
    this.services = new TreeMap<>();
  }

  public static void add(String name, Class<?> clazz, ServiceConstructor cons) {
    INCLUDED_SERVICES.put(name, clazz);
    REGISTRY.put(name, cons);
  }

  public static String getLinkDefinition(String name, int autoId, String params, HashSet<String> names, Consumer<String> error) {
    Class<?> clazz = INCLUDED_SERVICES.get(name);
    if (clazz == null) {
      return null;
    }
    try {
      Method method = clazz.getMethod("definition", int.class, String.class, HashSet.class, Consumer.class);
      return (String) method.invoke(null, autoId, params, names, error);
    } catch (Exception ex) {
      return null;
    }
  }

  /** find a service */
  public Service find(String name) {
    Service local = services.get(name);
    if (local == null) {
      return Service.FAILURE;
    }
    return local;
  }

  public boolean contains(String name) {
    return services.containsKey(name);
  }

  public void resolve(String spaceName, HashMap<String, HashMap<String, Object>> servicesConfig, TreeMap<Integer, PrivateKeyBundle> keys) {
    for (Map.Entry<String, HashMap<String, Object>> entry : servicesConfig.entrySet()) {
      Service resolved = resolveService(spaceName, entry.getValue(), keys);
      if (resolved == null) {
        resolved = Service.FAILURE;
      }
      services.put(entry.getKey(), resolved);
    }
  }

  private Service resolveService(String spaceName, HashMap<String, Object> config, TreeMap<Integer, PrivateKeyBundle> keys) {
    Object clazz = config.get("class");
    try {
      if (clazz != null && clazz instanceof String) {
        ServiceConstructor cons = REGISTRY.get((String) clazz);
        if (cons != null) {
          return cons.cons(spaceName, config, keys);
        }
      }
    } catch (Exception ex) { // ignore it
    }
    return null;
  }
}
