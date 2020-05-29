---
id: what-privacy-as-a-first-class-concept
title: Privacy as a First Class Concept
---

Don't show others my hand
-------------------------

So, exposing data from a table opens up the challenge of "well, wait, who can see that data?" One solution is to ensure all queries are respectful of the board game rules, but this requires ensuring all leafs in a forest are doing the right job. It would be better if the actual data could be encoded with who can see it. We start by defining the simplest model. So, here, we define an *Account* which

```adama
record Account {
  public string name;
  private string bank_account;
}

table<Account> accounts;
```

Observe, the **public** and **private** have been hijacked to mean "which humans can see this data!", and this mirrors my understanding of field visibility when I was a kid.

This was born from how to represent board game state such that secrets do not flow incorrectly to the wrong people. Afterall, if I can see your hand in poker, then you lose.

TODO:
* Board games depend on secrets
* Where do databases go wrong
* Bugs, Proofs, Containment
* Trust

Mental Model: Personal Data Security Guard
------------------------------------------
TODO