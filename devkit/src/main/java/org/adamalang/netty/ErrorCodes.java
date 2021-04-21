/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty;

/** error codes for things that go bump within the devkit package */
public class ErrorCodes {
  public static final int E5_CONFIGURATION_MALFORMED_NO_SOURCE_DIRECTORY = 5001;
  public static final int E5_CONFIGURATION_CRASHED = 5002;
  public static final int E5_GAMESPACE_DB_CRASHED = 5010;

  public static final int E5_REQUEST_UNKNOWN_EXCEPTION = 5050;

  public static final int E5_UNCAUGHT_EXCEPTION_WEB_HANDLER = 5500;
  public static final int E5_UNCAUGHT_EXCEPTION_WEB_SOCKET = 5501;

  public static final int USERLAND_REQUEST_HAS_NO_SESSION = 40000;
  public static final int USERLAND_RESOURCE_CANT_FIND_GAMESPACE = 40001;

  public static final int USERLAND_REQUEST_NO_GAMESPACE_PROPERTY = 40100;
  public static final int USERLAND_REQUEST_NO_METHOD_PROPERTY = 40102;
  public static final int USERLAND_REQUEST_INVALID_METHOD_PROPERTY = 40103;

  public static final int USERLAND_CANT_COMPILE_ADAMA_SCRIPT = 40200;

  public static final int DEVKIT_CANTLOAD_SCRIPT = 50000;
  public static final int DEVKIT_CANTSAVE_SCRIPT = 50001;
  public static final int DEVKIT_REQUEST_HAS_NO_SCRIPT = 50002;

}
