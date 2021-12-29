/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.stdlib;

import java.util.ArrayList;
import java.util.function.Function;
import org.adamalang.runtime.natives.NtMaybe;

/** some runtime exposed to the living document */
public class Utility {

  /** convert the maybe of one type into the maybe of another */
  public static <TyIn, TyOut> NtMaybe<TyOut> convertMaybe(final NtMaybe<TyIn> in, final Function<TyIn, TyOut> conv) {
    if (in.has()) {
      return new NtMaybe<>(conv.apply(in.get()));
    } else {
      return new NtMaybe<>();
    }
  }

  /** convert a list of Integers into an array of ints */
  public static int[] convertIntegerArrayList(ArrayList<Integer> in) {
    int[] output = new int[in.size()];
    int at = 0;
    for(Integer v : in) {
      output[at] = v;
      at++;
    }
    return output;
  }

  public static <TyIn, TyOut> TyOut[] convertMultiple(final Iterable<TyIn> source, final Function<Integer, TyOut[]> makeArray, final Function<TyIn, TyOut> conv) {
    final var out = new ArrayList<TyOut>();
    for (final TyIn item : source) {
      out.add(conv.apply(item));
    }
    return out.toArray(makeArray.apply(out.size()));
  }

  public static <TyIn, TyOut> TyOut[] convertMultiple(final TyIn[] source, final Function<Integer, TyOut[]> makeArray, final Function<TyIn, TyOut> conv) {
    final var result = makeArray.apply(source.length);
    for (var k = 0; k < source.length; k++) {
      result[k] = conv.apply(source[k]);
    }
    return result;
  }

  public static <TyIn, TyOut> TyOut convertSingle(final TyIn in, final Function<TyIn, TyOut> conv) {
    return conv.apply(in);
  }

  public static <T> T identity(final T value) {
    return value;
  }

  public static <T> NtMaybe<T> lookup(final T[] arr, final int k) {
    final var maybe = new NtMaybe<T>();
    if (0 <= k && k < arr.length) {
      maybe.set(arr[k]);
    }
    return maybe;
  }


}
