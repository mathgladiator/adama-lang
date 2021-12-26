package org.adamalang.runtime.natives;

/** a complex number */
public class NtComplex {
    public final double real;
    public final double imaginary;

    public NtComplex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NtComplex ntComplex = (NtComplex) o;
        return Double.compare(ntComplex.real, real) == 0 && Double.compare(ntComplex.imaginary, imaginary) == 0;
    }

    public long memory() {
        return 16;
    }
}
