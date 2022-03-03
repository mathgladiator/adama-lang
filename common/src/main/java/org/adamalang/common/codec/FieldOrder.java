package org.adamalang.common.codec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/** the order in which a field is serialized. This is required for long term stability */
public @interface FieldOrder {
  public int value();
}
