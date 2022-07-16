# How to create a Tic Tac Toe Game using Adama Platform

## Building the backend for your Tic Tac Toe game
In this section, we'll leverage the tools provided by Adama to build a Tic Tac Toe game. Tic Tac Toe is a game that consists of two users, one is X, and the other is O. The player that succeeds in placing three of its symbols horizontally, vertically, or diagonally is the winner.

Before writing Adama's code, we need to state the document's policy. The code snippet below allows anyone to create a document.

```adama
@static {
// This makes it possible for everyone to create a document.
create { return true; }
invent { return true; }

// As this will spawn on demand, let's clean up when the viewer goes away
delete_on_close = true;
}
```