# Privacy and bubbles

Adama's focus on data privacy is a critical aspect of the language and is enforced through privacy checks on document fields and records.
In this section, we will explore how Adama exposes data to users and the role of privacy modifiers in protecting sensitive information.
1At the heart of Adama's privacy system is the use of privacy modifiers, which are prefixes added to each field to control how they are accessed and by whom.
These privacy modifiers allow you to control the visibility of each field and ensure that sensitive information is only accessible to authorized users.

By carefully managing the privacy of your fields and records, you can create a secure and reliable system for managing data in your Adama applications.
This is particularly important in web and mobile applications, where sensitive information such as user credentials and financial data must be protected at all times.
Adama's privacy system offers a unique and innovative approach to data protection, and by understanding how it works, you can build applications that are both efficient and secure.
With Adama's support for privacy modifiers and other privacy features, you can create applications that meet the highest standards of data protection and ensure that your users' information is always safe and secure.

## Shared-value modifiers

These modifiers are prefixes added to each field to control how they are exposed to users, and they allow you to specify who can see the data contained within.
The following modifiers are commonly used in Adama to control field visibility:

| Modifier | Effect |
| --- | --- |
|  public | Anyone can see it |
|  private | No one can see it |
| viewer_is&lt;field&gt; | Only the viewer indicated by the given field is able to see it |
| use_policy&lt;policy&gt; | Validate the viewer can witness the value via code; policies are defined within documents and records via the ```policy``` keyword. |

```adama
record Row {
  // it's public
  public int pub;

  // it's private, no one can see it
  private int pri;

  // a private person
  private principal who;

  // data that is only visible to the who
  viewer_is<who> int whos_age;

  // a custom policy based on code
  use_policy<my_policy> int custom;

  // defining the policy
  policy my_policy(c) {
    return pub < pri;
  }

  require p1;
}

table<Row> tbl;

// reveal mine via a formula where me represents the client viewing the document
bubble mine = iterate tbl where who == @who;
```

## Viewer dependent modifiers

The ```bubble``` keyword is a powerful tool for controlling access to viewer-dependent data within a document.
Instead of using a policy to dictate who can see a shared value within a document, the ```bubble``` modifier allows you to create a custom computation for each viewer, ensuring that each user sees only the data that they are authorized to view.

One important caveat of using the ```bubble``` keyword to create privacy bubbles is that the resulting field is ephemeral and can only be seen by the connected viewer. This means that you cannot use a bubble field within the document itself, as it would not be visible to other logic.

```adama
// reveal mine via a formula where me represents the client viewing the document
bubble mine = iterate tbl where who == @who;
```

## Diving Into Details

### public/private
The **private** modifier hides data from users. The **public** modifier discloses data to users. If no modifier is specified, the default is **private**.

### viewer_is&lt;&gt;

Inside the angle brackets denotes a variable local to the document or record which must be of type client. For instance:

```adama
principal owner;
viewer_is<owner> int data_only_for_owner;
```

Here, the field owner is referenced via the privacy modifier for data_only_for_owner such that only the device/client authenticated can see that data.

### use_policy&lt;&gt; &amp; policy

As visibility may depend on some intrinsic logic or internal state, ```use_policy``` will leverage code outlined via a policy. This code is then run when the client wishes to see the data.

```adama
record Card {
  // some internal state
  private bool played;

  // who owns the card
  private principal owner;

  // the value of the card
  use_policy<is_in_play> value; // 0 to 51 for a standard playing deck

  // who can see the card
  policy is_in_play {
  	// if it has been played, then everyone knows
  	// otherwise, only the owner can see it
  	return played || owner == @who;
  }
}
```

### bubble&lt;&gt;

While privacy policies ensure compliance, we can leverage bubbles to efficiently query the document based on the viewer.

```adama
table<Card> deck;

bubble hand = iterate deck where owner == @who;
```
