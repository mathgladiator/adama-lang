/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.frontend;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

import java.util.TreeMap;

public class SpaceTemplates {
  public static class SpaceTemplate {
    public final String adama;
    public final String rxhtml;

    public SpaceTemplate(String adama, String rxhtml) {
      this.adama = adama;
      this.rxhtml = rxhtml;
    }

    public String idearg() {
      ObjectNode node = Json.newJsonObject();
      node.put("adama", adama);
      node.put("rxhtml", rxhtml);
      return node.toString();
    }

    public ObjectNode plan() {
      ObjectNode plan = Json.newJsonObject();
      plan.put("default", "main");
      plan.putArray("plan");
      ObjectNode main = plan.putObject("versions").putObject("main");
      main.put("main", adama);
      main.put("rxhtml", rxhtml);
      return plan;
    }
  }

  private final TreeMap<String, SpaceTemplate> templates;

  private SpaceTemplates() {
    this.templates = new TreeMap<>();
    this.templates.put("none", new SpaceTemplate("@construct { }\n", "<forest>\n  <page uri=\"/\">Hello World</page></forest>"));
  }

  public SpaceTemplate of(String name) {
    SpaceTemplate bundle = templates.get(name == null ? "none" : name);
    if (bundle != null) {
      return bundle;
    }
    return templates.get("none");
  }

  public static final SpaceTemplates REGISTRY = new SpaceTemplates();
}
