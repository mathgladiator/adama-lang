/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

/** error codes for things that go bump with the core package */
public class ErrorCodes {
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_FRESH_DRIVE = 1000;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_FRESH_TRANSFORM = 1001;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_INGEST_DRIVE = 1011;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_INGEST_PARTIAL = 1012;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_INGEST_DONE = 1013;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_DRIVE = 1020;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_DONE = 1021;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_LOAD = 1030;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_PARSE = 1031;
  public final static int E1_DURABLE_LIVING_DOCUMENT_STAGE_ATTACH_PRIVATE_VIEW = 1040;

  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_COMMAND_FOUND = 2000;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_UNRECOGNIZED_FIELD_PRESENT = 2001;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_ALREADY_CONNECTED = 2010;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED = 2020;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_CANT_DISCONNECT_DUE_TO_NOT_CONNECTED = 2030;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CHANNEL = 2040;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_MESSAGE = 2050;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NOT_CONNECTED = 2060;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_CANT_ATTACH_NOT_CONNECTED = 2061;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_CLIENT_REJECTED = 2070;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO = 2080;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_CONSTRUCTOR_ARG = 2081;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_TIMESTAMP = 2082;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_VALID_COMMAND_FOUND = 2083;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_ASSET = 2084;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_CANT_APPLY_NO_PATCH = 2090;

  public static final int E3_FACTORY_CANT_BIND_JAVA_CODE = 3000;
  public static final int E3_FACTORY_CANT_COMPILE_JAVA_CODE = 3001;
  public static final int E3_FACTORY_CANT_CREATE_OBJECT_DUE_TO_EXCEPTION = 3002;
}
