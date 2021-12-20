#!/usr/bin/python
import sys
import os
fast = False
client = False
jar = False
clean = False
made_jar = False
production = False
generate = False
for arg in sys.argv:
    if arg == "fast":
        fast = True
    if arg == "production":
        production = True
    if arg == "generate":
        generate = True
    if arg == "clean":
        clean = True
    if arg == "client" or arg == "all":
        client = True
    if arg == "jar" or arg == "all":
        jar = True

if clean:
    os.system("mvn clean")

if client:
    cwd = os.getcwd()
    os.chdir("client")
    if production:
        os.system("yarn release")
    else:
        os.system("yarn build")
    os.chdir(cwd)
    args = ["cp", "./client/dist/libadama.js", "./demo/html/"]
    os.system(" ".join(args))

if jar:
    args = ["mvn", "package"]
    if fast:
       args.append("-DskipTests")
    if os.system(" ".join(args)) == 0:
       made_jar = True

if generate:
    cwd = os.getcwd()
    os.chdir("core")
    os.system("java -jar ./target/core-0.2-jar-with-dependencies.jar generate-tests")
    os.chdir(cwd)

if made_jar:
    args = ["cp", "./devkit/target/devkit-0.2-jar-with-dependencies.jar", "./demo/devkit.jar"]
    os.system(" ".join(args))
    args = ["cp", "./saas/target/saas-0.2-jar-with-dependencies.jar", "./demo/saas.jar"]
    os.system(" ".join(args))
    args = ["cp", "./cli/target/cli-0.2-jar-with-dependencies.jar", "./demo/cli.jar"]
    os.system(" ".join(args))
