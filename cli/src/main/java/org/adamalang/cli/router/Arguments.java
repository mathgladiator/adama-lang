/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli.router;

import org.adamalang.common.ANSI;
import org.adamalang.cli.Util;
import org.adamalang.cli.Config;

public class Arguments {
	public static class SpaceCreateArgs {
		public Config config;
		public String space;
		public static SpaceCreateArgs from(String[] args, int start) {
			SpaceCreateArgs returnArgs = new SpaceCreateArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Creates a new space", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space create", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
		}
	}
	public static class SpaceDeleteArgs {
		public Config config;
		public String space;
		public static SpaceDeleteArgs from(String[] args, int start) {
			SpaceDeleteArgs returnArgs = new SpaceDeleteArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Deletes an empty space", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space delete", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
		}
	}
	public static class SpaceDeployArgs {
		public Config config;
		public String space;
		public String plan = null;
		public String file = null;
		public static SpaceDeployArgs from(String[] args, int start) {
			SpaceDeployArgs returnArgs = new SpaceDeployArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-p":
					case "--plan": {
						if (k+1 < args.length) {
							returnArgs.plan = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-f":
					case "--file": {
						if (k+1 < args.length) {
							returnArgs.file = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Deploy a plan to a space", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space deploy", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-p, --plan", ANSI.Green) + " " + Util.prefix("<plan>", ANSI.White) + " : A deployment plan; see https://book.adama-platform.com/reference/deployment-plan.html .");
			System.out.println("    " + Util.prefix("-f, --file", ANSI.Green) + " " + Util.prefix("<file>", ANSI.White) + " : A file.");
		}
	}
	public static class SpaceDevelopersArgs {
		public Config config;
		public String space;
		public static SpaceDevelopersArgs from(String[] args, int start) {
			SpaceDevelopersArgs returnArgs = new SpaceDevelopersArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("List developers for the given space", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space developers", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
		}
	}
	public static class SpaceEncryptPrivArgs {
		public Config config;
		public String space;
		public String priv = "private.key.json";
		public static SpaceEncryptPrivArgs from(String[] args, int start) {
			SpaceEncryptPrivArgs returnArgs = new SpaceEncryptPrivArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-p":
					case "--priv": {
						if (k+1 < args.length) {
							returnArgs.priv = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Encrypt a private key to store within code", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space encrypt-priv", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-p, --priv", ANSI.Green) + " " + Util.prefix("<priv>", ANSI.White) + " : A special JSON encoded private key");
		}
	}
	public static class SpaceEncryptSecretArgs {
		public Config config;
		public String space;
		public static SpaceEncryptSecretArgs from(String[] args, int start) {
			SpaceEncryptSecretArgs returnArgs = new SpaceEncryptSecretArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Encrypt a secret to store within code", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space encrypt-secret", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
		}
	}
	public static class SpaceGenerateKeyArgs {
		public Config config;
		public String space;
		public static SpaceGenerateKeyArgs from(String[] args, int start) {
			SpaceGenerateKeyArgs returnArgs = new SpaceGenerateKeyArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Generate a server-side key to use for storing secrets", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space generate-key", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
		}
	}
	public static class SpaceGetArgs {
		public Config config;
		public String space;
		public String output;
		public static SpaceGetArgs from(String[] args, int start) {
			SpaceGetArgs returnArgs = new SpaceGetArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--output", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Get a space's plan", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space get", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class SpaceGetPolicyArgs {
		public Config config;
		public String space;
		public String output;
		public static SpaceGetPolicyArgs from(String[] args, int start) {
			SpaceGetPolicyArgs returnArgs = new SpaceGetPolicyArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--output", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Get the access control policy for the space", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space get-policy", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class SpaceGetRxhtmlArgs {
		public Config config;
		public String space;
		public String output;
		public static SpaceGetRxhtmlArgs from(String[] args, int start) {
			SpaceGetRxhtmlArgs returnArgs = new SpaceGetRxhtmlArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--output", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Get the frontend RxHTML forest", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space get-rxhtml", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class SpaceListArgs {
		public Config config;
		public String marker = "";
		public String limit = "100";
		public static SpaceListArgs from(String[] args, int start) {
			SpaceListArgs returnArgs = new SpaceListArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-m":
					case "--marker": {
						if (k+1 < args.length) {
							returnArgs.marker = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-l":
					case "--limit": {
						if (k+1 < args.length) {
							returnArgs.limit = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("List spaces available to your account", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space list", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-m, --marker", ANSI.Green) + " " + Util.prefix("<marker>", ANSI.White) + " : Items greater than the marker are returned.");
			System.out.println("    " + Util.prefix("-l, --limit", ANSI.Green) + " " + Util.prefix("<limit>", ANSI.White) + " : Limit the returned items.");
		}
	}
	public static class SpaceMetricsArgs {
		public Config config;
		public String space;
		public String prefix = null;
		public static SpaceMetricsArgs from(String[] args, int start) {
			SpaceMetricsArgs returnArgs = new SpaceMetricsArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-p":
					case "--prefix": {
						if (k+1 < args.length) {
							returnArgs.prefix = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Get a metric report for the space and the documents that share the prefix", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space metrics", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-p, --prefix", ANSI.Green) + " " + Util.prefix("<prefix>", ANSI.White) + " : Items that have this prefixed are included");
		}
	}
	public static class SpaceReflectArgs {
		public Config config;
		public String space;
		public String key = null;
		public String output;
		public static SpaceReflectArgs from(String[] args, int start) {
			SpaceReflectArgs returnArgs = new SpaceReflectArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--output", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Get a file of the reflection of a space", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space reflect", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-k, --key", ANSI.Green) + " " + Util.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
		}
	}
	public static class SpaceSetPolicyArgs {
		public Config config;
		public String space;
		public String file;
		public static SpaceSetPolicyArgs from(String[] args, int start) {
			SpaceSetPolicyArgs returnArgs = new SpaceSetPolicyArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--file", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-f":
					case "--file": {
						if (k+1 < args.length) {
							returnArgs.file = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Set the space's access control policy", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space set-policy", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-f, --file", ANSI.Green) + " " + Util.prefix("<file>", ANSI.White) + " : A file.");
		}
	}
	public static class SpaceSetRoleArgs {
		public Config config;
		public String space;
		public String email;
		public String role = "none";
		public static SpaceSetRoleArgs from(String[] args, int start) {
			SpaceSetRoleArgs returnArgs = new SpaceSetRoleArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--email", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-e":
					case "--email": {
						if (k+1 < args.length) {
							returnArgs.email = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-r":
					case "--role": {
						if (k+1 < args.length) {
							returnArgs.role = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Set the role of another developer", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space set-role", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-e, --email", ANSI.Green) + " " + Util.prefix("<email>", ANSI.White) + " : An email address.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-r, --role", ANSI.Green) + " " + Util.prefix("<role>", ANSI.White) + " : Options are 'developer' or 'none'.");
		}
	}
	public static class SpaceSetRxhtmlArgs {
		public Config config;
		public String space;
		public String file;
		public static SpaceSetRxhtmlArgs from(String[] args, int start) {
			SpaceSetRxhtmlArgs returnArgs = new SpaceSetRxhtmlArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--file", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-f":
					case "--file": {
						if (k+1 < args.length) {
							returnArgs.file = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Set the frontend RxHTML forest", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space set-rxhtml", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-f, --file", ANSI.Green) + " " + Util.prefix("<file>", ANSI.White) + " : A file.");
		}
	}
	public static class SpaceUploadArgs {
		public Config config;
		public String space;
		public String gc = "no";
		public String file = null;
		public String directory = null;
		public static SpaceUploadArgs from(String[] args, int start) {
			SpaceUploadArgs returnArgs = new SpaceUploadArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-g":
					case "--gc": {
						if (k+1 < args.length) {
							returnArgs.gc = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-f":
					case "--file": {
						if (k+1 < args.length) {
							returnArgs.file = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-d":
					case "--directory": {
						if (k+1 < args.length) {
							returnArgs.directory = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Placeholder", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama space upload", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-g, --gc", ANSI.Green) + " " + Util.prefix("<gc>", ANSI.White) + " : Delete assets that were not present in this upload.");
			System.out.println("    " + Util.prefix("-f, --file", ANSI.Green) + " " + Util.prefix("<file>", ANSI.White) + " : A file.");
			System.out.println("    " + Util.prefix("-d, --directory", ANSI.Green) + " " + Util.prefix("<directory>", ANSI.White) + " : A directory.");
		}
	}
	public static class AuthorityAppendLocalArgs {
		public Config config;
		public String authority;
		public String keystore = "keystore.json";
		public String priv;
		public static AuthorityAppendLocalArgs from(String[] args, int start) {
			AuthorityAppendLocalArgs returnArgs = new AuthorityAppendLocalArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--authority", "--priv", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-a":
					case "--authority": {
						if (k+1 < args.length) {
							returnArgs.authority = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--keystore": {
						if (k+1 < args.length) {
							returnArgs.keystore = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-p":
					case "--priv": {
						if (k+1 < args.length) {
							returnArgs.priv = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Append a new public key to the public key file", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama authority append-local", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-a, --authority", ANSI.Green) + " " + Util.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
			System.out.println("    " + Util.prefix("-p, --priv", ANSI.Green) + " " + Util.prefix("<priv>", ANSI.White) + " : A special JSON encoded private key");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-k, --keystore", ANSI.Green) + " " + Util.prefix("<keystore>", ANSI.White) + " : A special JSON encoded keystore holding only public keys.");
		}
	}
	public static class AuthorityCreateArgs {
		public Config config;
		public static AuthorityCreateArgs from(String[] args, int start) {
			AuthorityCreateArgs returnArgs = new AuthorityCreateArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Creates a new authority", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama authority create", ANSI.Green));
		}
	}
	public static class AuthorityCreateLocalArgs {
		public Config config;
		public String authority;
		public String keystore = "keystore.json";
		public String priv = "private.key.json";
		public static AuthorityCreateLocalArgs from(String[] args, int start) {
			AuthorityCreateLocalArgs returnArgs = new AuthorityCreateLocalArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--authority", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-a":
					case "--authority": {
						if (k+1 < args.length) {
							returnArgs.authority = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--keystore": {
						if (k+1 < args.length) {
							returnArgs.keystore = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-p":
					case "--priv": {
						if (k+1 < args.length) {
							returnArgs.priv = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Make a new set of public keys", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama authority create-local", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-a, --authority", ANSI.Green) + " " + Util.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-k, --keystore", ANSI.Green) + " " + Util.prefix("<keystore>", ANSI.White) + " : A special JSON encoded keystore holding only public keys.");
			System.out.println("    " + Util.prefix("-p, --priv", ANSI.Green) + " " + Util.prefix("<priv>", ANSI.White) + " : A special JSON encoded private key");
		}
	}
	public static class AuthorityDestroyArgs {
		public Config config;
		public String authority;
		public static AuthorityDestroyArgs from(String[] args, int start) {
			AuthorityDestroyArgs returnArgs = new AuthorityDestroyArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--authority", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-a":
					case "--authority": {
						if (k+1 < args.length) {
							returnArgs.authority = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Destroy an authority", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama authority destroy", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-a, --authority", ANSI.Green) + " " + Util.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
		}
	}
	public static class AuthorityGetArgs {
		public Config config;
		public String authority;
		public String keystore;
		public static AuthorityGetArgs from(String[] args, int start) {
			AuthorityGetArgs returnArgs = new AuthorityGetArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--authority", "--keystore", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-a":
					case "--authority": {
						if (k+1 < args.length) {
							returnArgs.authority = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--keystore": {
						if (k+1 < args.length) {
							returnArgs.keystore = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Get released public keys for an authority", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama authority get", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-a, --authority", ANSI.Green) + " " + Util.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
			System.out.println("    " + Util.prefix("-k, --keystore", ANSI.Green) + " " + Util.prefix("<keystore>", ANSI.White) + " : A special JSON encoded keystore holding only public keys.");
		}
	}
	public static class AuthorityListArgs {
		public Config config;
		public static AuthorityListArgs from(String[] args, int start) {
			AuthorityListArgs returnArgs = new AuthorityListArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("List authorities this developer owns", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama authority list", ANSI.Green));
		}
	}
	public static class AuthoritySetArgs {
		public Config config;
		public String authority;
		public String keystore;
		public static AuthoritySetArgs from(String[] args, int start) {
			AuthoritySetArgs returnArgs = new AuthoritySetArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--authority", "--keystore", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-a":
					case "--authority": {
						if (k+1 < args.length) {
							returnArgs.authority = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--keystore": {
						if (k+1 < args.length) {
							returnArgs.keystore = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Set public keys to an authority", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama authority set", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-a, --authority", ANSI.Green) + " " + Util.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
			System.out.println("    " + Util.prefix("-k, --keystore", ANSI.Green) + " " + Util.prefix("<keystore>", ANSI.White) + " : A special JSON encoded keystore holding only public keys.");
		}
	}
	public static class AuthoritySignArgs {
		public Config config;
		public String key;
		public String agent;
		public String validate = null;
		public static AuthoritySignArgs from(String[] args, int start) {
			AuthoritySignArgs returnArgs = new AuthoritySignArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--key", "--agent", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-ag":
					case "--agent": {
						if (k+1 < args.length) {
							returnArgs.agent = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-v":
					case "--validate": {
						if (k+1 < args.length) {
							returnArgs.validate = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Sign an agent with a local private key", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama authority sign", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-k, --key", ANSI.Green) + " " + Util.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + Util.prefix("-ag, --agent", ANSI.Green) + " " + Util.prefix("<agent>", ANSI.White) + " : The user id or agent part of a principal.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-v, --validate", ANSI.Green) + " " + Util.prefix("<validate>", ANSI.White) + " : Should the plan be validated.");
		}
	}
	public static class AccountSetPasswordArgs {
		public Config config;
		public static AccountSetPasswordArgs from(String[] args, int start) {
			AccountSetPasswordArgs returnArgs = new AccountSetPasswordArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Create a password to be used on web", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama account set-password", ANSI.Green));
		}
	}
	public static class CodeBundlePlanArgs {
		public Config config;
		public String output;
		public String main;
		public String instrument = "false";
		public String imports = "backend";
		public static CodeBundlePlanArgs from(String[] args, int start) {
			CodeBundlePlanArgs returnArgs = new CodeBundlePlanArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--output", "--main", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-m":
					case "--main": {
						if (k+1 < args.length) {
							returnArgs.main = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-in":
					case "--instrument": {
						if (k+1 < args.length) {
							returnArgs.instrument = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-i":
					case "--imports": {
						if (k+1 < args.length) {
							returnArgs.imports = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Bundle the main and imports into a single deployment plan.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama code bundle-plan", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + Util.prefix("-m, --main", ANSI.Green) + " " + Util.prefix("<main>", ANSI.White) + " : The main/primary adama file.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-in, --instrument", ANSI.Green) + " " + Util.prefix("<instrument>", ANSI.White) + " : Instrument the plan");
			System.out.println("    " + Util.prefix("-i, --imports", ANSI.Green) + " " + Util.prefix("<imports>", ANSI.White) + " : A directory containing adama files to import into the main");
		}
	}
	public static class CodeCompileFileArgs {
		public Config config;
		public String file;
		public String imports = "backend";
		public String dumpTo = null;
		public static CodeCompileFileArgs from(String[] args, int start) {
			CodeCompileFileArgs returnArgs = new CodeCompileFileArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--file", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-f":
					case "--file": {
						if (k+1 < args.length) {
							returnArgs.file = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-i":
					case "--imports": {
						if (k+1 < args.length) {
							returnArgs.imports = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-d":
					case "--dump-to": {
						if (k+1 < args.length) {
							returnArgs.dumpTo = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Compiles the adama file and shows any problems", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama code compile-file", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-f, --file", ANSI.Green) + " " + Util.prefix("<file>", ANSI.White) + " : A file.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --imports", ANSI.Green) + " " + Util.prefix("<imports>", ANSI.White) + " : A directory containing adama files to import into the main");
			System.out.println("    " + Util.prefix("-d, --dump-to", ANSI.Green) + " " + Util.prefix("<dump-to>", ANSI.White) + " : Placeholder");
		}
	}
	public static class CodeDiagramArgs {
		public Config config;
		public String input = "reflected.json";
		public String output = "mermaid.mmd";
		public String title = "Schema";
		public static CodeDiagramArgs from(String[] args, int start) {
			CodeDiagramArgs returnArgs = new CodeDiagramArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-tt":
					case "--title": {
						if (k+1 < args.length) {
							returnArgs.title = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Convert a reflection JSON into a mermaid diagram source", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama code diagram", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + Util.prefix("-tt, --title", ANSI.Green) + " " + Util.prefix("<title>", ANSI.White) + " : The title of the diagram");
		}
	}
	public static class CodeFormatArgs {
		public Config config;
		public String file = null;
		public static CodeFormatArgs from(String[] args, int start) {
			CodeFormatArgs returnArgs = new CodeFormatArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-f":
					case "--file": {
						if (k+1 < args.length) {
							returnArgs.file = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Format the file or directory recursively (and inline updates)", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama code format", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-f, --file", ANSI.Green) + " " + Util.prefix("<file>", ANSI.White) + " : A file.");
		}
	}
	public static class CodeLspArgs {
		public Config config;
		public String port = "2423";
		public static CodeLspArgs from(String[] args, int start) {
			CodeLspArgs returnArgs = new CodeLspArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-p":
					case "--port": {
						if (k+1 < args.length) {
							returnArgs.port = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Spin up a single threaded language service protocol server", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama code lsp", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-p, --port", ANSI.Green) + " " + Util.prefix("<port>", ANSI.White) + " : Placeholder");
		}
	}
	public static class CodeReflectDumpArgs {
		public Config config;
		public String file;
		public String imports = "backend";
		public String dumpTo = null;
		public static CodeReflectDumpArgs from(String[] args, int start) {
			CodeReflectDumpArgs returnArgs = new CodeReflectDumpArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--file", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-f":
					case "--file": {
						if (k+1 < args.length) {
							returnArgs.file = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-i":
					case "--imports": {
						if (k+1 < args.length) {
							returnArgs.imports = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-d":
					case "--dump-to": {
						if (k+1 < args.length) {
							returnArgs.dumpTo = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Compiles the adama file and dumps the reflection json", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama code reflect-dump", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-f, --file", ANSI.Green) + " " + Util.prefix("<file>", ANSI.White) + " : A file.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --imports", ANSI.Green) + " " + Util.prefix("<imports>", ANSI.White) + " : A directory containing adama files to import into the main");
			System.out.println("    " + Util.prefix("-d, --dump-to", ANSI.Green) + " " + Util.prefix("<dump-to>", ANSI.White) + " : Placeholder");
		}
	}
	public static class CodeValidatePlanArgs {
		public Config config;
		public String plan;
		public static CodeValidatePlanArgs from(String[] args, int start) {
			CodeValidatePlanArgs returnArgs = new CodeValidatePlanArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--plan", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-p":
					case "--plan": {
						if (k+1 < args.length) {
							returnArgs.plan = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Validates a deployment plan (locally) for speed", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama code validate-plan", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-p, --plan", ANSI.Green) + " " + Util.prefix("<plan>", ANSI.White) + " : A deployment plan; see https://book.adama-platform.com/reference/deployment-plan.html .");
		}
	}
	public static class ContribBundleJsArgs {
		public Config config;
		public static ContribBundleJsArgs from(String[] args, int start) {
			ContribBundleJsArgs returnArgs = new ContribBundleJsArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Bundles the libadama.js into the webserver", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib bundle-js", ANSI.Green));
		}
	}
	public static class ContribCopyrightArgs {
		public Config config;
		public static ContribCopyrightArgs from(String[] args, int start) {
			ContribCopyrightArgs returnArgs = new ContribCopyrightArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Sprinkle the copyright everywhere.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib copyright", ANSI.Green));
		}
	}
	public static class ContribMakeApiArgs {
		public Config config;
		public static ContribMakeApiArgs from(String[] args, int start) {
			ContribMakeApiArgs returnArgs = new ContribMakeApiArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Produces api files for SaaS and documentation for the WebSocket low level API.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib make-api", ANSI.Green));
		}
	}
	public static class ContribMakeBookArgs {
		public Config config;
		public String input;
		public String output;
		public String bookTemplate;
		public String bookMerge;
		public static ContribMakeBookArgs from(String[] args, int start) {
			ContribMakeBookArgs returnArgs = new ContribMakeBookArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--input", "--output", "--book-template", "--book-merge", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-bt":
					case "--book-template": {
						if (k+1 < args.length) {
							returnArgs.bookTemplate = args[k+1];
							k++;
							missing[2] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-bm":
					case "--book-merge": {
						if (k+1 < args.length) {
							returnArgs.bookMerge = args[k+1];
							k++;
							missing[3] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Compile Adama's Book", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib make-book", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + Util.prefix("-bt, --book-template", ANSI.Green) + " " + Util.prefix("<book-template>", ANSI.White) + " : Template for generating the book");
			System.out.println("    " + Util.prefix("-bm, --book-merge", ANSI.Green) + " " + Util.prefix("<book-merge>", ANSI.White) + " : Files to merge into the book");
		}
	}
	public static class ContribMakeCliArgs {
		public Config config;
		public static ContribMakeCliArgs from(String[] args, int start) {
			ContribMakeCliArgs returnArgs = new ContribMakeCliArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Generate the command line router", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib make-cli", ANSI.Green));
		}
	}
	public static class ContribMakeCodecArgs {
		public Config config;
		public static ContribMakeCodecArgs from(String[] args, int start) {
			ContribMakeCodecArgs returnArgs = new ContribMakeCodecArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Generates the networking codec", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib make-codec", ANSI.Green));
		}
	}
	public static class ContribMakeEmbedArgs {
		public Config config;
		public static ContribMakeEmbedArgs from(String[] args, int start) {
			ContribMakeEmbedArgs returnArgs = new ContribMakeEmbedArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Generates the embedded templates", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib make-embed", ANSI.Green));
		}
	}
	public static class ContribMakeEtArgs {
		public Config config;
		public static ContribMakeEtArgs from(String[] args, int start) {
			ContribMakeEtArgs returnArgs = new ContribMakeEtArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Generates the error table which provides useful insight to issues", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib make-et", ANSI.Green));
		}
	}
	public static class ContribStrTempArgs {
		public Config config;
		public static ContribStrTempArgs from(String[] args, int start) {
			ContribStrTempArgs returnArgs = new ContribStrTempArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Generate string templates", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib str-temp", ANSI.Green));
		}
	}
	public static class ContribTestsAdamaArgs {
		public Config config;
		public String input = "./test_code";
		public String output = "./src/test/java/org/adamalang/translator";
		public String errors = "./error-messages.csv";
		public static ContribTestsAdamaArgs from(String[] args, int start) {
			ContribTestsAdamaArgs returnArgs = new ContribTestsAdamaArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-e":
					case "--errors": {
						if (k+1 < args.length) {
							returnArgs.errors = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Generate tests for Adama Language.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib tests-adama", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + Util.prefix("-e, --errors", ANSI.Green) + " " + Util.prefix("<errors>", ANSI.White) + " : Placeholder");
		}
	}
	public static class ContribTestsRxhtmlArgs {
		public Config config;
		public String input = "./test_templates";
		public String output = "./src/test/java/org/adamalang/rxhtml";
		public static ContribTestsRxhtmlArgs from(String[] args, int start) {
			ContribTestsRxhtmlArgs returnArgs = new ContribTestsRxhtmlArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Generate tests for RxHTML.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib tests-rxhtml", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class ContribVersionArgs {
		public Config config;
		public static ContribVersionArgs from(String[] args, int start) {
			ContribVersionArgs returnArgs = new ContribVersionArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Create the version number for the platform", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama contrib version", ANSI.Green));
		}
	}
	public static class DatabaseConfigureArgs {
		public Config config;
		public static DatabaseConfigureArgs from(String[] args, int start) {
			DatabaseConfigureArgs returnArgs = new DatabaseConfigureArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Update the configuration", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama database configure", ANSI.Green));
		}
	}
	public static class DatabaseInstallArgs {
		public Config config;
		public static DatabaseInstallArgs from(String[] args, int start) {
			DatabaseInstallArgs returnArgs = new DatabaseInstallArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Install the tables on a monolithic database", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama database install", ANSI.Green));
		}
	}
	public static class DatabaseMakeReservedArgs {
		public Config config;
		public String email;
		public String space;
		public static DatabaseMakeReservedArgs from(String[] args, int start) {
			DatabaseMakeReservedArgs returnArgs = new DatabaseMakeReservedArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--email", "--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-e":
					case "--email": {
						if (k+1 < args.length) {
							returnArgs.email = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Create reserved spaces", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama database make-reserved", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-e, --email", ANSI.Green) + " " + Util.prefix("<email>", ANSI.White) + " : An email address.");
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
		}
	}
	public static class DatabaseMigrateArgs {
		public Config config;
		public static DatabaseMigrateArgs from(String[] args, int start) {
			DatabaseMigrateArgs returnArgs = new DatabaseMigrateArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Migrate data from 'db' to 'nextdb'", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama database migrate", ANSI.Green));
		}
	}
	public static class DocumentAttachArgs {
		public Config config;
		public String space;
		public String key;
		public String file;
		public String name = null;
		public String type = null;
		public static DocumentAttachArgs from(String[] args, int start) {
			DocumentAttachArgs returnArgs = new DocumentAttachArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--key", "--file", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-f":
					case "--file": {
						if (k+1 < args.length) {
							returnArgs.file = args[k+1];
							k++;
							missing[2] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-n":
					case "--name": {
						if (k+1 < args.length) {
							returnArgs.name = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-t":
					case "--type": {
						if (k+1 < args.length) {
							returnArgs.type = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Attach an asset to a document", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama document attach", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-k, --key", ANSI.Green) + " " + Util.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + Util.prefix("-f, --file", ANSI.Green) + " " + Util.prefix("<file>", ANSI.White) + " : A file.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-n, --name", ANSI.Green) + " " + Util.prefix("<name>", ANSI.White) + " : Placeholder");
			System.out.println("    " + Util.prefix("-t, --type", ANSI.Green) + " " + Util.prefix("<type>", ANSI.White) + " : Placeholder");
		}
	}
	public static class DocumentConnectArgs {
		public Config config;
		public String space;
		public String key;
		public static DocumentConnectArgs from(String[] args, int start) {
			DocumentConnectArgs returnArgs = new DocumentConnectArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--key", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Connect to a document", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama document connect", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-k, --key", ANSI.Green) + " " + Util.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
		}
	}
	public static class DocumentCreateArgs {
		public Config config;
		public String space;
		public String key;
		public String arg;
		public String entropy = null;
		public static DocumentCreateArgs from(String[] args, int start) {
			DocumentCreateArgs returnArgs = new DocumentCreateArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--key", "--arg", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-aa":
					case "--arg": {
						if (k+1 < args.length) {
							returnArgs.arg = args[k+1];
							k++;
							missing[2] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-e":
					case "--entropy": {
						if (k+1 < args.length) {
							returnArgs.entropy = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Create a document", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama document create", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-k, --key", ANSI.Green) + " " + Util.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + Util.prefix("-aa, --arg", ANSI.Green) + " " + Util.prefix("<arg>", ANSI.White) + " : The constructor argument.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-e, --entropy", ANSI.Green) + " " + Util.prefix("<entropy>", ANSI.White) + " : A random seed.");
		}
	}
	public static class DocumentDeleteArgs {
		public Config config;
		public String space;
		public String key;
		public static DocumentDeleteArgs from(String[] args, int start) {
			DocumentDeleteArgs returnArgs = new DocumentDeleteArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--key", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Delete a document", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama document delete", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-k, --key", ANSI.Green) + " " + Util.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
		}
	}
	public static class DocumentDownloadArchiveArgs {
		public Config config;
		public String space;
		public String key;
		public String output;
		public static DocumentDownloadArchiveArgs from(String[] args, int start) {
			DocumentDownloadArchiveArgs returnArgs = new DocumentDownloadArchiveArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--key", "--output", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[2] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Download the latest archive backup", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama document download-archive", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + Util.prefix("-k, --key", ANSI.Green) + " " + Util.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class DocumentListArgs {
		public Config config;
		public String space;
		public String marker = null;
		public String limit = "1000";
		public static DocumentListArgs from(String[] args, int start) {
			DocumentListArgs returnArgs = new DocumentListArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-m":
					case "--marker": {
						if (k+1 < args.length) {
							returnArgs.marker = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-l":
					case "--limit": {
						if (k+1 < args.length) {
							returnArgs.limit = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("List documents", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama document list", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-m, --marker", ANSI.Green) + " " + Util.prefix("<marker>", ANSI.White) + " : Items greater than the marker are returned.");
			System.out.println("    " + Util.prefix("-l, --limit", ANSI.Green) + " " + Util.prefix("<limit>", ANSI.White) + " : Limit the returned items.");
		}
	}
	public static class OpsCompactArgs {
		public Config config;
		public String input;
		public String output;
		public static OpsCompactArgs from(String[] args, int start) {
			OpsCompactArgs returnArgs = new OpsCompactArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--input", "--output", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Compact an archive to a single JSON file", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama ops compact", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class OpsExplainArgs {
		public Config config;
		public String input;
		public String jquery;
		public static OpsExplainArgs from(String[] args, int start) {
			OpsExplainArgs returnArgs = new OpsExplainArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--input", "--jquery", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-j":
					case "--jquery": {
						if (k+1 < args.length) {
							returnArgs.jquery = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Explain the history of a value at a path", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama ops explain", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-j, --jquery", ANSI.Green) + " " + Util.prefix("<jquery>", ANSI.White) + " : A json path (field0.field1....fieldN)");
		}
	}
	public static class DomainConfigureArgs {
		public Config config;
		public String domain;
		public String product;
		public static DomainConfigureArgs from(String[] args, int start) {
			DomainConfigureArgs returnArgs = new DomainConfigureArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--domain", "--product", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-d":
					case "--domain": {
						if (k+1 < args.length) {
							returnArgs.domain = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-p":
					case "--product": {
						if (k+1 < args.length) {
							returnArgs.product = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Provide a product configuration to define various aspects of a product by domain", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama domain configure", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-d, --domain", ANSI.Green) + " " + Util.prefix("<domain>", ANSI.White) + " : The domain name");
			System.out.println("    " + Util.prefix("-p, --product", ANSI.Green) + " " + Util.prefix("<product>", ANSI.White) + " : Product configuration for native apps on the platform");
		}
	}
	public static class DomainListArgs {
		public Config config;
		public static DomainListArgs from(String[] args, int start) {
			DomainListArgs returnArgs = new DomainListArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("List domains", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama domain list", ANSI.Green));
		}
	}
	public static class DomainMapArgs {
		public Config config;
		public String domain;
		public String space;
		public String cert = null;
		public String key = null;
		public String route = "false";
		public String auto = "true";
		public static DomainMapArgs from(String[] args, int start) {
			DomainMapArgs returnArgs = new DomainMapArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--domain", "--space", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-d":
					case "--domain": {
						if (k+1 < args.length) {
							returnArgs.domain = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-s":
					case "--space": {
						if (k+1 < args.length) {
							returnArgs.space = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-c":
					case "--cert": {
						if (k+1 < args.length) {
							returnArgs.cert = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-re":
					case "--route": {
						if (k+1 < args.length) {
							returnArgs.route = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-a":
					case "--auto": {
						if (k+1 < args.length) {
							returnArgs.auto = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Map a domain to a space", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama domain map", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-d, --domain", ANSI.Green) + " " + Util.prefix("<domain>", ANSI.White) + " : The domain name");
			System.out.println("    " + Util.prefix("-s, --space", ANSI.Green) + " " + Util.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-c, --cert", ANSI.Green) + " " + Util.prefix("<cert>", ANSI.White) + " : Placeholder");
			System.out.println("    " + Util.prefix("-k, --key", ANSI.Green) + " " + Util.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + Util.prefix("-re, --route", ANSI.Green) + " " + Util.prefix("<route>", ANSI.White) + " : Should the domain route to the key's handler.");
			System.out.println("    " + Util.prefix("-a, --auto", ANSI.Green) + " " + Util.prefix("<auto>", ANSI.White) + " : Should the Adama Platform automatically get a domain.");
		}
	}
	public static class DomainUnmapArgs {
		public Config config;
		public String domain;
		public static DomainUnmapArgs from(String[] args, int start) {
			DomainUnmapArgs returnArgs = new DomainUnmapArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--domain", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-d":
					case "--domain": {
						if (k+1 < args.length) {
							returnArgs.domain = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Unmap a domain from a space", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama domain unmap", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-d, --domain", ANSI.Green) + " " + Util.prefix("<domain>", ANSI.White) + " : The domain name");
		}
	}
	public static class FrontendBundleArgs {
		public Config config;
		public String rxhtmlPath = "frontend";
		public String output = "frontend.rx.html";
		public static FrontendBundleArgs from(String[] args, int start) {
			FrontendBundleArgs returnArgs = new FrontendBundleArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-r":
					case "--rxhtml-path": {
						if (k+1 < args.length) {
							returnArgs.rxhtmlPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Bundle many *.rx.html into one big one.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend bundle", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-r, --rxhtml-path", ANSI.Green) + " " + Util.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class FrontendDecryptProductConfigArgs {
		public Config config;
		public String input = "product.config.json.encrypted";
		public String output = "product.config.json";
		public static FrontendDecryptProductConfigArgs from(String[] args, int start) {
			FrontendDecryptProductConfigArgs returnArgs = new FrontendDecryptProductConfigArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Decrypt product config", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend decrypt-product-config", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class FrontendDevServerArgs {
		public Config config;
		public String rxhtmlPath = "frontend";
		public String assetPath = "assets";
		public String microverse = "local.verse.json";
		public String debugger = "true";
		public String localLibadamaPath = null;
		public String environment = "test";
		public String preserveView = "true";
		public String types = "types";
		public static FrontendDevServerArgs from(String[] args, int start) {
			FrontendDevServerArgs returnArgs = new FrontendDevServerArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-r":
					case "--rxhtml-path": {
						if (k+1 < args.length) {
							returnArgs.rxhtmlPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-a":
					case "--asset-path": {
						if (k+1 < args.length) {
							returnArgs.assetPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-mv":
					case "--microverse": {
						if (k+1 < args.length) {
							returnArgs.microverse = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-dbg":
					case "--debugger": {
						if (k+1 < args.length) {
							returnArgs.debugger = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-lap":
					case "--local-libadama-path": {
						if (k+1 < args.length) {
							returnArgs.localLibadamaPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-e":
					case "--environment": {
						if (k+1 < args.length) {
							returnArgs.environment = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-pv":
					case "--preserve-view": {
						if (k+1 < args.length) {
							returnArgs.preserveView = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-ty":
					case "--types": {
						if (k+1 < args.length) {
							returnArgs.types = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Host the working directory as a webserver", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend dev-server", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-r, --rxhtml-path", ANSI.Green) + " " + Util.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + Util.prefix("-a, --asset-path", ANSI.Green) + " " + Util.prefix("<asset-path>", ANSI.White) + " : The path to map for static assets.");
			System.out.println("    " + Util.prefix("-mv, --microverse", ANSI.Green) + " " + Util.prefix("<microverse>", ANSI.White) + " : The microverse plan which defines the local devbox solo mode.");
			System.out.println("    " + Util.prefix("-dbg, --debugger", ANSI.Green) + " " + Util.prefix("<debugger>", ANSI.White) + " : Is the online debugger available");
			System.out.println("    " + Util.prefix("-lap, --local-libadama-path", ANSI.Green) + " " + Util.prefix("<local-libadama-path>", ANSI.White) + " : The path to the libadama.js source code for direct linkage.");
			System.out.println("    " + Util.prefix("-e, --environment", ANSI.Green) + " " + Util.prefix("<environment>", ANSI.White) + " : The environment label for filtering things out.");
			System.out.println("    " + Util.prefix("-pv, --preserve-view", ANSI.Green) + " " + Util.prefix("<preserve-view>", ANSI.White) + " : Whether or not to preserve (take a snapshot) of the viewstate before automatically reloading (default 'true').");
			System.out.println("    " + Util.prefix("-ty, --types", ANSI.Green) + " " + Util.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
		}
	}
	public static class FrontendEnableEncryptionArgs {
		public Config config;
		public static FrontendEnableEncryptionArgs from(String[] args, int start) {
			FrontendEnableEncryptionArgs returnArgs = new FrontendEnableEncryptionArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Encrypted product config encryption by generating a master key which", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend enable-encryption", ANSI.Green));
		}
	}
	public static class FrontendEncryptProductConfigArgs {
		public Config config;
		public String input = "product.config.json";
		public String output = "product.config.json.encrypted";
		public static FrontendEncryptProductConfigArgs from(String[] args, int start) {
			FrontendEncryptProductConfigArgs returnArgs = new FrontendEncryptProductConfigArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Encrypt product config", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend encrypt-product-config", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class FrontendMake200Args {
		public Config config;
		public String rxhtmlPath = ".";
		public String output = "200.html";
		public String environment = "production";
		public String types = "types";
		public static FrontendMake200Args from(String[] args, int start) {
			FrontendMake200Args returnArgs = new FrontendMake200Args();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-r":
					case "--rxhtml-path": {
						if (k+1 < args.length) {
							returnArgs.rxhtmlPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-e":
					case "--environment": {
						if (k+1 < args.length) {
							returnArgs.environment = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-ty":
					case "--types": {
						if (k+1 < args.length) {
							returnArgs.types = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Create a 200.html", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend make-200", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-r, --rxhtml-path", ANSI.Green) + " " + Util.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + Util.prefix("-e, --environment", ANSI.Green) + " " + Util.prefix("<environment>", ANSI.White) + " : The environment label for filtering things out.");
			System.out.println("    " + Util.prefix("-ty, --types", ANSI.Green) + " " + Util.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
		}
	}
	public static class FrontendMobileCapacitorArgs {
		public Config config;
		public String rxhtmlPath = "frontend";
		public String assetPath = "assets";
		public String localLibadamaPath;
		public String types = "types";
		public String mobileConfig;
		public static FrontendMobileCapacitorArgs from(String[] args, int start) {
			FrontendMobileCapacitorArgs returnArgs = new FrontendMobileCapacitorArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--local-libadama-path", "--mobile-config", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-r":
					case "--rxhtml-path": {
						if (k+1 < args.length) {
							returnArgs.rxhtmlPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-a":
					case "--asset-path": {
						if (k+1 < args.length) {
							returnArgs.assetPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-lap":
					case "--local-libadama-path": {
						if (k+1 < args.length) {
							returnArgs.localLibadamaPath = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-ty":
					case "--types": {
						if (k+1 < args.length) {
							returnArgs.types = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-mc":
					case "--mobile-config": {
						if (k+1 < args.length) {
							returnArgs.mobileConfig = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Create a shell for https://capacitorjs.com/", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend mobile-capacitor", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-lap, --local-libadama-path", ANSI.Green) + " " + Util.prefix("<local-libadama-path>", ANSI.White) + " : The path to the libadama.js source code for direct linkage.");
			System.out.println("    " + Util.prefix("-mc, --mobile-config", ANSI.Green) + " " + Util.prefix("<mobile-config>", ANSI.White) + " : The configuration file for a mobile app");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-r, --rxhtml-path", ANSI.Green) + " " + Util.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + Util.prefix("-a, --asset-path", ANSI.Green) + " " + Util.prefix("<asset-path>", ANSI.White) + " : The path to map for static assets.");
			System.out.println("    " + Util.prefix("-ty, --types", ANSI.Green) + " " + Util.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
		}
	}
	public static class FrontendPushGenerateArgs {
		public Config config;
		public static FrontendPushGenerateArgs from(String[] args, int start) {
			FrontendPushGenerateArgs returnArgs = new FrontendPushGenerateArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Generate VAPID tokens for a devbox.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend push-generate", ANSI.Green));
		}
	}
	public static class FrontendRxhtmlArgs {
		public Config config;
		public String input;
		public String output;
		public String environment = "production";
		public String types = "types";
		public static FrontendRxhtmlArgs from(String[] args, int start) {
			FrontendRxhtmlArgs returnArgs = new FrontendRxhtmlArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--input", "--output", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
							missing[1] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-e":
					case "--environment": {
						if (k+1 < args.length) {
							returnArgs.environment = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-ty":
					case "--types": {
						if (k+1 < args.length) {
							returnArgs.types = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Compile an rxhtml template set", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend rxhtml", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-e, --environment", ANSI.Green) + " " + Util.prefix("<environment>", ANSI.White) + " : The environment label for filtering things out.");
			System.out.println("    " + Util.prefix("-ty, --types", ANSI.Green) + " " + Util.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
		}
	}
	public static class FrontendSetLibadamaArgs {
		public Config config;
		public String localLibadamaPath = null;
		public static FrontendSetLibadamaArgs from(String[] args, int start) {
			FrontendSetLibadamaArgs returnArgs = new FrontendSetLibadamaArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-lap":
					case "--local-libadama-path": {
						if (k+1 < args.length) {
							returnArgs.localLibadamaPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Update your config to have a local-libadama-path-default which will be used in 'dev-server' when --local-libadama-path is not specified.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend set-libadama", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-lap, --local-libadama-path", ANSI.Green) + " " + Util.prefix("<local-libadama-path>", ANSI.White) + " : The path to the libadama.js source code for direct linkage.");
		}
	}
	public static class FrontendStudyCssArgs {
		public Config config;
		public String input = "style.css";
		public static FrontendStudyCssArgs from(String[] args, int start) {
			FrontendStudyCssArgs returnArgs = new FrontendStudyCssArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Tool to study CSS", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend study-css", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
		}
	}
	public static class FrontendValidateArgs {
		public Config config;
		public String rxhtmlPath = "frontend";
		public String types = "types";
		public static FrontendValidateArgs from(String[] args, int start) {
			FrontendValidateArgs returnArgs = new FrontendValidateArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-r":
					case "--rxhtml-path": {
						if (k+1 < args.length) {
							returnArgs.rxhtmlPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-ty":
					case "--types": {
						if (k+1 < args.length) {
							returnArgs.types = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("(Temporary) Runs a deeper check on an RxHTML forest", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend validate", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-r, --rxhtml-path", ANSI.Green) + " " + Util.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + Util.prefix("-ty, --types", ANSI.Green) + " " + Util.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
		}
	}
	public static class FrontendWrapCssArgs {
		public Config config;
		public String input = "style.css";
		public String output = "css.rx.html";
		public static FrontendWrapCssArgs from(String[] args, int start) {
			FrontendWrapCssArgs returnArgs = new FrontendWrapCssArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-i":
					case "--input": {
						if (k+1 < args.length) {
							returnArgs.input = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-o":
					case "--output": {
						if (k+1 < args.length) {
							returnArgs.output = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Wrap a CSS file in a rx.html script to be picked up during build", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama frontend wrap-css", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-i, --input", ANSI.Green) + " " + Util.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + Util.prefix("-o, --output", ANSI.Green) + " " + Util.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class ServicesAutoArgs {
		public Config config;
		public static ServicesAutoArgs from(String[] args, int start) {
			ServicesAutoArgs returnArgs = new ServicesAutoArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("The config will decide the role", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama services auto", ANSI.Green));
		}
	}
	public static class ServicesBackendArgs {
		public Config config;
		public static ServicesBackendArgs from(String[] args, int start) {
			ServicesBackendArgs returnArgs = new ServicesBackendArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Spin up a Adama back-end node", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama services backend", ANSI.Green));
		}
	}
	public static class ServicesDashboardsArgs {
		public Config config;
		public static ServicesDashboardsArgs from(String[] args, int start) {
			ServicesDashboardsArgs returnArgs = new ServicesDashboardsArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Produce dashboards for prometheus.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama services dashboards", ANSI.Green));
		}
	}
	public static class ServicesFrontendArgs {
		public Config config;
		public static ServicesFrontendArgs from(String[] args, int start) {
			ServicesFrontendArgs returnArgs = new ServicesFrontendArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Spin up a WebSocket front-end node", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama services frontend", ANSI.Green));
		}
	}
	public static class ServicesOverlordArgs {
		public Config config;
		public static ServicesOverlordArgs from(String[] args, int start) {
			ServicesOverlordArgs returnArgs = new ServicesOverlordArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Spin up the cluster overlord", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama services overlord", ANSI.Green));
		}
	}
	public static class ServicesPrepareArgs {
		public Config config;
		public static ServicesPrepareArgs from(String[] args, int start) {
			ServicesPrepareArgs returnArgs = new ServicesPrepareArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Run code that signals a deployment is coming", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama services prepare", ANSI.Green));
		}
	}
	public static class ServicesProbeArgs {
		public Config config;
		public String target = "127.0.0.1:8001";
		public static ServicesProbeArgs from(String[] args, int start) {
			ServicesProbeArgs returnArgs = new ServicesProbeArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-tg":
					case "--target": {
						if (k+1 < args.length) {
							returnArgs.target = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Connect to the local Adama instance", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama services probe", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-tg, --target", ANSI.Green) + " " + Util.prefix("<target>", ANSI.White) + " : A target is a combination of ip address and port.");
		}
	}
	public static class ServicesSoloArgs {
		public Config config;
		public static ServicesSoloArgs from(String[] args, int start) {
			ServicesSoloArgs returnArgs = new ServicesSoloArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Spin up a solo machine", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama services solo", ANSI.Green));
		}
	}
	public static class CanaryArgs {
		public Config config;
		public String scenario;
		public static CanaryArgs from(String[] args, int start) {
			CanaryArgs returnArgs = new CanaryArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--scenario", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-sn":
					case "--scenario": {
						if (k+1 < args.length) {
							returnArgs.scenario = args[k+1];
							k++;
							missing[0] = null;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			boolean invalid = false;
			for (String misArg : missing) {
				if (misArg != null) {
					System.err.println("Expected argument '" + misArg + "'");
					invalid = true;
				}
			}
			return (invalid ? null : returnArgs);
		}
		public static void help() {
			System.out.println(Util.prefix("Run an E2E test suite against production", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama canary", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-sn, --scenario", ANSI.Green) + " " + Util.prefix("<scenario>", ANSI.White));
		}
	}
	public static class DeinitArgs {
		public Config config;
		public static DeinitArgs from(String[] args, int start) {
			DeinitArgs returnArgs = new DeinitArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Destroy your account. This requires you to delete all spaces, documents, authorities, and domains.", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama deinit", ANSI.Green));
		}
	}
	public static class DevboxArgs {
		public Config config;
		public String rxhtmlPath = "frontend";
		public String assetPath = "assets";
		public String microverse = "local.verse.json";
		public String debugger = "true";
		public String localLibadamaPath = null;
		public String environment = "test";
		public String preserveView = "true";
		public String types = "types";
		public static DevboxArgs from(String[] args, int start) {
			DevboxArgs returnArgs = new DevboxArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-r":
					case "--rxhtml-path": {
						if (k+1 < args.length) {
							returnArgs.rxhtmlPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-a":
					case "--asset-path": {
						if (k+1 < args.length) {
							returnArgs.assetPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-mv":
					case "--microverse": {
						if (k+1 < args.length) {
							returnArgs.microverse = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-dbg":
					case "--debugger": {
						if (k+1 < args.length) {
							returnArgs.debugger = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-lap":
					case "--local-libadama-path": {
						if (k+1 < args.length) {
							returnArgs.localLibadamaPath = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-e":
					case "--environment": {
						if (k+1 < args.length) {
							returnArgs.environment = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-pv":
					case "--preserve-view": {
						if (k+1 < args.length) {
							returnArgs.preserveView = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-ty":
					case "--types": {
						if (k+1 < args.length) {
							returnArgs.types = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Host the working directory as a personal localhost instance", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama devbox", ANSI.Green)+ " " + Util.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(Util.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("-r, --rxhtml-path", ANSI.Green) + " " + Util.prefix("<rxhtml-path>", ANSI.White));
			System.out.println("    " + Util.prefix("-a, --asset-path", ANSI.Green) + " " + Util.prefix("<asset-path>", ANSI.White));
			System.out.println("    " + Util.prefix("-mv, --microverse", ANSI.Green) + " " + Util.prefix("<microverse>", ANSI.White));
			System.out.println("    " + Util.prefix("-dbg, --debugger", ANSI.Green) + " " + Util.prefix("<debugger>", ANSI.White));
			System.out.println("    " + Util.prefix("-lap, --local-libadama-path", ANSI.Green) + " " + Util.prefix("<local-libadama-path>", ANSI.White));
			System.out.println("    " + Util.prefix("-e, --environment", ANSI.Green) + " " + Util.prefix("<environment>", ANSI.White));
			System.out.println("    " + Util.prefix("-pv, --preserve-view", ANSI.Green) + " " + Util.prefix("<preserve-view>", ANSI.White));
			System.out.println("    " + Util.prefix("-ty, --types", ANSI.Green) + " " + Util.prefix("<types>", ANSI.White));
		}
	}
	public static class DumpenvArgs {
		public Config config;
		public static DumpenvArgs from(String[] args, int start) {
			DumpenvArgs returnArgs = new DumpenvArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Dump your environment variables", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama dumpenv", ANSI.Green));
		}
	}
	public static class InitArgs {
		public Config config;
		public static InitArgs from(String[] args, int start) {
			InitArgs returnArgs = new InitArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Initializes the config with a valid token", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama init", ANSI.Green));
		}
	}
	public static class KickstartArgs {
		public Config config;
		public static KickstartArgs from(String[] args, int start) {
			KickstartArgs returnArgs = new KickstartArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Kickstart a project via an interactive process!", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama kickstart", ANSI.Green));
		}
	}
	public static class VersionArgs {
		public Config config;
		public static VersionArgs from(String[] args, int start) {
			VersionArgs returnArgs = new VersionArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
						case "--help":
						case "-h":
						case "help":
							if (k == start)
								return null;
						case "--config":
							k++;
						case "--json":
						case "--no-color":
							break;
						default:
							System.err.println("Unknown argument '" + args[k] + "'");
							return null;
				}
			}
			return returnArgs;
		}
		public static void help() {
			System.out.println(Util.prefix("Dump the current Adama version", ANSI.Green));
			System.out.println(Util.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + Util.prefix("adama version", ANSI.Green));
		}
	}
}
