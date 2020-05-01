package uk.gov.ons.ctp.response.lib.common.util;

import java.util.Optional;
import net.sourceforge.cobertura.CoverageIgnore;

/** Enum related Utilities */
@CoverageIgnore
public class EnumUtils {

  /**
   * Gets optional Enum from String
   *
   * @param <T> type to use
   * @param enumClass Enum Class to use
   * @param value String to get from
   * @return Optional enum from string
   */
  @SuppressWarnings("unchecked")
  public static <T extends Enum<T>> T getEnumFromString(Class<T> enumClass, String value) {
    if (enumClass == null) {
      throw new IllegalArgumentException("cant be null");
    }

    for (Enum<?> enumValue : enumClass.getEnumConstants()) {
      if (enumValue.toString().equalsIgnoreCase(value)) {
        return (T) enumValue;
      }
    }

    StringBuilder errorMessage = new StringBuilder();
    boolean firstTime = true;
    for (Enum<?> enumValue : enumClass.getEnumConstants()) {
      errorMessage.append(firstTime ? "" : ", ").append(enumValue);
      firstTime = false;
    }
    throw new IllegalArgumentException(value + " is invalid value. only " + errorMessage);
  }

  /**
   * Gets optional Enum from String
   *
   * @param enumClass Enum Class to use
   * @param value String to get from
   * @param <T> optional
   * @return Optional enum from string
   */
  public static <T extends Enum<T>> Optional<T> getOptionalEnumFromString(
      Class<T> enumClass, String value) {
    T inst = null;
    try {
      inst = getEnumFromString(enumClass, value);
    } catch (IllegalArgumentException iae) {
      // That's OK - is optional!
    }
    return Optional.ofNullable((T) inst);
  }
}
