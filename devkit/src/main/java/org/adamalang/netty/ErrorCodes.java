/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty;

/** error codes for things that go bump within the devkit package */
public class ErrorCodes {
  public static final int E5_CONFIGURATION_MALFORMED_NO_SOURCE_DIRECTORY = 5001;
  public static final int E5_CONFIGURATION_CRASHED = 5002;
  public static final int E5_GAMESPACE_DB_CRASHED = 5010;

  public static final int E5_REQUEST_UNKNOWN_EXCEPTION = 5050;

  public static final int E5_REQUEST_GENERATE_CRASHED = 5021;
  public static final int E5_REQUEST_CREATE_CRASHED = 5022;
  public static final int E5_REQUEST_CONNECT_CRASHED_GET = 5023;
  public static final int E5_REQUEST_CONNECT_CRASHED_PV = 5024;
  public static final int E5_REQUEST_CONNECT_CRASHED_GC = 5025;
  public static final int E5_REQUEST_CONNECT_CRASHED_POST_CONNECT = 5026;
  public static final int E5_REQUEST_CONNECT_CRASHED_CONNECT = 5027;
  public static final int E5_REQUEST_DISCONNECT_CRASHED = 5028;
  public static final int E5_REQUEST_SEND_CRASHED = 5029;
  public static final int E5_REQUEST_SEND_CRASHED_ACTUAL = 5030;

  public static final int E5_UNCAUGHT_EXCEPTION_WEB_HANDLER = 5500;
  public static final int E5_UNCAUGHT_EXCEPTION_WEB_SOCKET = 5501;

  public static final int USERLAND_REQUEST_HAS_NO_SESSION = 40000;
  public static final int USERLAND_RESOURCE_CANT_FIND_GAMESPACE = 40001;
  public static final int USERLAND_SESSION_CANT_CONNECT_AGAIN = 40002;

  public static final int USERLAND_REQUEST_NO_GAMESPACE_PROPERTY = 40100;
  public static final int USERLAND_REQUEST_NO_GAME_PROPERTY = 40101;
  public static final int USERLAND_REQUEST_NO_METHOD_PROPERTY = 40102;
  public static final int USERLAND_REQUEST_INVALID_METHOD_PROPERTY = 40103;
  public static final int USERLAND_REQUEST_NO_CHANNEL_PROPERTY = 40104;
  public static final int USERLAND_REQUEST_NO_MESSAGE_PROPERTY = 40105;
  public static final int USERLAND_REQUEST_NO_CONSTRUCTOR_ARG = 40106;

  public static final int USERLAND_CANT_COMPILE_ADAMA_SCRIPT = 40200;

  public static final int DEVKIT_CANTLOAD_SCRIPT = 50000;
  public static final int DEVKIT_CANTSAVE_SCRIPT = 50001;
  public static final int DEVKIT_REQUEST_HAS_NO_SCRIPT = 50002;

}
