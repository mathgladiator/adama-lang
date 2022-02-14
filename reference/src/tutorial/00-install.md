# Installing the tool

First thing you need to do is install Java. [Please refer to Oracle's website for how to install Java 17](https://www.oracle.com/java/technologies/downloads/#jdk17-windows), and you can check that you ready when the command:

```shell
java -version
```

shows something like
```shell
openjdk version "11.0.13" 2021-10-19
OpenJDK Runtime Environment (build 11.0.13+8-Ubuntu-0ubuntu1.20.04)
OpenJDK 64-Bit Server VM (build 11.0.13+8-Ubuntu-0ubuntu1.20.04, mixed mode, sharing)
```

That's all you need for Adama to work. Once Java is working, you can download the latest jar using wget or pull from . (TODO WHERE TO DOWNLOAD OFFICIAL RELEASES) and then run

```shell
#TODO wget http://releases.adama-platform.com/latest/adama.jar
java -jar adama.jar
```

to get help on how to use the jar. [The next step is to initialize your developer account.](01-init.md)
