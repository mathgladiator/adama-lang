# Installing the tool

First thing you need to do is install Java.
Either use your distribution's version of Java 11+, or [please refer to Oracle's website for how to install Java 17](https://www.oracle.com/java/technologies/downloads/#jdk17-windows).
You can check that you ready when the command:

```shell
java -version
```

shows something like
```shell
openjdk version "11.0.13" 2021-10-19
OpenJDK Runtime Environment (build 11.0.13+8-Ubuntu-0ubuntu1.20.04)
OpenJDK 64-Bit Server VM (build 11.0.13+8-Ubuntu-0ubuntu1.20.04, mixed mode, sharing)
```

That's all you need for Adama to work. Once Java is working, you can download the latest jar using wget or [download directly](http://releases.adama-platform.com/adama.jar) 

```shell
wget --server-response http://releases.adama-platform.com/adama.jar
java -jar adama.jar
```

to get help on how to use the jar. [The next step is to initialize your developer account.](01-init.md)

**Bonus** If you are concerned about data-integrity of the jar, then you can get base-64 digests via curl:
```shell
curl http://releases.adama-platform.com/adama.jar.md5
curl http://releases.adama-platform.com/adama.jar.sha-256
```