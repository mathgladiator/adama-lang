/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_LIMIT = 2085;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_CONSTRUCTOR_ARG = 2081;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_TIMESTAMP = 2082;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_VALID_COMMAND_FOUND = 2083;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_NO_ASSET = 2084;
  public static final int E2_LIVING_DOCUMENT_TRANSACTION_CANT_APPLY_NO_PATCH = 2090;

  public static final int E3_FACTORY_CANT_BIND_JAVA_CODE = 3000;
  public static final int E3_FACTORY_CANT_COMPILE_JAVA_CODE = 3001;
  public static final int E3_FACTORY_CANT_CREATE_OBJECT_DUE_TO_EXCEPTION = 3002;

  public static final int E4_CREATE_ALREADY_EXISTS = 4009;
  public static final int E4_LOAD_ALREADY_EXISTS = 4010;
  public static final int E4_FAILED_FIND_DOCUMENT = 4011;

  public static final int E5_CATASTROPHIC_DOCUMENT_FAILURE_EXCEPTION = 9601;
  public static final int USERLAND_CANT_COMPILE_ADAMA_SCRIPT = 40200;

  public static final int E6_DOCUMENT_ALREADY_CREATED = 5990;
  public static final int E7_MESSAGE_ALREADY_SENT = 99922;
  public static final int E7_EXPIRE_LIMIT_MUST_BE_POSITIVE = 99923;

}
