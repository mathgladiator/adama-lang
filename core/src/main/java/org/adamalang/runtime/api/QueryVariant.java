/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

/** since query parameters arrive as untyped strings, this will shread the
 * string into various types for easier consumption and testing */
public class QueryVariant {
  public final boolean bool_value;
  public final Double double_value;
  public final Integer int_value;
  public final String string_value;

  /** shred the given string into potential rigid types */
  public QueryVariant(final String value) {
    string_value = value;
    Integer local_int_value = null;
    Double local_double_value = null;
    try {
      local_int_value = Integer.parseInt(value);
    } catch (final NumberFormatException nfe) {}
    try {
      local_double_value = Double.parseDouble(value);
    } catch (final NumberFormatException nfe) {}
    int_value = local_int_value;
    double_value = local_double_value;
    bool_value = "true".equals(value) || "1".equals(value);
  }
}
