---
id: reference-comments
title: Comments
---

## Fast Intro

Since code should be manageable and understandable over time via different people, Adama supports C style comments.
```adama
// This is a comment with a single line which terminates at the ->\n

/* This is a comment with multiple lines

It can have oh so many lines.

* <- why not? it's freeform madness!
*/

int x /* and can be embedded anywhere */ = 123;
```

This enables comments to be sprinkled liberally with Adama code.

## A Tiny (kind of useless) Technical Detail

Comments are part of the [syntax tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree) for Adama as part of hidden meta-data on tokens. This is so comments are available to tooling such that error messages can more helpful, but also so documentation can be generated and shared with document consumers.

Comments associate to tokens in either a forwards or backwards manner. Comments on multiple lines such as
```adama
/**
 * foo
 */
```
always associate forward. For instance the comment in
```adama
/* age of the user */
int age;
```
is associated to the 'int' token. Whereas single line comment like
```adama
int age; // age of the user
```
usually associate backwards. Here, the comment is associated with the ';' token. Sometimes, the single line comment will associate forward but only after a multi-line comment has been introduced. For instance
```adama
/* the age */ // of the user
int age;
```
will associate both comments to the 'int' token.