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
package org.adamalang.web.features;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.web.client.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.helpers.NOPLogger;

import java.net.URL;
import java.util.Locale;
import java.util.TreeMap;

public class UrlSummaryGenerator {

  private static boolean shredUrl(String url, ObjectNode node) {
    try {
      URL parsed = new URL(url);
      node.put("host", parsed.getHost());
      node.put("path", parsed.getPath());
      return false;
    } catch (Exception ex) {
      return true;
    }
  }

  public static void summarize(WebClientBase base, String url, Callback<ObjectNode> callback) {
    SimpleHttpRequest request = new SimpleHttpRequest("GET", url, new TreeMap<>(), SimpleHttpRequestBody.EMPTY);
    base.executeShared(request, new StringCallbackHttpResponder(NOPLogger.NOP_LOGGER, RequestResponseMonitor.UNMONITORED, new Callback<String>() {
      @Override
      public void success(String html) {
        ObjectNode summary = Json.newJsonObject();
        summary.put("url", url);
        summary.put("title", url);
        summary.put("description", "");

        try {
          Document document = Jsoup.parse(html);
          Elements titles = document.getElementsByTag("title");
          if (titles.size() > 0) {
            summary.put("title", titles.get(0).text());
          }
          for (Element element : document.getElementsByTag("meta")) {
            String property = element.attr("property");
            if (property == null) {
              continue;
            }
            String content = element.attr("content");
            if (content == null) {
              continue;
            }
            property = property.toLowerCase(Locale.ENGLISH).trim();
            content = content.trim();
            if (property.startsWith("og:")) {
              summary.put(property.substring(3), content);
            }
          }
          if (shredUrl(summary.get("url").textValue(), summary)) {
            shredUrl(url, summary);
          }
        } catch (Exception failedToParseAndShred) {
          // do nothing
        }
        callback.success(summary);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    }));
  }
}
