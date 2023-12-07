# Kickstart a web app

The fastest way to get started with Adama is to kick start an application from a template. Run the jar via

```shell
java -jar adama.jar kickstart
```

It is going to ask for a template, then type **webapp** and hit enter. Once you give the tool a template, it is going to ask for a space name. This space name is an organizational concept for documents stored within Adama, and you can learn more via [core concepts of Adama](/concepts.md).

The space name you use should be globally unique, so enter a name like **mywebapp42** or **a-space-for-me123**; please use a good name if you plan on sharing this project with others. Once you hit enter, it is going to create the space and create a directory using the chosen space name. That's it!

Now, let's dive into the generated code.

```shell
cd mywebapp42
find
```

Will produce a nice list of files which we will walk through now.

| file               | description                                                                                                                                                                        |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| backend/*          | as you group, you'll want to organize your adama specification by breaking it up into multiple files                                                                               |
| backend.adama      | the **main** file to start with and has been populated with code from the template; this fill will be responsible for including other files via ```@import``` top level definition |
| frontend/*.rx.html | this will contain the [RxHTML](./rxhtml/ref.md) files used to build the web experience                                                                                             |
| local.verse.json   | configuration for the local devbox                                                                                                                                                 |
| README.me          | A place to put notes, and this has been seeded from the tool. Please read it!                                                                                                      |

With the directory organization available, run the devbox:

```shell
java -jar ../adama.jar devbox
```

Now navigate your browser to [http://localhost:8080](http://localhost:8080), and you'll have a local sandbox for changing the *.rx.html and *.adama files. Note, changes to the *.adama files are reflected instantly while changes to the *.rx.html files require a full screen refresh (F5) in the browser.

[With the basic shell, let's build a product](03-add-a-table.md)