/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml.template.elements;

import org.adamalang.rxhtml.template.Base;
import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Fix;

public class Input {
  public static void write(Environment env) {
    String type = Fix.ident(env.element.attr("type"));
    String name = Fix.ident(env.element.attr("name"));
    if (type == null) {
      type = "text";
    }
    String inputVar = Base.write(env.returnVariable(true));
    if (env.formVariable != null) {
      if ("checkbox".equals(type)) {
        if (env.element.hasAttr("value")) {
          env.writer.tab().append("_$.push((function(x) { if (this.checked) { x['").append(name).append("'] = this.value; }; }).bind(").append(inputVar).append("));").newline();
        } else {
          env.writer.tab().append("_$.push((function(x) { x['").append(name).append("'] = this.checked; }).bind(").append(inputVar).append("));").newline();
        }
      } else if ("radio".equals(type)) {
        env.writer.tab().append("_$.push((function(x) { if (this.checked) { x['").append(name).append("'] = this.value; }; }).bind(").append(inputVar).append("));").newline();
      } else {
        env.writer.tab().append("_$.push((function(x) { x['").append(name).append("'] = this.value; }).bind(").append(inputVar).append("));").newline();
      }
    }
    env.pool.give(inputVar);
  }

  /**
   *
   <input type="button">
   <input type="checkbox">
   <input type="color">
   <input type="date">
   <input type="datetime-local">
   <input type="email">
   <input type="file">
   <input type="hidden">
   <input type="image">
   <input type="month">
   <input type="number">
   <input type="password">
   <input type="radio">
   <input type="range">
   <input type="reset">
   <input type="search">
   <input type="submit">
   <input type="tel">
   <input type="text">
   <input type="time">
   <input type="url">
   <input type="week">

   */
}
