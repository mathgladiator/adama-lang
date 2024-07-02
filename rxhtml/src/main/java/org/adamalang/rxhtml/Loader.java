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
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.preprocess.Mobilify;
import org.adamalang.rxhtml.preprocess.ExpandStaticObjects;
import org.adamalang.rxhtml.preprocess.Pagify;
import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Loader {
  public static Document parseForest(String forest, Feedback feedback, ProductionMode mode) {
    Document document = Jsoup.parse(forest);
    Mobilify.go(document, mode);
    // TODO: enabling this requires a LOT of work
    // MarkStaticContent.mark(document);
    Pagify.pagify(document);
    ExpandStaticObjects.expand(document, feedback);
    return document;
  }
}
