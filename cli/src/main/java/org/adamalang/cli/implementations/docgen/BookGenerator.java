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
package org.adamalang.cli.implementations.docgen;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.common.template.Settings;
import org.adamalang.common.template.tree.T;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/** generate the book from markdown and related assets */
public class BookGenerator {

  private void assemble(File root, String prefix, TreeMap<String, String> files, TreeMap<String, File> merge) throws Exception {
    for (File child : root.listFiles()) {
      if (child.isDirectory()) {
        assemble(child, prefix + child.getName() + "/", files, merge);
      } else {
        if (child.getName().endsWith(".md")) {
          files.put(prefix + child.getName(), Files.readString(child.toPath()));
        } else {
          merge.put(prefix + child.getName(), child);
        }
      }
    }
  }

  private String md2html(String markdown) {
    HashSet<Extension> ext = new HashSet<>();
    ext.add(TablesExtension.create());
    Parser parser = Parser.builder().extensions(ext).build();
    Node document = parser.parse(markdown);
    HtmlRenderer renderer = HtmlRenderer.builder().extensions(ext).build();
    Document doc = Jsoup.parse(renderer.render(document));
    for (Element a : doc.getElementsByTag("a")) {
      if (a.hasAttr("href")) {
        String href = a.attr("href");
        if (href.endsWith(".md")) {
          a.attr("href", href.substring(0, href.length() - 3) + ".html");
        }
      }
    }
    return doc.body().html();
  }

  private String render(String markdown, String navHtml, T template) {
    ObjectNode input = Json.newJsonObject();
    input.put("body", md2html(markdown));
    input.put("nav", navHtml);
    StringBuilder output = new StringBuilder();
    template.render(new Settings(true), input, output);
    return output.toString();
  }

  private String renderNav(String markdown) {
    Document doc = Jsoup.parse(md2html(markdown));
    Element body = doc.body();
    body.getElementsByTag("h1").remove();
    for (Element element : body.children()) {
      if (element.tagName().endsWith("ul")) {
        element.attr("class", "text-sm list-disc list-inside");
        for (Element firstLevelItem : element.children()) {
          if (firstLevelItem.tagName().equals("li")) {
            firstLevelItem.attr("class", "mb-2");
            for (Element secondLevel : firstLevelItem.children()) {
              if (secondLevel.tagName().endsWith("ul")) {
                secondLevel.attr("class", "mb-3 ml-4 pl-6 border-l border-slate-200 dark:border-slate-800 list-decimal");
                for (Element secondLevelItem : secondLevel.children()) {
                  if (secondLevelItem.tagName().equals("li")) {
                    secondLevelItem.attr("class", "mt-3");
                  }
                }
              }
            }
          }
        }
      }
    }
    for (Element a : body.getElementsByTag("a")) {
      if (a.hasAttr("href")) {
        String href = a.attr("href");
        if (href.startsWith(".")) {
          a.attr("href", href.substring(1));
        }
      }
    }

    return body.html();
  }

  public void go(Arguments.ContribMakeBookArgs args, Output.YesOrError output) throws Exception {
    File input = new File(args.input);
    TreeMap<String, String> book = new TreeMap<>();
    TreeMap<String, File> merge = new TreeMap<>();
    assemble(input, "", book, merge);
    File out = new File(args.output);
    out.mkdirs();

    T template = org.adamalang.common.template.Parser.parse(Files.readString(new File(args.bookTemplate).toPath()));

    String navHtml = renderNav(book.remove("SUMMARY.md"));

    for (Map.Entry<String, String> entry : book.entrySet()) {
      DirFile df = new DirFile(out, entry.getKey());

      String name = df.name.substring(0, df.name.lastIndexOf('.')) + ".html";
      Files.writeString(new File(df.dir, name).toPath(), render(entry.getValue(), navHtml, template));
    }

    for (Map.Entry<String, File> entry : merge.entrySet()) {
      DirFile df = new DirFile(out, entry.getKey());
      Files.copy(entry.getValue().toPath(), new File(df.dir, df.name).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    for (File child : new File(args.bookMerge).listFiles()) {
      Files.copy(child.toPath(), new File(out, child.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    output.out();
  }

  private class DirFile {
    public final File dir;
    public final String name;

    public DirFile(File out, String path) {
      int lastSlash = path.lastIndexOf('/');
      File toPut = out;
      String local = path;
      if (lastSlash > 0) {
        toPut = new File(toPut, path.substring(0, lastSlash));
        toPut.mkdirs();
        local = path.substring(lastSlash + 1);
      }
      this.dir = toPut;
      this.name = local;
    }
  }
}
