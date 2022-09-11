/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.frontend;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

import java.util.TreeMap;
import java.util.regex.Pattern;

public class SpaceTemplates {
  public static class SpaceTemplate {
    public final String adama;
    public final String rxhtml;

    public SpaceTemplate(String adama, String rxhtml) {
      this.adama = adama;
      this.rxhtml = rxhtml;
    }

    public String initialRxHTML(String spaceName) {
      return rxhtml.replaceAll(Pattern.quote("$TEMPLATE_SPACE"), spaceName);
    }

    public String idearg(String spaceName) {
      ObjectNode node = Json.newJsonObject();
      node.put("adama", adama);
      node.put("rxhtml", initialRxHTML(spaceName));
      return node.toString();
    }

    public ObjectNode plan() {
      ObjectNode plan = Json.newJsonObject();
      plan.put("default", "main");
      plan.putArray("plan");
      ObjectNode main = plan.putObject("versions").putObject("main");
      main.put("main", adama);
      return plan;
    }
  }

  private final TreeMap<String, SpaceTemplate> templates;

  private SpaceTemplates() {
    this.templates = new TreeMap<>();
    // BEGIN-TEMPLATES-POPULATE
    templates.put("chat", new SpaceTemplate("@static {\n  create {\n    return true;\n  }\n\n  invent {\n    return true;\n  }\n}\n\n@connected {\n  return true;\n}\n\n// `who said `what `when\nrecord Line {\n  public principal who;\n  public string what;\n  public long when;\n}\n\n// a table will privately store messages\ntable<Line> _chat;\n\n// since we want all connected parties to\n// see everything, just reactively expose it\npublic formula chat = iterate _chat;\n\nmessage Say {\n  string what;\n}\n\n// the \"channel\" enables someone to send a message\n// bound to some code\nchannel say(Say what) {\n  // ingest the line into the chat\n  _chat <- {who:@who, what:what.what, when: Time.now()};\n\n  // since you are paying for the chat, let's cap the\n  // size to 50 total messages.\n  (iterate _chat order by when desc offset 50).delete();\n}\n","<forest>\n  <template name=\"chatter\">\n    <h2><fragment /></h2>\n    <table border=\"1\">\n    <thead><tr><th>Who</th><th>What</th><th>When</th></thead>\n    <tbody rx:iterate=\"chat\">\n      <tr>\n        <td><lookup path=\"who\" transform=\"principal.agent\" /></td>\n        <td><lookup path=\"what\" /></td>\n        <td><lookup path=\"when\" /></td>\n      </tr>\n    </tbody></table>\n    <form rx:action=\"send:say\">\n      <input type=\"text\" name=\"what\">\n      <button type=\"submit\">Say</button>\n    </form>\n  </template>\n  <page uri=\"/\">\n    <table><tr><td>\n      <connection name=\"alice\" space=\"$TEMPLATE_SPACE\" key=\"talk\" identity=\"direct:anonymous:alice\">\n        <div rx:template=\"chatter\">Alice</div>\n      </connection>\n    </td><td>\n      <connection name=\"bob\" space=\"$TEMPLATE_SPACE\" key=\"talk\" identity=\"direct:anonymous:bob\">\n        <div rx:template=\"chatter\">Bob</div>\n      </connection>\n    </td></tr></table>\n  </page>\n</forest>"));
    templates.put("none", new SpaceTemplate("@static {\n  create {\n    return @who.isAnonymous() || @who.isAdamaDeveloper();\n  }\n}\n\nprivate principal owner;\n\n@construct {\n  owner = @who;\n}\n\n@connected {\n  return owner == @who;\n}\n\n@delete {\n  return owner == @who;\n}\n","<forest>\n  <template name=\"perspective\">\n    <h2><fragment /></h2>\n    Here is where you bind data...\n  </template>\n  <page uri=\"/\">\n    <table><tr><td>\n      <connection space=\"$TEMPLATE_SPACE\" key=\"demo\" identity=\"direct:anonymous:alice\">\n        <div rx:template=\"perspective\">Alice</div>\n      </connection>\n    </td><td>\n      <connection space=\"$TEMPLATE_SPACE\" key=\"demo\" identity=\"direct:anonymous:bob\">\n        <div rx:template=\"perspective\">Bob</div>\n      </connection>\n    </td></tr></table>\n  </page>\n</forest>"));
    templates.put("pubsub", new SpaceTemplate("@static {\n  // anyone can create/invent\n  create { return true; }\n  invent { return true; }\n}\n\n// let everyone connect; sure, what can go wrong\n@connected {\n  return true;\n}\n\n// let everyone delete; sure, what can go wrong\n@delete {\n  return true;\n}\n\n// we build a table of publishes with who published it and when they did so\nrecord Publish {\n  public principal who;\n  public long when;\n  public string payload;\n}\n\ntable<Publish> _publishes;\n\n// since tables are private, we expose all publishes to all connected people\npublic formula publishes = iterate _publishes order by when asc;\n\n// we wrap a payload inside a message\nmessage PublishMessage {\n  string payload;\n}\n\nprocedure expire_publishes() {\n  (iterate _publishes where when < Time.now() - 30000).delete();\n}\n\n// and then open a channel to accept the publish from any connected client\nchannel publish(PublishMessage message) {\n  _publishes <- {who: @who, when: Time.now(), payload: message.payload };\n\n  // At this point, we encounter a key problem with maintaining a\n  // log of publishes. Namely, the log is potentially infinite, so\n  // we have to leverage some product intelligence to reduce it to\n  // a reasonably finite set which is important for the product.\n\n  // First, we age out publishes too old (sad face)\n  expire_publishes();\n\n  // Second, we hard cap the publishes biasing younger ones\n  (iterate _publishes\n     order by when desc\n     offset 10).delete();\n\n  transition #clean in 1;\n}\n\n#clean {\n  expire_publishes();\n  if (_publishes.size() > 0) {\n    transition #clean in 1;\n  }\n}\n\n","<forest>\n  <template name=\"history\">\n    <h2><fragment /></h2>\n    <table border=\"1\">\n    <thead><tr><th>Who</th><th>Payload</th><th>When</th></thead>\n    <tbody rx:iterate=\"publishes\">\n      <tr>\n        <td><lookup path=\"who\" transform=\"principal.agent\" /></td>\n        <td><lookup path=\"payload\" /></td>\n        <td><lookup path=\"when\" /></td>\n      </tr>\n    </tbody></table>\n    <form rx:action=\"send:publish\">\n      <input type=\"text\" name=\"payload\">\n      <button type=\"submit\">Publish</button>\n    </form>\n  </template>\n  <page uri=\"/\">\n    <table><tr><td>\n      <connection name=\"alice\" space=\"$TEMPLATE_SPACE\" key=\"demo-log\" identity=\"direct:anonymous:alice\">\n        <div rx:template=\"history\">Alice's View</div>\n      </connection>\n    </td><td>\n      <connection name=\"bob\" space=\"$TEMPLATE_SPACE\" key=\"demo-log\" identity=\"direct:anonymous:bob\">\n        <div rx:template=\"history\">Bob's View</div>\n      </connection>\n    </td></tr></table>\n  </page>\n</forest>"));
    templates.put("tic-tac-toe", new SpaceTemplate("@static {\n  // As this is going to be a live home-page sample, let anyone create\n  create { return true; }\n  invent { return true; }\n\n  // As this will spawn on demand, let's clean up when the viewer goes away\n  delete_on_close = true;\n}\n\n// What is the state of a square\nenum SquareState { Open, X, O }\n\n// who are the two players\npublic principal playerX;\npublic principal playerO;\n\n// who is the current player\npublic principal current;\n\n// how many wins per player\npublic int wins_X;\npublic int wins_O;\n\n// how many stalemates\npublic int stalemates;\n\n// personalized data for the connected player:\n// show the player their role, a signal if it is their turn, and their wins\nbubble your_role = playerX == @who ? \"X\" : (playerO == @who ? \"O\" : \"Observer\");\nbubble your_turn = current == @who;\nbubble your_wins = playerX == @who ? wins_X : (playerO == @who ? wins_O : 0);\n\n// a record of the data in the square\nrecord Square {\n  public int id;\n  public int x;\n  public int y;\n  public SquareState state;\n}\n\n// the collection of all squares\ntable<Square> _squares;\n\n// for visualization, we break the squares into rows\npublic formula row1 = iterate _squares where y == 0;\npublic formula row2 = iterate _squares where y == 1;\npublic formula row3 = iterate _squares where y == 2;\n\n// when the document is created, initialize the squares and zero out the totals\n@construct {\n  for (int y = 0; y < 3; y++) {\n    for (int x = 0; x < 3; x++) {\n      _squares <- { x:x, y:y, state: SquareState::Open };\n    }\n  }\n  wins_X = 0;\n  wins_O = 0;\n  stalemates = 0;\n}\n\n// when a player connects, assign them to either the X or O role. If there are more than two players, then they can observe.\n@connected {\n  if (playerX == @no_one) {\n    playerX = @who;\n    if (playerO != @no_one) {\n      transition #initiate;\n    }\n  } else if (playerO == @no_one) {\n    playerO = @who;\n    if (playerX != @no_one) {\n      transition #initiate;\n    }\n  }\n  return true;\n}\n\n// open a channel for players to select a move\nmessage Play { int id; }\nchannel<Play> play;\n\n// the game is afoot\n#initiate {\n  current = playerX;\n  transition #turn;\n}\n\n// test if the placed square produced a winning combination\nprocedure test_placed_for_victory(SquareState placed) -> bool {\n  for (int k = 0; k < 3; k++) {\n    // vertical lines\n    if ( (iterate _squares where x == k && state == placed).size() == 3) {\n      return true;\n    }\n    // horizontal lines\n    if ( (iterate _squares where y == k && state == placed).size() == 3) {\n      return true;\n    }\n  }\n  // diagonals\n  if ( (iterate _squares where y == x && state == placed).size() == 3 || (iterate _squares where y == 2 - x && state == placed).size() == 3 ) {\n    return true;\n  }\n  return false;\n}\n\n#turn {\n  // find the open spaces\n  list<Square> open = iterate _squares where state == SquareState::Open;\n  if (open.size() == 0) {\n    stalemates++;\n    transition #end;\n    return;\n  }\n  // ask the current play to choose an open space\n  if (play.decide(current, @convert<Play>(open)).await() as pick) {\n    // assign the open space to the player\n    let placed = playerX == current ? SquareState::X : SquareState::O;;\n    (iterate _squares where id == pick.id).state = placed;\n    if (test_placed_for_victory(placed)) {\n      if (playerX == current) {\n        wins_X++;\n      } else {\n        wins_O++;\n      }\n      transition #end;\n    } else {\n      transition #turn;\n    }\n    current = playerX == current ? playerO : playerX;\n  }\n}\n\n#end {\n  (iterate _squares).state = SquareState::Open;\n  transition #turn;\n}\n","<forest>\n  <template name=\"cell\">\n    <span rx:switch=\"state\">\n      <span rx:case=\"0\">\n        <span rx:if=\"decide:play\">\n          <button rx:click=\"decide:play\">Play</button>\n        </span>\n      </span>\n      <span rx:case=\"1\">X</span>\n      <span rx:case=\"2\">O</span>\n    </span>\n  </template>\n\n  <template name=\"game\">\n    <h2><fragment /> - <lookup path=\"your_role\" /> (Won: <lookup path=\"your_wins\" />)</h2>\n    <table border=\"1\">\n      <tr rx:iterate=\"row1\">\n        <td rx:template=\"cell\"></td>\n      </tr>\n      <tr rx:iterate=\"row2\">\n        <td rx:template=\"cell\"></td>\n      </tr>\n      <tr rx:iterate=\"row3\">\n        <td rx:template=\"cell\"></td>\n      </tr>\n    </table>\n  </template>\n  <page uri=\"/\">\n    <connection name=\"player1\" identity=\"direct:anonymous:alice\" space=\"$TEMPLATE_SPACE\" key=\"demo-{view:session_id}\">\n      <div rx:template=\"game\">Alice</div>\n    </connection>\n    <hr />\n    <connection name=\"player2\" identity=\"direct:anonymous:bob\" space=\"$TEMPLATE_SPACE\" key=\"demo-{view:session_id}\">\n      <div rx:template=\"game\">Bob</div>\n    </connection>\n  </page>\n</forest>"));
    // END-TEMPLATES-POPULATE
  }

  public SpaceTemplate of(String name) {
    SpaceTemplate bundle = templates.get(name == null ? "none" : name);
    if (bundle != null) {
      return bundle;
    }
    return templates.get("none");
  }

  public static final SpaceTemplates REGISTRY = new SpaceTemplates();
}
