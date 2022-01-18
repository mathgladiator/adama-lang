/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.billing;

import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.DocumentThreadBase;
import org.adamalang.runtime.sys.PredictiveInventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** a state machine for computing a service bill across all threads */
public class BillingStateMachine {
  private final DocumentThreadBase[] bases;
  private final Consumer<HashMap<String, PredictiveInventory.Billing>> onFinalBill;
  private final HashMap<String, PredictiveInventory.Billing> accum;
  private int at;

  private BillingStateMachine(
      DocumentThreadBase[] bases,
      Consumer<HashMap<String, PredictiveInventory.Billing>> onFinalBill) {
    this.bases = bases;
    this.at = 0;
    this.onFinalBill = onFinalBill;
    this.accum = new HashMap<>();
  }

  public static void bill(
      DocumentThreadBase[] bases,
      LivingDocumentFactoryFactory factory,
      Consumer<HashMap<String, PredictiveInventory.Billing>> onFinalBill) {
    new BillingStateMachine(bases, onFinalBill).seed(factory.spacesAvailable()).next();
  }

  private void next() {
    if (at < bases.length) {
      bases[at].bill(
          (b) -> {
            for (Map.Entry<String, PredictiveInventory.Billing> entry : b.entrySet()) {
              PredictiveInventory.Billing prior = accum.get(entry.getKey());
              if (prior != null) {
                accum.put(entry.getKey(), PredictiveInventory.Billing.add(prior, entry.getValue()));
              } else {
                accum.put(entry.getKey(), entry.getValue());
              }
            }
            at++;
            next();
          });
    } else {
      onFinalBill.accept(accum);
    }
  }

  private BillingStateMachine seed(Collection<String> spaces) {
    for (String space : spaces) {
      accum.put(space, new PredictiveInventory.Billing(0, 0, 0, 0, 0));
    }
    return this;
  }
}
