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
package org.adamalang.extern.aws;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/** parsing for S3's XML */
public class S3XmlParsing {

  public static class ListResult {
    public final String[] keys;
    public final boolean truncated;
    public ListResult(String[] keys, boolean truncated) {
      this.keys = keys;
      this.truncated = truncated;
    }

    public String last() {
      if (keys.length > 0) {
        return keys[keys.length - 1];
      }
      return null;
    }
  }

  public static ListResult listResultOf(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    ArrayList<String> keys = new ArrayList<>();
    NodeList nodeList = doc.getElementsByTagName("Key");
    for (int k = 0; k < nodeList.getLength(); k++) {
      keys.add(nodeList.item(k).getTextContent());
    }
    boolean truncated = "true".equals(doc.getElementsByTagName("IsTruncated").item(0).getTextContent());
    return new ListResult(keys.toArray(new String[keys.size()]), truncated);
  }
}
