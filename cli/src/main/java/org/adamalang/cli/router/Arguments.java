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
import org.adamalang.common.ColorUtilTools;
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
			System.out.println(ColorUtilTools.prefix("Creates a new space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space create", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
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
			System.out.println(ColorUtilTools.prefix("Deletes an empty space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space delete", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
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
			System.out.println(ColorUtilTools.prefix("Deploy a plan to a space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space deploy", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-p, --plan", ANSI.Green) + " " + ColorUtilTools.prefix("<plan>", ANSI.White) + " : A deployment plan; see https://book.adama-platform.com/reference/deployment-plan.html .");
			System.out.println("    " + ColorUtilTools.prefix("-f, --file", ANSI.Green) + " " + ColorUtilTools.prefix("<file>", ANSI.White) + " : A file.");
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
			System.out.println(ColorUtilTools.prefix("List developers for the given space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space developers", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
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
			System.out.println(ColorUtilTools.prefix("Encrypt a private key to store within code", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space encrypt-priv", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-p, --priv", ANSI.Green) + " " + ColorUtilTools.prefix("<priv>", ANSI.White) + " : A special JSON encoded private key");
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
			System.out.println(ColorUtilTools.prefix("Encrypt a secret to store within code", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space encrypt-secret", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
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
			System.out.println(ColorUtilTools.prefix("Generate a server-side key to use for storing secrets", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space generate-key", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
		}
	}
	public static class SpaceGeneratePolicyArgs {
		public Config config;
		public String output;
		public static SpaceGeneratePolicyArgs from(String[] args, int start) {
			SpaceGeneratePolicyArgs returnArgs = new SpaceGeneratePolicyArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--output", };
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
			System.out.println(ColorUtilTools.prefix("Generate a default policy", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space generate-policy", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("Get a space's plan", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space get", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("Get the access control policy for the space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space get-policy", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("Get the frontend RxHTML forest", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space get-rxhtml", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("List spaces available to your account", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space list", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-m, --marker", ANSI.Green) + " " + ColorUtilTools.prefix("<marker>", ANSI.White) + " : Items greater than the marker are returned.");
			System.out.println("    " + ColorUtilTools.prefix("-l, --limit", ANSI.Green) + " " + ColorUtilTools.prefix("<limit>", ANSI.White) + " : Limit the returned items.");
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
			System.out.println(ColorUtilTools.prefix("Get a metric report for the space and the documents that share the prefix", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space metrics", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-p, --prefix", ANSI.Green) + " " + ColorUtilTools.prefix("<prefix>", ANSI.White) + " : Items that have this prefixed are included");
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
			System.out.println(ColorUtilTools.prefix("Get a file of the reflection of a space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space reflect", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
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
			System.out.println(ColorUtilTools.prefix("Set the space's access control policy", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space set-policy", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-f, --file", ANSI.Green) + " " + ColorUtilTools.prefix("<file>", ANSI.White) + " : A file.");
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
			System.out.println(ColorUtilTools.prefix("Set the role of another developer", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space set-role", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-e, --email", ANSI.Green) + " " + ColorUtilTools.prefix("<email>", ANSI.White) + " : An email address.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-r, --role", ANSI.Green) + " " + ColorUtilTools.prefix("<role>", ANSI.White) + " : Options are 'developer' or 'none'.");
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
			System.out.println(ColorUtilTools.prefix("Set the frontend RxHTML forest", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space set-rxhtml", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-f, --file", ANSI.Green) + " " + ColorUtilTools.prefix("<file>", ANSI.White) + " : A file.");
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
			System.out.println(ColorUtilTools.prefix("Upload a file or directory to a space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama space upload", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-g, --gc", ANSI.Green) + " " + ColorUtilTools.prefix("<gc>", ANSI.White) + " : Delete assets that were not present in this upload.");
			System.out.println("    " + ColorUtilTools.prefix("-f, --file", ANSI.Green) + " " + ColorUtilTools.prefix("<file>", ANSI.White) + " : A file.");
			System.out.println("    " + ColorUtilTools.prefix("-d, --directory", ANSI.Green) + " " + ColorUtilTools.prefix("<directory>", ANSI.White) + " : A directory.");
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
			System.out.println(ColorUtilTools.prefix("Append a new public key to the public key file", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama authority append-local", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-a, --authority", ANSI.Green) + " " + ColorUtilTools.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
			System.out.println("    " + ColorUtilTools.prefix("-p, --priv", ANSI.Green) + " " + ColorUtilTools.prefix("<priv>", ANSI.White) + " : A special JSON encoded private key");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-k, --keystore", ANSI.Green) + " " + ColorUtilTools.prefix("<keystore>", ANSI.White) + " : A special JSON encoded keystore holding only public keys.");
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
			System.out.println(ColorUtilTools.prefix("Creates a new authority", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama authority create", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Make a new set of public keys", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama authority create-local", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-a, --authority", ANSI.Green) + " " + ColorUtilTools.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-k, --keystore", ANSI.Green) + " " + ColorUtilTools.prefix("<keystore>", ANSI.White) + " : A special JSON encoded keystore holding only public keys.");
			System.out.println("    " + ColorUtilTools.prefix("-p, --priv", ANSI.Green) + " " + ColorUtilTools.prefix("<priv>", ANSI.White) + " : A special JSON encoded private key");
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
			System.out.println(ColorUtilTools.prefix("Destroy an authority", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama authority destroy", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-a, --authority", ANSI.Green) + " " + ColorUtilTools.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
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
			System.out.println(ColorUtilTools.prefix("Get released public keys for an authority", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama authority get", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-a, --authority", ANSI.Green) + " " + ColorUtilTools.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --keystore", ANSI.Green) + " " + ColorUtilTools.prefix("<keystore>", ANSI.White) + " : A special JSON encoded keystore holding only public keys.");
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
			System.out.println(ColorUtilTools.prefix("List authorities this developer owns", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama authority list", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Set public keys to an authority", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama authority set", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-a, --authority", ANSI.Green) + " " + ColorUtilTools.prefix("<authority>", ANSI.White) + " : The name or key of a keystore.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --keystore", ANSI.Green) + " " + ColorUtilTools.prefix("<keystore>", ANSI.White) + " : A special JSON encoded keystore holding only public keys.");
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
			System.out.println(ColorUtilTools.prefix("Sign an agent with a local private key", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama authority sign", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + ColorUtilTools.prefix("-ag, --agent", ANSI.Green) + " " + ColorUtilTools.prefix("<agent>", ANSI.White) + " : The user id or agent part of a principal.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-v, --validate", ANSI.Green) + " " + ColorUtilTools.prefix("<validate>", ANSI.White) + " : Should the plan be validated.");
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
			System.out.println(ColorUtilTools.prefix("Create a password to be used on web", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama account set-password", ANSI.Green));
		}
	}
	public static class CodeBenchmarkMessageArgs {
		public Config config;
		public String main;
		public String imports = "backend";
		public String data = "input.json";
		public String message = "message.json";
		public String dumpTo = "benchmark.report.json";
		public static CodeBenchmarkMessageArgs from(String[] args, int start) {
			CodeBenchmarkMessageArgs returnArgs = new CodeBenchmarkMessageArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--main", };
			for (int k = start; k < args.length; k++) {
				switch(args[k]) {
					case "-m":
					case "--main": {
						if (k+1 < args.length) {
							returnArgs.main = args[k+1];
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
					case "-dt":
					case "--data": {
						if (k+1 < args.length) {
							returnArgs.data = args[k+1];
							k++;
						} else {
							System.err.println("Expected value for argument '" + args[k] + "'");
							return null;
						}
						break;
					}
					case "-msg":
					case "--message": {
						if (k+1 < args.length) {
							returnArgs.message = args[k+1];
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
			System.out.println(ColorUtilTools.prefix("Compiles the adama file and shows any problems", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama code benchmark-message", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-m, --main", ANSI.Green) + " " + ColorUtilTools.prefix("<main>", ANSI.White) + " : The main/primary adama file.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --imports", ANSI.Green) + " " + ColorUtilTools.prefix("<imports>", ANSI.White) + " : A directory containing adama files to import into the main");
			System.out.println("    " + ColorUtilTools.prefix("-dt, --data", ANSI.Green) + " " + ColorUtilTools.prefix("<data>", ANSI.White) + " : A file containing a snapshot");
			System.out.println("    " + ColorUtilTools.prefix("-msg, --message", ANSI.Green) + " " + ColorUtilTools.prefix("<message>", ANSI.White) + " : A file containing a send message (channel + message)");
			System.out.println("    " + ColorUtilTools.prefix("-d, --dump-to", ANSI.Green) + " " + ColorUtilTools.prefix("<dump-to>", ANSI.White) + " : Dump the output/result to the given file");
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
			System.out.println(ColorUtilTools.prefix("Bundle the main and imports into a single deployment plan.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama code bundle-plan", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + ColorUtilTools.prefix("-m, --main", ANSI.Green) + " " + ColorUtilTools.prefix("<main>", ANSI.White) + " : The main/primary adama file.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-in, --instrument", ANSI.Green) + " " + ColorUtilTools.prefix("<instrument>", ANSI.White) + " : Instrument the plan");
			System.out.println("    " + ColorUtilTools.prefix("-i, --imports", ANSI.Green) + " " + ColorUtilTools.prefix("<imports>", ANSI.White) + " : A directory containing adama files to import into the main");
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
			System.out.println(ColorUtilTools.prefix("Compiles the adama file and shows any problems", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama code compile-file", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-f, --file", ANSI.Green) + " " + ColorUtilTools.prefix("<file>", ANSI.White) + " : A file.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --imports", ANSI.Green) + " " + ColorUtilTools.prefix("<imports>", ANSI.White) + " : A directory containing adama files to import into the main");
			System.out.println("    " + ColorUtilTools.prefix("-d, --dump-to", ANSI.Green) + " " + ColorUtilTools.prefix("<dump-to>", ANSI.White) + " : Dump the output/result to the given file");
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
			System.out.println(ColorUtilTools.prefix("Convert a reflection JSON into a mermaid diagram source", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama code diagram", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + ColorUtilTools.prefix("-tt, --title", ANSI.Green) + " " + ColorUtilTools.prefix("<title>", ANSI.White) + " : The title of the diagram");
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
			System.out.println(ColorUtilTools.prefix("Format the file or directory recursively (and inline updates)", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama code format", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-f, --file", ANSI.Green) + " " + ColorUtilTools.prefix("<file>", ANSI.White) + " : A file.");
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
			System.out.println(ColorUtilTools.prefix("Spin up a single threaded language service protocol server", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama code lsp", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-p, --port", ANSI.Green) + " " + ColorUtilTools.prefix("<port>", ANSI.White) + " : Port for a server");
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
			System.out.println(ColorUtilTools.prefix("Compiles the adama file and dumps the reflection json", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama code reflect-dump", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-f, --file", ANSI.Green) + " " + ColorUtilTools.prefix("<file>", ANSI.White) + " : A file.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --imports", ANSI.Green) + " " + ColorUtilTools.prefix("<imports>", ANSI.White) + " : A directory containing adama files to import into the main");
			System.out.println("    " + ColorUtilTools.prefix("-d, --dump-to", ANSI.Green) + " " + ColorUtilTools.prefix("<dump-to>", ANSI.White) + " : Dump the output/result to the given file");
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
			System.out.println(ColorUtilTools.prefix("Validates a deployment plan (locally) for speed", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama code validate-plan", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-p, --plan", ANSI.Green) + " " + ColorUtilTools.prefix("<plan>", ANSI.White) + " : A deployment plan; see https://book.adama-platform.com/reference/deployment-plan.html .");
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
			System.out.println(ColorUtilTools.prefix("Bundles the libadama.js into the webserver", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib bundle-js", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Sprinkle the copyright everywhere.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib copyright", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Produces api files for SaaS and documentation for the WebSocket low level API.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib make-api", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Compile Adama's Book", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib make-book", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + ColorUtilTools.prefix("-bt, --book-template", ANSI.Green) + " " + ColorUtilTools.prefix("<book-template>", ANSI.White) + " : Template for generating the book");
			System.out.println("    " + ColorUtilTools.prefix("-bm, --book-merge", ANSI.Green) + " " + ColorUtilTools.prefix("<book-merge>", ANSI.White) + " : Files to merge into the book");
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
			System.out.println(ColorUtilTools.prefix("Generate the command line router", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib make-cli", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Generates the networking codec", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib make-codec", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Generates the embedded templates", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib make-embed", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Generates the error table which provides useful insight to issues", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib make-et", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Generate string templates", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib str-temp", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Generate tests for Adama Language.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib tests-adama", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + ColorUtilTools.prefix("-e, --errors", ANSI.Green) + " " + ColorUtilTools.prefix("<errors>", ANSI.White) + " : Placeholder");
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
			System.out.println(ColorUtilTools.prefix("Generate tests for RxHTML.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib tests-rxhtml", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("Create the version number for the platform", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama contrib version", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Update the configuration", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama database configure", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Install the tables on a monolithic database", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama database install", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Create reserved spaces", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama database make-reserved", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-e, --email", ANSI.Green) + " " + ColorUtilTools.prefix("<email>", ANSI.White) + " : An email address.");
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
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
			System.out.println(ColorUtilTools.prefix("Migrate data from 'db' to 'nextdb'", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama database migrate", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Attach an asset to a document", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document attach", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + ColorUtilTools.prefix("-f, --file", ANSI.Green) + " " + ColorUtilTools.prefix("<file>", ANSI.White) + " : A file.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-n, --name", ANSI.Green) + " " + ColorUtilTools.prefix("<name>", ANSI.White) + " : Placeholder");
			System.out.println("    " + ColorUtilTools.prefix("-t, --type", ANSI.Green) + " " + ColorUtilTools.prefix("<type>", ANSI.White) + " : Placeholder");
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
			System.out.println(ColorUtilTools.prefix("Connect to a document", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document connect", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
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
			System.out.println(ColorUtilTools.prefix("Create a document", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document create", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + ColorUtilTools.prefix("-aa, --arg", ANSI.Green) + " " + ColorUtilTools.prefix("<arg>", ANSI.White) + " : The constructor argument.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-e, --entropy", ANSI.Green) + " " + ColorUtilTools.prefix("<entropy>", ANSI.White) + " : A random seed.");
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
			System.out.println(ColorUtilTools.prefix("Delete a document", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document delete", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
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
			System.out.println(ColorUtilTools.prefix("Download the latest archive backup", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document download-archive", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class DocumentDownloadBackupArgs {
		public Config config;
		public String space;
		public String key;
		public String backupId;
		public String output;
		public static DocumentDownloadBackupArgs from(String[] args, int start) {
			DocumentDownloadBackupArgs returnArgs = new DocumentDownloadBackupArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--key", "--backup-id", "--output", };
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
					case "-r":
					case "--backup-id": {
						if (k+1 < args.length) {
							returnArgs.backupId = args[k+1];
							k++;
							missing[2] = null;
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
			System.out.println(ColorUtilTools.prefix("Download the latest archive backup", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document download-backup", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + ColorUtilTools.prefix("-r, --backup-id", ANSI.Green) + " " + ColorUtilTools.prefix("<backup-id>", ANSI.White) + " : The id of the backup");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("List documents", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document list", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-m, --marker", ANSI.Green) + " " + ColorUtilTools.prefix("<marker>", ANSI.White) + " : Items greater than the marker are returned.");
			System.out.println("    " + ColorUtilTools.prefix("-l, --limit", ANSI.Green) + " " + ColorUtilTools.prefix("<limit>", ANSI.White) + " : Limit the returned items.");
		}
	}
	public static class DocumentListBackupsArgs {
		public Config config;
		public String space;
		public String key;
		public static DocumentListBackupsArgs from(String[] args, int start) {
			DocumentListBackupsArgs returnArgs = new DocumentListBackupsArgs();
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
			System.out.println(ColorUtilTools.prefix("List the available backups", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document list-backups", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
		}
	}
	public static class DocumentListPushTokensArgs {
		public Config config;
		public String space;
		public String key;
		public String domain;
		public String agent;
		public static DocumentListPushTokensArgs from(String[] args, int start) {
			DocumentListPushTokensArgs returnArgs = new DocumentListPushTokensArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--space", "--key", "--domain", "--agent", };
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
					case "-d":
					case "--domain": {
						if (k+1 < args.length) {
							returnArgs.domain = args[k+1];
							k++;
							missing[2] = null;
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
			System.out.println(ColorUtilTools.prefix("List push tokens for a specific agent within a document's authority", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama document list-push-tokens", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + ColorUtilTools.prefix("-d, --domain", ANSI.Green) + " " + ColorUtilTools.prefix("<domain>", ANSI.White) + " : The domain name");
			System.out.println("    " + ColorUtilTools.prefix("-ag, --agent", ANSI.Green) + " " + ColorUtilTools.prefix("<agent>", ANSI.White) + " : The user id or agent part of a principal.");
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
			System.out.println(ColorUtilTools.prefix("Compact an archive to a single JSON file", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama ops compact", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("Explain the history of a value at a path", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama ops explain", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-j, --jquery", ANSI.Green) + " " + ColorUtilTools.prefix("<jquery>", ANSI.White) + " : A json path (field0.field1....fieldN)");
		}
	}
	public static class OpsForensicsArgs {
		public Config config;
		public String input;
		public String output;
		public String minSize = "0";
		public static OpsForensicsArgs from(String[] args, int start) {
			OpsForensicsArgs returnArgs = new OpsForensicsArgs();
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
					case "-ms":
					case "--min-size": {
						if (k+1 < args.length) {
							returnArgs.minSize = args[k+1];
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
			System.out.println(ColorUtilTools.prefix("Dive into a data store and recover snapshots", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama ops forensics", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-ms, --min-size", ANSI.Green) + " " + ColorUtilTools.prefix("<min-size>", ANSI.White) + " : Minimum size for considering a recovered json object.");
		}
	}
	public static class OpsSummarizeArgs {
		public Config config;
		public String input;
		public static OpsSummarizeArgs from(String[] args, int start) {
			OpsSummarizeArgs returnArgs = new OpsSummarizeArgs();
			try {
				returnArgs.config = Config.fromArgs(args);
			} catch (Exception er) {
				System.out.println("Error creating default config file.");
			}
			String[] missing = new String[]{"--input", };
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
			System.out.println(ColorUtilTools.prefix("Summarize the archive in a meaningful way", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama ops summarize", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
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
			System.out.println(ColorUtilTools.prefix("Provide a product configuration to define various aspects of a product by domain", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama domain configure", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-d, --domain", ANSI.Green) + " " + ColorUtilTools.prefix("<domain>", ANSI.White) + " : The domain name");
			System.out.println("    " + ColorUtilTools.prefix("-p, --product", ANSI.Green) + " " + ColorUtilTools.prefix("<product>", ANSI.White) + " : Product configuration for native apps on the platform");
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
			System.out.println(ColorUtilTools.prefix("List domains", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama domain list", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Map a domain to a space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama domain map", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-d, --domain", ANSI.Green) + " " + ColorUtilTools.prefix("<domain>", ANSI.White) + " : The domain name");
			System.out.println("    " + ColorUtilTools.prefix("-s, --space", ANSI.Green) + " " + ColorUtilTools.prefix("<space>", ANSI.White) + " : A 'space' is a collection of documents with the same schema and logic; space names must have a length greater than 3 and less than 128, have valid characters are lower-case alphanumeric or hyphens, and double hyphens (--) are not allowed.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-c, --cert", ANSI.Green) + " " + ColorUtilTools.prefix("<cert>", ANSI.White) + " : Placeholder");
			System.out.println("    " + ColorUtilTools.prefix("-k, --key", ANSI.Green) + " " + ColorUtilTools.prefix("<key>", ANSI.White) + " : A document key; keys must have a length greater than 0 and less than 512; valid characters are A-Z, a-z, 0-9, underscore (_), hyphen (-i), or period (.).");
			System.out.println("    " + ColorUtilTools.prefix("-re, --route", ANSI.Green) + " " + ColorUtilTools.prefix("<route>", ANSI.White) + " : Should the domain route to the key's handler.");
			System.out.println("    " + ColorUtilTools.prefix("-a, --auto", ANSI.Green) + " " + ColorUtilTools.prefix("<auto>", ANSI.White) + " : Should the Adama Platform automatically get a domain.");
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
			System.out.println(ColorUtilTools.prefix("Unmap a domain from a space", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama domain unmap", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-d, --domain", ANSI.Green) + " " + ColorUtilTools.prefix("<domain>", ANSI.White) + " : The domain name");
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
			System.out.println(ColorUtilTools.prefix("Bundle many *.rx.html into one big one.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend bundle", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-r, --rxhtml-path", ANSI.Green) + " " + ColorUtilTools.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("Decrypt product config", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend decrypt-product-config", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("Host the working directory as a webserver", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend dev-server", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-r, --rxhtml-path", ANSI.Green) + " " + ColorUtilTools.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + ColorUtilTools.prefix("-a, --asset-path", ANSI.Green) + " " + ColorUtilTools.prefix("<asset-path>", ANSI.White) + " : The path to map for static assets.");
			System.out.println("    " + ColorUtilTools.prefix("-mv, --microverse", ANSI.Green) + " " + ColorUtilTools.prefix("<microverse>", ANSI.White) + " : The microverse plan which defines the local devbox solo mode.");
			System.out.println("    " + ColorUtilTools.prefix("-dbg, --debugger", ANSI.Green) + " " + ColorUtilTools.prefix("<debugger>", ANSI.White) + " : Is the online debugger available");
			System.out.println("    " + ColorUtilTools.prefix("-lap, --local-libadama-path", ANSI.Green) + " " + ColorUtilTools.prefix("<local-libadama-path>", ANSI.White) + " : The path to the libadama.js source code for direct linkage.");
			System.out.println("    " + ColorUtilTools.prefix("-e, --environment", ANSI.Green) + " " + ColorUtilTools.prefix("<environment>", ANSI.White) + " : The environment label for filtering things out.");
			System.out.println("    " + ColorUtilTools.prefix("-pv, --preserve-view", ANSI.Green) + " " + ColorUtilTools.prefix("<preserve-view>", ANSI.White) + " : Whether or not to preserve (take a snapshot) of the viewstate before automatically reloading (default 'true').");
			System.out.println("    " + ColorUtilTools.prefix("-ty, --types", ANSI.Green) + " " + ColorUtilTools.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
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
			System.out.println(ColorUtilTools.prefix("Encrypted product config encryption by generating a master key which", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend enable-encryption", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Encrypt product config", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend encrypt-product-config", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
		}
	}
	public static class FrontendMake200Args {
		public Config config;
		public String rxhtmlPath = ".";
		public String output = "200.html";
		public String environment = "prod";
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
			System.out.println(ColorUtilTools.prefix("Create a 200.html", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend make-200", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-r, --rxhtml-path", ANSI.Green) + " " + ColorUtilTools.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println("    " + ColorUtilTools.prefix("-e, --environment", ANSI.Green) + " " + ColorUtilTools.prefix("<environment>", ANSI.White) + " : The environment label for filtering things out.");
			System.out.println("    " + ColorUtilTools.prefix("-ty, --types", ANSI.Green) + " " + ColorUtilTools.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
		}
	}
	public static class FrontendMeasureArgs {
		public Config config;
		public String rxhtmlPath = "frontend";
		public static FrontendMeasureArgs from(String[] args, int start) {
			FrontendMeasureArgs returnArgs = new FrontendMeasureArgs();
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
			System.out.println(ColorUtilTools.prefix("Measure potential optimizations for an RxHTML directory", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend measure", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-r, --rxhtml-path", ANSI.Green) + " " + ColorUtilTools.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
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
			System.out.println(ColorUtilTools.prefix("Create a shell for https://capacitorjs.com/", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend mobile-capacitor", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-lap, --local-libadama-path", ANSI.Green) + " " + ColorUtilTools.prefix("<local-libadama-path>", ANSI.White) + " : The path to the libadama.js source code for direct linkage.");
			System.out.println("    " + ColorUtilTools.prefix("-mc, --mobile-config", ANSI.Green) + " " + ColorUtilTools.prefix("<mobile-config>", ANSI.White) + " : The configuration file for a mobile app");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-r, --rxhtml-path", ANSI.Green) + " " + ColorUtilTools.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + ColorUtilTools.prefix("-a, --asset-path", ANSI.Green) + " " + ColorUtilTools.prefix("<asset-path>", ANSI.White) + " : The path to map for static assets.");
			System.out.println("    " + ColorUtilTools.prefix("-ty, --types", ANSI.Green) + " " + ColorUtilTools.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
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
			System.out.println(ColorUtilTools.prefix("Generate VAPID tokens for a devbox.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend push-generate", ANSI.Green));
		}
	}
	public static class FrontendRxhtmlArgs {
		public Config config;
		public String input;
		public String output;
		public String environment = "prod";
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
			System.out.println(ColorUtilTools.prefix("Compile an rxhtml template set", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend rxhtml", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-e, --environment", ANSI.Green) + " " + ColorUtilTools.prefix("<environment>", ANSI.White) + " : The environment label for filtering things out.");
			System.out.println("    " + ColorUtilTools.prefix("-ty, --types", ANSI.Green) + " " + ColorUtilTools.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
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
			System.out.println(ColorUtilTools.prefix("Update your config to have a local-libadama-path-default which will be used in 'dev-server' when --local-libadama-path is not specified.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend set-libadama", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-lap, --local-libadama-path", ANSI.Green) + " " + ColorUtilTools.prefix("<local-libadama-path>", ANSI.White) + " : The path to the libadama.js source code for direct linkage.");
		}
	}
	public static class FrontendTailwindKickArgs {
		public Config config;
		public static FrontendTailwindKickArgs from(String[] args, int start) {
			FrontendTailwindKickArgs returnArgs = new FrontendTailwindKickArgs();
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
			System.out.println(ColorUtilTools.prefix("Bootstrap an RxHTML project in the working directory with tailwind", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend tailwind-kick", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("(Temporary) Runs a deeper check on an RxHTML forest", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend validate", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-r, --rxhtml-path", ANSI.Green) + " " + ColorUtilTools.prefix("<rxhtml-path>", ANSI.White) + " : The path to scan for RxHTML files.");
			System.out.println("    " + ColorUtilTools.prefix("-ty, --types", ANSI.Green) + " " + ColorUtilTools.prefix("<types>", ANSI.White) + " : The path for RxHTML to scan for reflected types.");
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
			System.out.println(ColorUtilTools.prefix("Wrap a CSS file in a rx.html script to be picked up during build", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama frontend wrap-css", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-i, --input", ANSI.Green) + " " + ColorUtilTools.prefix("<input>", ANSI.White) + " : An input file");
			System.out.println("    " + ColorUtilTools.prefix("-o, --output", ANSI.Green) + " " + ColorUtilTools.prefix("<output>", ANSI.White) + " : A file (or directory) to output to.");
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
			System.out.println(ColorUtilTools.prefix("The config will decide the role", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama services auto", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Spin up a Adama back-end node", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama services backend", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Produce dashboards for prometheus.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama services dashboards", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Spin up a WebSocket front-end node", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama services frontend", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Spin up the cluster overlord", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama services overlord", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Run code that signals a deployment is coming", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama services prepare", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Connect to the local Adama instance", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama services probe", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-tg, --target", ANSI.Green) + " " + ColorUtilTools.prefix("<target>", ANSI.White) + " : A target is a combination of ip address and port.");
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
			System.out.println(ColorUtilTools.prefix("Spin up a solo machine", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama services solo", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Run an E2E test suite against production", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama canary", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-sn, --scenario", ANSI.Green) + " " + ColorUtilTools.prefix("<scenario>", ANSI.White));
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
			System.out.println(ColorUtilTools.prefix("Destroy your account. This requires you to delete all spaces, documents, authorities, and domains.", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama deinit", ANSI.Green));
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
		public String languagePort = "2423";
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
					case "-p":
					case "--language-port": {
						if (k+1 < args.length) {
							returnArgs.languagePort = args[k+1];
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
			System.out.println(ColorUtilTools.prefix("Host the working directory as a personal localhost instance", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama devbox", ANSI.Green)+ " " + ColorUtilTools.prefix("[FLAGS]", ANSI.Magenta));
			System.out.println(ColorUtilTools.prefixBold("OPTIONAL FLAGS:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("-r, --rxhtml-path", ANSI.Green) + " " + ColorUtilTools.prefix("<rxhtml-path>", ANSI.White));
			System.out.println("    " + ColorUtilTools.prefix("-a, --asset-path", ANSI.Green) + " " + ColorUtilTools.prefix("<asset-path>", ANSI.White));
			System.out.println("    " + ColorUtilTools.prefix("-mv, --microverse", ANSI.Green) + " " + ColorUtilTools.prefix("<microverse>", ANSI.White));
			System.out.println("    " + ColorUtilTools.prefix("-dbg, --debugger", ANSI.Green) + " " + ColorUtilTools.prefix("<debugger>", ANSI.White));
			System.out.println("    " + ColorUtilTools.prefix("-lap, --local-libadama-path", ANSI.Green) + " " + ColorUtilTools.prefix("<local-libadama-path>", ANSI.White));
			System.out.println("    " + ColorUtilTools.prefix("-e, --environment", ANSI.Green) + " " + ColorUtilTools.prefix("<environment>", ANSI.White));
			System.out.println("    " + ColorUtilTools.prefix("-pv, --preserve-view", ANSI.Green) + " " + ColorUtilTools.prefix("<preserve-view>", ANSI.White));
			System.out.println("    " + ColorUtilTools.prefix("-ty, --types", ANSI.Green) + " " + ColorUtilTools.prefix("<types>", ANSI.White));
			System.out.println("    " + ColorUtilTools.prefix("-p, --language-port", ANSI.Green) + " " + ColorUtilTools.prefix("<language-port>", ANSI.White));
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
			System.out.println(ColorUtilTools.prefix("Dump your environment variables", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama dumpenv", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Initializes the config with a valid token", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama init", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Kickstart a project via an interactive process!", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama kickstart", ANSI.Green));
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
			System.out.println(ColorUtilTools.prefix("Dump the current Adama version", ANSI.Green));
			System.out.println(ColorUtilTools.prefixBold("USAGE:", ANSI.Yellow));
			System.out.println("    " + ColorUtilTools.prefix("adama version", ANSI.Green));
		}
	}
}
