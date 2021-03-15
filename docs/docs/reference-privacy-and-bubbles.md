---
id: reference-privacy-and-bubbles
title: Privacy & Bubbles
---

## Fast Intro

The document and records contain fields and formulas that are privacy checked before they can be viewed by users. This section outlines how data is exposed to users. At the core, each field is prefixed with a privacy modifier.

| Modifier | Effect |
| --- | --- |
|  public | Anyone can see it |
|  private | No one can see it |
| viewer_is&lt;field&gt; | Only the viewer indicated by the given field is able to see it |
| use_policy&lt;policy&gt; | Validate the viewer can witness the value via code; policies are defined within documents and records via the ```policy``` keyword. |

These modifiers are great for revealing simple data common between all viewers of the document. Viewer-dependent data is achievable via a privacy bubble using the ```bubble``` keyword.

```adama
record Row {
  // it's public
  public int pub;

  // it's private, no one can see it
  private int pri;

  // a private person
  private client who;

  // data that is only visiable to the who
  viewer_is<who> int whos_age;

  // a custom policy based on code
  use_policy<my_policy> int custom;

  // defining the polcy
  policy my_policy(c) {
    return pub < pri;
  }

  require p1;
}

table<Row> tbl;

// reveal mine via a formula where me represents the client viewing the document
bubble<me> mine = iterate tbl where who == me;
```

## Diving Into Details

### public/private
The **private** modifier hides data from users. The **public** modifier discloses data to users. If no modifier is specified, the default is **private**.

### client_is&lt;&gt;

Inside the angle brackets denotes a variable local to the document or record which must be of type client. For instance

```adama
client owner;
viewer_is<owner> int data_only_for_owner;
```

Here, the field owner is referenced via the privacy modifer for data_only_for_owner such that only the device/client authenticated can see that data.

### use_policy&lt;&gt; &amp; policy

As visibility may depend on some intrinsic logic or internal state, ```use_policy``` will leverage code outlined via a policy. This code is then run when the client wishes to see the data.

```adama
record Card {
  // some internal state
  private bool played;

  // who owns the card
  private client owner;

  // the value of the card
  use_policy<is_in_play> value; // 0 to 51 for a standard playing deck

  // who can see the card
  policy is_in_play(who) {
  	// if it has been played, then everyone knows
  	// otherwise, only the owner can see it
  	return played || owner == who;
  }
}
```

### bubble&lt;&gt;

While privacy policies ensure compliance, we can leverage bubbles to efficiently query the document based on the viewer.

```adama
table<Card> deck;

bubble<who> hand = iterate deck where owner == who;
```
