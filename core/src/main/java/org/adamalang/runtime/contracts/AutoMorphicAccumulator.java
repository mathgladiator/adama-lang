package org.adamalang.runtime.contracts;

/** provides a mechanism to accumulate data into a final bundle */
public interface AutoMorphicAccumulator<T> {

    /** provide a data element */
    public void next(T data);

    /** finish the stream */
    public T finish();
}
