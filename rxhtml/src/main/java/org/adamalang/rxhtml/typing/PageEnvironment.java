/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.rxhtml.typing;

import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.regex.Pattern;

public class PageEnvironment {
  private final HashMap<String, DataScope> connections;
  private final PrivacyFilter privacy;
  private final DataScope scope;
  private final Element fragmentProvider;
  private final HashMap<String, Element> templates;

  public PageEnvironment(PrivacyFilter privacy, DataScope scope, Element fragmentProvider, HashMap<String, Element> templates, HashMap<String, DataScope> connections) {
    this.privacy = privacy;
    this.scope = scope;
    this.fragmentProvider = fragmentProvider;
    this.templates = templates;
    this.connections = connections;
  }

  public void registerConnection(String name, DataScope scope) {
    connections.put(name, scope);
  }

  public PageEnvironment maybePickConnection(String name) {
    DataScope scope = connections.get(name);
    if (scope == null) {
      return null;
    }
    return withDataScope(scope);
  }

  public Element getFragmentProvider() {
    return fragmentProvider;
  }

  public Element findTemplate(String name) {
    return templates.get(name);
  }

  public PageEnvironment withFragmentProvider(Element fragmentProvider) {
    return new PageEnvironment(privacy, scope, fragmentProvider, templates, connections);
  }

  public PageEnvironment withDataScope(DataScope scope) {
    return new PageEnvironment(privacy, scope, fragmentProvider, templates, connections);
  }

  public static PageEnvironment newPage(String privacy, HashMap<String, Element> templates) {
    return new PageEnvironment(new PrivacyFilter(privacy.split(Pattern.quote(","))), null, null, templates, new HashMap<>());
  }
}
