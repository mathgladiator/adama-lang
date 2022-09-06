/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

public class TemplateMultipageTests extends BaseRxHtmlTest {
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Text type user profile '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['string','usery'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Text type user profile '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','profile'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' No type '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','settings','text','key'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets1','number','key'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets2','int','key'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets3','double','key'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','yolo'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/bounce').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','bounce'], function(b,a) {");
    gold.append("\n    if ($.ID('default','/login').abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','login'], function(b,a) {");
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><link rel=\"stylesheet\" href=\"/template.css\"><script src=\"/template.js\"></script></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\" authenticate>");
    source.append("\n        Simple Page");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user:text\" authenticate>");
    source.append("\n        Text type user profile");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$usery:string\" authenticate>");
    source.append("\n        Text type user profile");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/profile\" authenticate>");
    source.append("\n        No type");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/settings/$key\" authenticate>");
    source.append("\n        Keys for a user");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/sets1/$key:number\" authenticate>");
    source.append("\n        Keys for a user");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/sets2/$key:int\" authenticate>");
    source.append("\n        Keys for a user");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/sets3/$key:double\" authenticate>");
    source.append("\n        Keys for a user");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/yolo\" authenticate redirect=\"/bounce\">");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/bounce\" authenticate>");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/login\" default-redirect-source>");
    source.append("\n        Simple Page");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
