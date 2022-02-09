# Creating a space

Spaces are globally named, so you may need to be a bit clever with how you name them.
This is the rough spot of the tutorial since I can't tell you exactly what to type as you will have to invent a name.
However, I can provide an example of creating a space.

```shell
adama space create --space chat001
```

And, if you don't see a bloody mess of an error message, then your space is created! Huzzah!

You can poke around the space sub command as well. For instance, you can investigate your options by invoking the help on the space sub command via

```shell
adama space help
```

And one option available to you is to list all your spaces via

```shell
adama space list
```

For me, using my freshly made account to test this tooling and your experience, produced a list of JSON object containing

```json
{
  "space" : "chat001",
  "role" : "owner",
  "billing" : "free",
  "created" : "2022-02-09",
  "balance" : 0,
  "storage-bytes" : 0
}
```

This object reveals the name of the space, role of the person doing the listing, billing plan associated with the space, date when the space was created, current balance available to the space, and finally the total storage used by the space.

The space is now created in an empty state, so [we can move on and make the space useful.](03-space.md) 