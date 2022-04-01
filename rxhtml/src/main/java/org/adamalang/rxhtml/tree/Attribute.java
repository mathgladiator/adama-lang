package org.adamalang.rxhtml.tree;

import org.adamalang.translator.parser.token.Token;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** $name or $name 'eq' $value where $value may contain [guard]blah[/guard] */
public class Attribute {
  public static final Pattern GUARD_INITIATE = Pattern.compile("[\\[]([a-zA-Z_]\\w*)[\\]]");
  public final Token name;
  public final Token equals;
  public final Token value;
  public final String[] guards;

  public Attribute(Token name, Token equals, Token value) {
    this.name = name;
    this.equals = equals;
    this.value = value;
    HashSet<String> gtemp = new HashSet<>();
    if (value != null) {
      Matcher matcher = GUARD_INITIATE.matcher(value.text);
      while (matcher.find()) {
        gtemp.add(matcher.group(1));
      }
    }
    this.guards = gtemp.toArray(new String[gtemp.size()]);
  }

  public String assignLine(String var) {
    // TODO: evaluate guards and add some shenagins
    // TODO: translate name.text to JavaScript name (BIG TABLE)
    return var + "." + name.text + " = " + value.text + ";";
  }

  public String html() {
    return name.text + (value != null ? ("=" + value.text) : "");
  }
}
