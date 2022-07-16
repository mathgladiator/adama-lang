# Creating a space

Fundamentally, a space is a namespace/container for Adama documents which share a common document script.

Spaces are global resources, so you may need to be a bit clever with how you name them as conflicts between other developers can happen.
This is the rough spot of the tutorial since I can't tell you exactly what to type as you will have to invent a name.
However, I can provide an example of creating a space.

```shell
java -jar adama.jar space create --space chat001
```

And, if you don't see a bloody mess of an error message, then your space is created! Huzzah!

You can poke around the space sub command as well. For instance, you can investigate your options by invoking the help on the space sub command via

```shell
java -jar adama.jar space help
```

And one option available to you is to list all your spaces via

```shell
java -jar adama.jar space list
```

For me, using my freshly made account to test this tooling and your experience, produced a list of JSON object containing

```json
{
  "space" : "chat001",
  "role" : "owner",
  "created" : "2022-02-09",
  "enabled" : true,
  "storage-bytes" : 0
}
```

This object reveals the name of the space, role of the person doing the listing, date when the space was created, whether or not the space is currently enabled, and finally the total storage used by the space.

The space is now created in an empty state, so [let's create some people to leverage it.](03-authorities.md)

