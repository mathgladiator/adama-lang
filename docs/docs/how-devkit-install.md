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

Due to sillyness, there is a jar checked into the repo, so you can skip down to using the Dev Kit. However, you do need Java 11 installed.

## Building Instructions

Adama uses Java 11 and Maven, and has a simple build script. Running the python script:

```sh
./build.py jar
```

will create ```demo/devkit.jar``` which is the jar.

## Using Dev Kit

### Running Demo

A fast way to get familiar to look at the demo. Since the devkit is just an HTTP server, you can run the server within the demo directory.

```sh
cd demo
jar -jar devkit.jar
```

### About usernames and passwords
The DevKit allows any username with the password 'pw'. This silly convention allows you to get going faster, and you use a multi-tab container to deal with multiple users.

As a future ideal, it would be nice to have multiple users within iframes. This requires finalizing some design issues around users.