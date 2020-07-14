/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.natives.NtClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a bridge is a way of filling many type-erasure holes and sorting out how to
 * read/write JSON */
public interface Bridge<Ty> {
  /** append the given value to the given array */
  public void appendTo(Ty value, ArrayNode array);
  /** read the given type from the given JSON tree */
  public Ty fromJsonNode(JsonNode node);
  /** invent an array with n elements */
  public Ty[] makeArray(int n);
  /** convert the given value to a private JSON node for the given client */
  public JsonNode toPrivateJsonNode(NtClient who, Ty value);
  /** write the given value to the given Object under the field `name */
  public void writeTo(String name, Ty value, ObjectNode node);
}
