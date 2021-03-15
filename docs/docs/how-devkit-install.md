---
id: how-devkit-install
title: Build and play with demo
---

TODO:
- [x] add some content that kind of works (at least for me)
- [ ] provide setup instructions for linux (Debian, RHEL) for Java (just sources? talk about windows subsystem)
- [ ] provide steps to validate your install works well
- [ ] validate build instructions
- [ ] Install the dev-kit (where should we put it? is there a standard toolset for making linux java binaries? maven thing)
- [x] outline expectations for the demo and the password stuff
- [ ] sort out users social join

## Warning: This is a giant mess right now!

Due to sillyness, there is a jar checked into the repo, so you can skip down to using the devkit. However, you do need Java 11 installed.

## Building Instructions

Before you build Adama, you must first install [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) and [Apache Maven](https://maven.apache.org/download.cgi).

With Java 11 and Maven installed, you can then run the Adama build script. The following Python script builds Adama:

```sh
./build.py jar
```

The build script will create ```demo/devkit.jar``` which is the jar.

## Using the Devkit

### Running the Adama Demo

A fast way to familiarize yourself with Adama is to run the demo. Since the devkit is just an HTTP server, you can run the server within the demo directory using the following commands:

```sh
cd demo
jar -jar devkit.jar
```

### About the Demo Username and Password
To help familiarize yourself with the devkit, it allows any username with the password 'pw' to authenticate. These credentials allow you to easily get started, and you can use a multi-tab container to manage multiple users.

In the future, Adama will have multiple users managed within iframes, but this requires changes to design.
