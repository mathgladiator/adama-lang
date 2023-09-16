package org.adamalang.rxhtml;

import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/** entry point for the type checker */
public class TypeChecker {
  /** Given a bundled forest, produce feedback for the developer */
  public final void typecheck(String forest, Feedback feedback) {
    Document document = Jsoup.parse(forest);
  }
}
