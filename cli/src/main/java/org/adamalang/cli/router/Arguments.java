package org.adamalang.cli.router;

import org.adamalang.cli.runtime.Argument;
import org.adamalang.cli.Config;

public class Arguments {
	public static class SpaceCreateArgs {
		public String space;
		public static SpaceCreateArgs from(String[] args, int start) {
			SpaceCreateArgs returnArgs = new SpaceCreateArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceDeleteArgs {
		public String space;
		public static SpaceDeleteArgs from(String[] args, int start) {
			SpaceDeleteArgs returnArgs = new SpaceDeleteArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceDeployArgs {
		public String space;
		public String dumpTo = "null";
		public String plan;
		public String file = "null";
		public static SpaceDeployArgs from(String[] args, int start) {
			SpaceDeployArgs returnArgs = new SpaceDeployArgs();
			String[] missing = new String[]{"--space", "--plan", };
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
					case "-p":
					case "--plan": {
						if (k+1 < args.length) {
							returnArgs.plan = args[k+1];
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceSetRxhtmlArgs {
		public String space;
		public String file = "null";
		public static SpaceSetRxhtmlArgs from(String[] args, int start) {
			SpaceSetRxhtmlArgs returnArgs = new SpaceSetRxhtmlArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceGetRxhtmlArgs {
		public String space;
		public static SpaceGetRxhtmlArgs from(String[] args, int start) {
			SpaceGetRxhtmlArgs returnArgs = new SpaceGetRxhtmlArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceUploadArgs {
		public String space;
		public String gc = "no";
		public String root = "null";
		public String file;
		public static SpaceUploadArgs from(String[] args, int start) {
			SpaceUploadArgs returnArgs = new SpaceUploadArgs();
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
					case "-r":
					case "--root": {
						if (k+1 < args.length) {
							returnArgs.root = args[k+1];
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceDownloadArgs {
		public String space;
		public static SpaceDownloadArgs from(String[] args, int start) {
			SpaceDownloadArgs returnArgs = new SpaceDownloadArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceListArgs {
		public String marker = "";
		public String limit = "100";
		public static SpaceListArgs from(String[] args, int start) {
			SpaceListArgs returnArgs = new SpaceListArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceUsageArgs {
		public String space;
		public String limit = "336";
		public static SpaceUsageArgs from(String[] args, int start) {
			SpaceUsageArgs returnArgs = new SpaceUsageArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceReflectArgs {
		public String space;
		public String marker;
		public String output;
		public String key;
		public String limit = "336";
		public static SpaceReflectArgs from(String[] args, int start) {
			SpaceReflectArgs returnArgs = new SpaceReflectArgs();
			String[] missing = new String[]{"--space", "--marker", "--output", "--key", };
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
					case "-k":
					case "--key": {
						if (k+1 < args.length) {
							returnArgs.key = args[k+1];
							k++;
							missing[3] = null;
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceSetRoleArgs {
		public String space;
		public String marker;
		public String email = "";
		public String role = "none";
		public static SpaceSetRoleArgs from(String[] args, int start) {
			SpaceSetRoleArgs returnArgs = new SpaceSetRoleArgs();
			String[] missing = new String[]{"--space", "--marker", };
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
							missing[1] = null;
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceGenerateKeyArgs {
		public String space;
		public static SpaceGenerateKeyArgs from(String[] args, int start) {
			SpaceGenerateKeyArgs returnArgs = new SpaceGenerateKeyArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class SpaceEncryptSecretArgs {
		public String space;
		public static SpaceEncryptSecretArgs from(String[] args, int start) {
			SpaceEncryptSecretArgs returnArgs = new SpaceEncryptSecretArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class AuthorityCreateArgs {
		public static AuthorityCreateArgs from(String[] args, int start) {
			AuthorityCreateArgs returnArgs = new AuthorityCreateArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class AuthoritySetArgs {
		public String authority;
		public String keystore;
		public static AuthoritySetArgs from(String[] args, int start) {
			AuthoritySetArgs returnArgs = new AuthoritySetArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class AuthorityGetArgs {
		public String authority;
		public String keystore;
		public static AuthorityGetArgs from(String[] args, int start) {
			AuthorityGetArgs returnArgs = new AuthorityGetArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class AuthorityDestroyArgs {
		public String authority;
		public static AuthorityDestroyArgs from(String[] args, int start) {
			AuthorityDestroyArgs returnArgs = new AuthorityDestroyArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class AuthorityListArgs {
		public static AuthorityListArgs from(String[] args, int start) {
			AuthorityListArgs returnArgs = new AuthorityListArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class AuthorityCreateLocalArgs {
		public String authority;
		public String keystore;
		public String priv;
		public static AuthorityCreateLocalArgs from(String[] args, int start) {
			AuthorityCreateLocalArgs returnArgs = new AuthorityCreateLocalArgs();
			String[] missing = new String[]{"--authority", "--keystore", "--priv", };
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
					case "-p":
					case "--priv": {
						if (k+1 < args.length) {
							returnArgs.priv = args[k+1];
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
			System.out.println("Display Help");
		}
	}
	public static class AuthorityAppendLocalArgs {
		public String authority;
		public String keystore;
		public String priv;
		public static AuthorityAppendLocalArgs from(String[] args, int start) {
			AuthorityAppendLocalArgs returnArgs = new AuthorityAppendLocalArgs();
			String[] missing = new String[]{"--authority", "--keystore", "--priv", };
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
					case "-p":
					case "--priv": {
						if (k+1 < args.length) {
							returnArgs.priv = args[k+1];
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
			System.out.println("Display Help");
		}
	}
	public static class AuthoritySignArgs {
		public String key;
		public String agent;
		public String validate = "null";
		public static AuthoritySignArgs from(String[] args, int start) {
			AuthoritySignArgs returnArgs = new AuthoritySignArgs();
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
			System.out.println("Display Help");
		}
	}
	public static class InitArgs {
		public static InitArgs from(String[] args, int start) {
			InitArgs returnArgs = new InitArgs();
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
			System.out.println("Display Help");
		}
	}
}
