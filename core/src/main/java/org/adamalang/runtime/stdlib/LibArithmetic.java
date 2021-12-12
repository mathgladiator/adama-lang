package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtMaybe;

/** Since division is complicated, we need to think about what it means to build up arithmetic using maybe<N> where N is a numeric type of int, long, double */
public class LibArithmetic {

    public static NtMaybe<Double> divDD(double x, double y) {
        double z = x / y;
        if (Double.isNaN(z) || Double.isInfinite(z)) {
            return new NtMaybe<>();
        }
        return new NtMaybe<>(z);
    }

    public static NtMaybe<Double> divDD(NtMaybe<Double> x, double y) {
        if (x.has()) {
            return divDD(x.get(), y);
        }
        return x;
    }

    public static NtMaybe<Double> divDD(double x, NtMaybe<Double> y) {
        if (y.has()) {
            return divDD(x, y.get());
        }
        return y;
    }

    public static NtMaybe<Double> divDD(NtMaybe<Double> x, NtMaybe<Double> y) {
        if (x.has()) {
            if (y.has()) {
                return divDD(x.get(), y.get());
            }
            return y;
        } else {
            return x;
        }
    }

    public static NtMaybe<Double> divII(int x, int y) {
        double z = ((double) x) / y;
        if (Double.isNaN(z) || Double.isInfinite(z)) {
            return new NtMaybe<>();
        }
        return new NtMaybe<>(z);
    }
}
