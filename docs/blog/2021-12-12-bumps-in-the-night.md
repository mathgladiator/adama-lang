---
slug: bumps-in-the-night
title: On things going bump in the night
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, rambling, errors]
---
A lofty goal is producing stable code which does not require maintenance beyond adjusting for the humans that use the artifacts of Adama. For instance, if you use Adama for calculating your taxes, then you’ll need yearly maintenance. If you use Adama for a chess back-end, then it should outlast our collective memory of chess.

Since I’m about to be working a great deal more on Adama, I intend to focus on fixing some of my early mistakes. However, these may not seem like mistakes, and I’ll layout out the core tasks I must perform at the end. Before I address that plan, I want to lay out how Adama perceives problems. For you see, I’ve been coding for close to three decades (15 years professionally). The one truth that I have come away with is that this shit is hard. It is sooo hard, and I have developed nothing but empathy for junior engineers starting their journey.

The reason it is hard is because it is difficult enough to figure out the happy path and yet bad things will happen. Bad things outside of your control will happen. You are told that is just life, and you just must suffer it. Disks will fail. Networks will get congestion. Operators will turn off machines. The OS will betray you. Simply put: Writing reliable and robust code is hard.

This is at core why Adama’s model has no disk, no network, and has a memory model that enables the entire document to be replicated without the developer thinking about it nor the user experience being negatively affected. Deterministic behavior is built in just in case the hardware you execute on goes faulty or bits get flipped due to cosmic radiation.

The Adama model is a safe space, and the only remaining problem is you… the human developer.

## The core challenge is discipline

I wish I could say that there was a magic book which could be written and digested instantly by new developers, but this is this boundary between what the developer tools help with and the discipline a developer must have.

The question then is how does Adama help developers.

Adama starts with static typing because that eliminates a whole class of silly issues, but this illustrates the boundary perfectly. You could use a dynamically typed language, but this requires the developer to have the discipline to look up what arguments are and the expectations for each function. With static typing, your discipline is reduced because the type can tell or force you to look in the right direction.

## Null really was a mistake

Adama takes a position that null was a [big but necessary mistake](https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare/). We had to evolve with null to realize it was a problem. Adama does not have typical reference types and therefore doesn’t have null by fiat. Instead, Adama embraces the Maybe&lt;&gt; (i.e. Optional&lt;&gt;) monad type.

This creates a burden on the happy path which requires the developer to, at the very least, contend with the possibility of something going wrong. In my experience, optimistic code tend to create more technical debt and unreliability. When developers are, at the very least, forced to contend with the possibility of a problem then they at least think about the problem being produced.

This is one of the good elements about checked exceptions is that they at least force developers to think about the issue or pass the buck to the next developer. For serious shops, doing the wrong thing should be caught during a simple code review pass.

An interesting problem, which I am considering of solving, is to make the conversion of functions which don’t operate on maybe types to implicitly operate on them. This is the philosophy guiding the stdlib for strings where maybe&lt;string&gt; has all the methods of String, and this allows simply chains to be formed like:

```adama
maybe<string> x = y.left(4).multiply(2).upper();
```

## Exceptions are awful

Adama also takes the position that exceptions are just messy hacks. Sure, they make some problems easier that require back-tracking like n-queens. However, for most of my career they are simply used as a signal that something bad happened.

Adama doesn’t have exceptions as this stems from the precision of the memory model that Adama has. An unsolved problem right now in the design of Adama is what do exceptions do with side-effects. For example
```adama
x = 123;
double z = 1 / 0;
```

When that bad division by zero happens,  do we rollback the change to x? I haven’t solved this yet, and since most bad things that happen which require exceptions don’t happen, then I doubt they will come into play.

It's worth noting that Adama supports message abortion via the ```abort``` keyword which will rollback state prior to the message. Internally, this uses exceptions, and developers can leverage maybe&lt;&gt; with abort to get many of the benefits.

## Division

The lack of exceptions makes division especially problematic, and this is a big bug in Adama that I want to resolve before I start telling more people about it. The question then is what do you? Well, it means that X / Y has the type of maybe&lt;double&gt; where lack of a value represents undefined.

As a minor consequence, this eliminates the /= and %= operator which I am blowing away now in this diff.

Since Adama aims to be a language for non-hard-core developers, the principal of least surprise comes into play such that int / int is a double as you would expect as a normal human. 5 / 2 should be 2.5 without a pedantic “well, the types are both integers therefore this is really the special integer division operator rather than the typical division”

## Imaginary numbers

Also related to exceptions and mathematics is what to do about the square root of negative numbers. Well, this will instead require Adama pick up complex numbers.

## A few next steps

As this entry is coming with the diff to remove both /= and %=, the next steps are as follows.
1. Fix division and % to return maybe&lt;&gt; type
2. Introduce a new complex number type and fix sqrt
3. Look into adding maybe&lt;&gt; arithmetic such that math can operate on these values to minimize the developer pain of division being awful. Perhaps, just handling division is sufficient, but I also want to clean up some of the type system as well.

As a consequence, this will require the stdlib to be exceptionally precise. I may have to think hard about further quality of life improvements. For now, the goal is to ensure that things are correct.