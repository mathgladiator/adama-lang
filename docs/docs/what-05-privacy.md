---
id: what-privacy-as-a-first-class-concept
title: Privacy as a First Class Concept
---

Don't show others my hand
-------------------------

So, exposing data from a table opens up the challenge of "well, wait, who can see that data?" One solution is to ensure all queries are respectful of the board game rules, but this requires ensuring all leafs in a forest are doing the right job. It would be better if the actual data could be encoded with who can see it. We start by defining the simplest model. So, here, we define an *Account*:

```adama
record Account {
  public string name;
  private string bank_account;
}

table<Account> accounts;
```

Notice that the **public** and **private** modifiers have been hijacked to mean "which humans can see this data!", and this mirrors my understanding of field visibility when I was a kid.

This was born from how to represent board game state such that secrets do not flow incorrectly to the wrong people. After all, if I can see your hand in poker, then you lose.

Classical databases were built around security within an organization and the granularity within the database is too coarse-grained. It's up to application developers to protect data between people, and this is a heavy burden. This burden was inherited by NoSQL. Instead, Adama believes that a document should contain all access rules to data, and the language aims to simplify that process.

The end goal here is to have a language which prevents information leakage to unintended parties, so developers are not in a position to accidentally leak data as reading data is entirely controlled by the document. The document is the source of truth with regards to privacy.


Mental Model: VIP Club
------------------------------------------
The document is an exclusive club, and you have an id card. That id card is checked when entering the club via security, and it grants you access to parts of the club.
