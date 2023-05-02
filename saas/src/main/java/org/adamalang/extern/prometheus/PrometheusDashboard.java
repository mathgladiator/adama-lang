/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.prometheus;

import org.adamalang.common.metrics.*;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PrometheusDashboard implements MetricsFactory {

  private String filename;
  private StringBuilder current;
  private final HashMap<String, String> files;
  private final StringBuilder nav;
  private final StringBuilder alerts;
  private int id;

  public PrometheusDashboard() {
    this.filename = null;
    this.current = null;
    this.files = new HashMap<>();
    this.nav = new StringBuilder();
    this.id = 0;
    this.alerts = new StringBuilder();
    alerts.append("groups:\n");
    nav.append(" [<a href=\"index.html\">Home</a>] ");
  }

  public void finish(File consolesDirectory) throws Exception {
    if (filename != null) {
      cut();
    }
    this.filename = "index.html";
    this.current = new StringBuilder();
    begin("Index");

    alerts.append("  - alert: jvm_threads_deadlocked\n");
    alerts.append("    expr: jvm_threads_deadlocked > 0\n");
    alerts.append("    for: 1m\n");
    alerts.append("    labels:\n");
    alerts.append("      severity: page\n");
    alerts.append("    annotations:\n");
    alerts.append("      summary: Too many JVM threads are deadlocked\n");

    String graphId = makeId();
    current.append("<table width=\"100%\"><tr><td width=\"30%\">");
    current.append("<b> Old Gen Memory </b>\n");
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"avg_over_time(jvm_memory_pool_bytes_used{pool=\\\"G1 Old Gen\\\"}[5m])\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td><td width=\"30%\">");
    current.append("<b> GC Pressure </b>\n");
    graphId = makeId();
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"rate(jvm_gc_collection_seconds_count[5m])\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td><td width=\"40%\">");
    current.append("<b> Memory </b>\n");
    graphId = makeId();
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"process_resident_memory_bytes\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td></tr></table>");

    current.append("<table width=\"100%\"><tr><td width=\"50%\">");
    current.append("<b> Send Failure Rates </b>\n");
    graphId = makeId();
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"rate(rr_connectionsend_failure_total[1m])/rate(rr_connectionsend_start_total[1m])\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td><td width=\"50%\">");
    current.append("<b> Connection Failure Rates </b>\n");
    graphId = makeId();
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"rate(stream_connectioncreate_failure_total[1m])/rate(stream_connectioncreate_start_total[1m])\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td></tr></table>");

    current.append("<table width=\"100%\"><tr><td width=\"50%\">");
    current.append("<b> Send Traffic </b>\n");
    graphId = makeId();
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"rate(rr_connectionsend_start_total[1m])\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td><td width=\"50%\">");
    current.append("<b> Connection Traffic </b>\n");
    graphId = makeId();
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"rate(stream_connectioncreate_start_total[1m])\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td></tr></table>");

    current.append("<table width=\"100%\"><tr><td width=\"50%\">");
    current.append("<b> CPU </b>\n");
    graphId = makeId();
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"rate(process_cpu_seconds_total[1m])\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td><td width=\"50%\">");
    current.append("<b> Threads Waiting </b>\n");
    graphId = makeId();
    current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
    current.append("new PromConsole.Graph({\n");
    current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
    current.append("  expr: \"jvm_threads_state{state=\\\"TIMED_WAITING\\\"}\",\n");
    current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: ''\n");
    current.append("});\n");
    current.append("</script>");
    current.append("</td></tr></table>");
    cut();


    for (Map.Entry<String, String> entry : files.entrySet()) {
      File toWrite = new File(consolesDirectory, entry.getKey());
      String data = entry.getValue();
      data = data.replaceAll(Pattern.quote("##NAV##"), nav.toString());
      Files.writeString(toWrite.toPath(), data);
    }
    Files.writeString(new File(consolesDirectory.getParentFile(), "adama_rules.yml").toPath(), alerts.toString());


  }

  String makeId() {
    return "ID" + (id++);
  }

  @Override
  public RequestResponseMonitor makeRequestResponseMonitor(String nameRaw) {
    String name = PrometheusMetricsFactory.makeNameCompatibleWithPrometheus(nameRaw);
    alerts.append("  - alert: FailureRate").append(name).append("\n");
    alerts.append("    expr: rate(rr_").append(name).append("_failure_total[2m])/rate(rr_").append(name).append("_start_total[2m]) > 0.05\n");
    alerts.append("    for: 5m\n");
    alerts.append("    labels:\n");
    alerts.append("      severity: page\n");
    alerts.append("    annotations:\n");
    alerts.append("      summary: High Failure Rate for ").append(name).append("\n");

    current.append("<table width=\"100%\"><tr><td width=\"25%\">");
    {
      String graphId = makeId();
      current.append("<b> Failure Rate:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(rr_").append(name).append("_failure_total[1m])/rate(rr_").append(name).append("_start_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"25%\">");
    {
      String graphId = makeId();
      current.append("<b> Latency p95:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"histogram_quantile(0.95, rate(rr_").append(name).append("_latency_bucket[1m])) * 1000\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"25%\">");
    {
      String graphId = makeId();
      current.append("<b> Successes:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(rr_").append(name).append("_success_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"25%\">");
    {
      String graphId = makeId();
      current.append("<b>Inflight: ").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rr_").append(name).append("_inflight\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td></tr></table>");
    return null;
  }

  @Override
  public StreamMonitor makeStreamMonitor(String nameRaw) {
    String name = PrometheusMetricsFactory.makeNameCompatibleWithPrometheus(nameRaw);
    alerts.append("  - alert: FailureRate").append(name).append("\n");
    alerts.append("    expr: rate(stream_").append(name).append("_failure_total[2m])/rate(stream_").append(name).append("_start_total[2m]) > 0.05\n");
    alerts.append("    for: 5m\n");
    alerts.append("    labels:\n");
    alerts.append("      severity: page\n");
    alerts.append("    annotations:\n");
    alerts.append("      summary: High Failure Rate for ").append(name).append("\n");

    current.append("<table width=\"100%\"><tr><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b> Failure Rate:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(stream_").append(name).append("_failure_total[1m])/rate(stream_").append(name).append("_start_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b> Progress:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(stream_").append(name).append("_progress_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b> Finishes:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(stream_").append(name).append("_finish_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b>Inflight: ").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"stream_").append(name).append("_inflight\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b> Latency p95:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"histogram_quantile(0.95, rate(stream_").append(name).append("_first_latency_bucket[1m])) * 1000\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td></tr></table>");
    return null;
  }

  @Override
  public CallbackMonitor makeCallbackMonitor(String name) {
    alerts.append("  - alert: FailureRate").append(name).append("\n");
    alerts.append("    expr: rate(cb_").append(name).append("_failure_total[2m])/rate(cb_").append(name).append("_start_total[2m]) > 0.05\n");
    alerts.append("    for: 5m\n");
    alerts.append("    labels:\n");
    alerts.append("      severity: page\n");
    alerts.append("    annotations:\n");
    alerts.append("      summary: High Failure Rate for ").append(name).append("\n");
    current.append("<table width=\"100%\"><tr><td width=\"25%\">");
    {
      String graphId = makeId();
      current.append("<b> Failure Rate:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(cb_").append(name).append("_failure_total[1m])/rate(cb_").append(name).append("_start_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"25%\">");
    {
      String graphId = makeId();
      current.append("<b> Latency p95:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"histogram_quantile(0.95, rate(cb_").append(name).append("_latency_bucket[1m])) * 1000\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"25%\">");
    {
      String graphId = makeId();
      current.append("<b> Successes:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(cb_").append(name).append("_success_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"25%\">");
    {
      String graphId = makeId();
      current.append("<b>Inflight: ").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"cb_").append(name).append("_inflight\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td></tr></table>");
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
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
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
    current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
    current.append("  yTitle: '").append(name).append("'\n");
    current.append("});\n");
    current.append("</script>");
    if (name.startsWith("alarm_")) {
      alerts.append("  - alert: ").append(name.substring(6)).append("\n");
      alerts.append("    expr: inf_").append(name).append(" > 0\n");
      alerts.append("    for: 1m\n");
      alerts.append("    labels:\n");
      alerts.append("      severity: page\n");
      alerts.append("    annotations:\n");
      alerts.append("      summary: A manual alarm fired - ").append(name).append("\n");
    }
    return null;
  }

  @Override
  public ItemActionMonitor makeItemActionMonitor(String name) {
    alerts.append("  - alert: SuccessDrop").append(name).append("\n");
    alerts.append("    expr: rate(im_").append(name).append("_executed_total[2m])/rate(im_").append(name).append("_start_total[2m]) < 0.95\n");
    alerts.append("    for: 5m\n");
    alerts.append("    labels:\n");
    alerts.append("      severity: page\n");
    alerts.append("    annotations:\n");
    alerts.append("      summary: Success Drop for ").append(name).append("\n");
    current.append("<table width=\"100%\"><tr><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b> Execution Rate:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(im_").append(name).append("_executed_total[1m])/rate(im_").append(name).append("_start_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b> Latency p95:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"histogram_quantile(0.95, rate(im_").append(name).append("_latency_bucket[1m])) * 1000\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b> Rejected:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(im_").append(name).append("_rejected_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b> Timeout:").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"rate(im_").append(name).append("_timeout_total[1m])\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td><td width=\"20%\">");
    {
      String graphId = makeId();
      current.append("<b>Inflight: ").append(name).append("</b>\n");
      current.append("<div id=\"").append(graphId).append("\"></div><script>\n");
      current.append("new PromConsole.Graph({\n");
      current.append("  node: document.querySelector(\"#").append(graphId).append("\"),\n");
      current.append("  expr: \"im_").append(name).append("_inflight\",\n");
      current.append("  yAxisFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  yHoverFormatter: PromConsole.NumberFormatter.humanizeNoSmallPrefix,\n");
      current.append("  name: function(v) { return v.instance + \"(\" + v.service + \")\";  },\n");
      current.append("  yTitle: '").append(name).append("'\n");
      current.append("});\n");
      current.append("</script>");
    }
    current.append("</td></tr></table>");
    return null;
  }

  @Override
  public void page(String name, String title) {
    if (filename != null) {
      cut();
    }
    alerts.append("- name: ").append(name).append("\n");
    alerts.append("  rules:\n");
    nav.append(" [<a href=\"adama-").append(name).append(".html").append("\">").append(title).append("</a>] ");
    this.filename = "adama-" + name + ".html";
    this.current = new StringBuilder();
    begin(title);
  }

  @Override
  public void section(String title) {
    current.append("<h3>").append(title).append("</h3>\n");
  }

  public void cut() {
    current.append("\n");
    current.append("{{ template \"prom_content_tail\" . }}\n");
    current.append("{{ template \"tail\" . }}\n");
    files.put(filename, current.toString());
  }

  private void begin(String title) {
    current.append("{{ template \"head\" . }}\n");
    current.append("{{ template \"prom_right_table_head\" }}");
    current.append("##NAV##\n");
    current.append("{{ template \"prom_right_table_tail\" }}");
    current.append("{{ template \"prom_content_head\" . }}\n");
    current.append("<h1>").append(title).append("</h1>\n");
  }
}
