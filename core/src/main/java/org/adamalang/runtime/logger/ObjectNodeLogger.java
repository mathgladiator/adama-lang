/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a logger which incorporates the deltas into a giant JSON object */
public class ObjectNodeLogger implements TransactionLogger {
  public static ObjectNodeLogger fresh() {
    return new ObjectNodeLogger(Utility.createObjectNode());
  }

  public static ObjectNodeLogger recover(ObjectNode prior) {
    return new ObjectNodeLogger(prior);
  }

  public ObjectNode node;

  private ObjectNodeLogger(final ObjectNode node) {
    this.node = node;
  }

  @Override
  public void close() {
  }

  @Override
  public void ingest(final Transaction transaction) {
    node = (ObjectNode) JsonAlgebra.patch(node, transaction.delta);
  }
}
