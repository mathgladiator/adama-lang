/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.exceptions;

/** an error happened related to an error code that we can present to the
 * public */
public class ErrorCodeException extends Exception {
  public static final int CONFIGURATION_MALFORMED_NO_SOURCE_DIRECTORY = 5003;
  public static final int FACTORY_CANT_BIND_JAVA_CODE = 5005;
  public static final int FACTORY_CANT_COMPILE_JAVA_CODE = 5004;
  public static final int FACTORY_CANT_CREATE_OBJECT_DUE_TO_EXCEPTION = 5002;
  public static final int LIVING_DOCUMENT_CRASHED = 5018;
  public static final int LIVING_DOCUMENT_TRANSACTION_ALREADY_CONNECTED = 5010;
  public static final int LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED = 5012;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_DISCONNECT_DUE_TO_NOT_CONNECTED = 5014;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CHANNEL = 5016;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_MESSAGE = 5017;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NOT_CONNECTED = 5015;
  public static final int LIVING_DOCUMENT_TRANSACTION_CLIENT_REJECTED = 5011;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO = 5008;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_COMMAND_FOUND = 5006;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_CONSTRUCTOR_ARG = 5013;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_TIMESTAMP = 5007;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_VALID_COMMAND_FOUND = 5009;
  public static final int SERVICE_UNKNOWN_FAILURE = 5500;
  public static final int SYNC_DISK_LOGGER_CANT_OPEN_APPEND = 5201;
  public static final int SYNC_DISK_LOGGER_CANT_PUMP = 5200;
  public static final int TRANSACTOR_CANT_CREATE_BECAUSE_ALREADY_CREATED = 5001;
  public static final int USERLAND_CANT_COMPILE_ADAMA_SCRIPT = 4002;
  public static final int USERLAND_CANT_FIND_GAME = 4011;
  public static final int USERLAND_CANT_CONNECT_AGAIN = 4020;
  public static final int USERLAND_CANT_FIND_GAMESPACE = 4001;
  public static final int USERLAND_GAME_ALREADY_EXISTS = 4008;
  public static final int USERLAND_INVALID_METHOD_PROPERTY = 4007;
  public static final int USERLAND_NO_CHANNEL_PROPERTY = 4009;
  public static final int USERLAND_NO_GAME_PROPERTY = 4004;
  public static final int USERLAND_NO_GAMESPACE_PROPERTY = 4003;
  public static final int USERLAND_NO_MESSAGE_PROPERTY = 4010;
  public static final int USERLAND_NO_METHOD_PROPERTY = 4006;
  public static final int USERLAND_NO_SESSION = 4005;
  public final int code;

  public ErrorCodeException(final int code) {
    this.code = code;
  }

  public ErrorCodeException(final int code, final Throwable cause) {
    super(cause);
    this.code = code;
  }
}