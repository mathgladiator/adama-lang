#!/usr/bin/env python3

import argparse
import os
import subprocess
import sys

parser = argparse.ArgumentParser(description="Adama Build Script")
parser.add_argument('--clean', '-c', default=False, action="store_true", help="Run mvn clean before building")
parser.add_argument('--jar', '-j', default=False, action="store_true", help="Build adama.jar")
parser.add_argument('--fast', '-f', default=False, action="store_true", help="If building --jar or --all, Skip Tests")
parser.add_argument('--client', '-cli', default=False, action="store_true", help="Build javascript client")
parser.add_argument('--all', '-a', default=False, action="store_true", help="Alias to --client and --jar")
parser.add_argument('--generate', '-g', default=False, action="store_true", help="Generate contrib")
parser.add_argument('--java-path', default="java", help="Defaults to `java`")
parser.add_argument('--mvn-path', default="mvn", help="Defaults to `mvn`")
args = parser.parse_args()
args.client = args.client or args.all or args.generate
args.jar = args.jar or args.all

ROOT_PATH = os.path.dirname(os.path.abspath(__file__))


class ansi:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'
    ESC = '\033[0m'


def execute_cmd(cmd: str, cwd: str = ROOT_PATH, hide: bool = False) -> int:
    stdout = subprocess.DEVNULL if hide else None
    stderr = subprocess.DEVNULL if hide else None
    sp = subprocess.Popen(cmd, shell=True, cwd=cwd, stdout=stdout, stderr=stderr)
    return sp.wait()


def fail(flag, message):
    print(f"\n{ansi.FAIL}Failed to execute {ansi.BOLD}{flag}{ansi.ESC}", file=sys.stderr)
    print(f"{ansi.BOLD}{message}{ansi.ESC}", file=sys.stderr)
    exit(1)


made_jar = False
if execute_cmd(f"which {args.java_path}", hide=True) != 0 and not os.path.exists(args.java_path):
    fail("clean", f"`{args.java_path}` is not a valid java path")
if execute_cmd(f"which {args.mvn_path}", hide=True) != 0 and not os.path.exists(args.mvn_path):
    fail("clean", f"`{args.mvn_path}` is not a valid mvn path")

execute_cmd("mkdir release -p")

if args.clean:
    if execute_cmd(f"{args.mvn_path} clean") != 0:
        fail("clean", "Failed to clean! Is the mvn path correct?")

if args.client:
    cwd = os.path.join(ROOT_PATH, 'clientjs')
    if execute_cmd("./build.sh", cwd) != 0:
        fail("client", "Failed to build libadama.js")
    cmd_args = ["cp", os.path.join(cwd, "libadama.js"), os.path.join(ROOT_PATH, "release")]
    execute_cmd(" ".join(cmd_args))

if args.jar:
    cmd_args = [args.mvn_path, "package"]
    if args.fast:
        cmd_args.append("-DskipTests")
    if execute_cmd(" ".join(cmd_args)) == 0:
        made_jar = True
    else:
        fail("jar", "Failed to build mvn")

if made_jar:
    cmd_args = ["cp", "./cli/target/cli-MAIN-jar-with-dependencies.jar", "./release/adama.jar"]
    if execute_cmd(" ".join(cmd_args)) != 0:
        fail("jar", "Failed to copy adama.jar")

if args.generate:
    cor_path = os.path.join(ROOT_PATH, "core")
    contribs = {
        cor_path: ('tests-adama', 'tests-rxhtml'),
        ROOT_PATH: ('make-cli', 'make-api', 'make-et', 'make-codec', 'bundle-js', 'copyright', 'version'),
    }
    adama_jar = os.path.join(ROOT_PATH, 'release', 'adama.jar')
    for cwd, contrib in contribs.items():
        for item in contrib:
            if execute_cmd(f"{args.java_path} -jar {adama_jar} contrib {item}", cwd=cwd) != 0:
                fail("generate", f"Failed to generate contrib {item}")

print(f"\n{ansi.OKGREEN}Build Success!{ansi.ESC}")
