/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern.prometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import org.adamalang.common.metrics.*;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PrometheusDashboard implements MetricsFactory {

  private String filename;
  private StringBuilder current;
  private HashMap<String, String> files;
  private StringBuilder nav;
  private int id;

  public PrometheusDashboard() {
    this.filename = null;
    this.current = null;
    this.files = new HashMap<>();
    this.nav = new StringBuilder();
    this.id = 0;
  }

  String makeId() {
    return "ID" + (id++);
  }


  @Override
  public void page(String name, String title) {
    if (filename != null) {
      cut();
    }
    nav.append(" [<a href=\"adama-").append(name).append(".html").append("\">").append(title).append("</a>] ");
    this.filename = "adama-" + name + ".html";
    this.current = new StringBuilder();
    current.append("{{ template \"head\" . }}\n");
    current.append("{{ template \"prom_right_table_head\" }}");
    current.append("##NAV##\n");
    current.append("{{ template \"prom_right_table_tail\" }}");
    current.append("{{ template \"prom_content_head\" . }}\n");
    current.append("<h1>").append(title).append("</h1>\n");
  }

  public void finish(File consolesDirectory) throws Exception {
    if (filename != null) {
      cut();
    }
    for (Map.Entry<String, String> entry : files.entrySet()) {
      File toWrite = new File(consolesDirectory, entry.getKey());
      String data = entry.getValue();
      data = data.replaceAll(Pattern.quote("##NAV##"), nav.toString());
      Files.writeString(toWrite.toPath(), data);
    }
  }

  @Override
  public void section(String title) {
    current.append("<h3>").append(title).append("</h3>\n");
  }

  public void cut() {
    current.append("{{ template \"prom_content_tail\" . }}\n");
    current.append("{{ template \"tail\" . }}\n");
    files.put(filename, current.toString());
  }

  @Override
  public RequestResponseMonitor makeRequestResponseMonitor(String nameRaw) {
    String name = PrometheusMetricsFactory.correctName(nameRaw);
    {
      String graphId = makeId();
      current.append("<b> Failure Rate:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(rr_").append(name).append("_failure_total[1m])/rate(rr_").append(name).append("_start_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b> Successes:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(rr_").append(name).append("_success_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b>Inflight: ").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rr_").append(name).append("_inflight\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    return null;
  }

  @Override
  public StreamMonitor makeStreamMonitor(String nameRaw) {
    String name = PrometheusMetricsFactory.correctName(nameRaw);
    {
      String graphId = makeId();
      current.append("<b> Failure Rate:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(stream_").append(name).append("_failure_total[1m])/rate(stream_").append(name).append("_start_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b> Progress:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(stream_").append(name).append("_progress_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b> Finishes:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(stream_").append(name).append("_finish_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b>Inflight: ").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"stream_").append(name).append("_inflight\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    return null;
  }

  @Override
  public CallbackMonitor makeCallbackMonitor(String name) {
    {
      String graphId = makeId();
      current.append("<b> Failure Rate:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(cb_").append(name).append("_failure_total[1m])/rate(cb_").append(name).append("_start_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b> Successes:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(cb_").append(name).append("_success_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b>Inflight: ").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"cb_").append(name).append("_inflight\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    return null;
  }

  @Override
  public Runnable counter(String name) {
    String graphId = makeId();
    current.append("<b>").append(name).append("</b>\n");
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"rate(raw_").append(name).append("_total[1m])\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yTitle: '").append(name).append("'\n");
    current.append("});\n");
    current.append("</script>");
     return null;
  }

  @Override
  public Inflight inflight(String name) {
    String graphId = makeId();
    current.append("<b>").append(name).append("</b>\n");
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"inf_").append(name).append("\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yTitle: '").append(name).append("'\n");
    current.append("});\n");
    current.append("</script>");
    return null;
  }

  @Override
  public ItemActionMonitor makeItemActionMonitor(String name) {
    {
      String graphId = makeId();
      current.append("<b> Execution Rate:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(im_").append(name).append("_executed_total[1m])/rate(im_").append(name).append("_start_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b> Rejected:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(im_").append(name).append("_rejected_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b> Rejected:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(im_").append(name).append("_timeout_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    {
      String graphId = makeId();
      current.append("<b>Inflight: ").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"im_").append(name).append("_inflight\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    return null;
  }

}
