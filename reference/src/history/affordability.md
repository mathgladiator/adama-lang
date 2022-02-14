# Affordability (i.e. Cheap)

We live in magical times when you can spin up a Linux machine for less than $5/mo. For the average individual, the power of that machine is unimaginable (when used efficiently). Unfortunately, this creates an illusion where things are cheap.

That $5/mo machine could power thousands of games, and you could turn that $5 into way more than $5 with a reasonable business model. You will have to, since there is a human cost for managing that $5/mo machine as machines fail. But, what happens when the revenue dips below the $5 + human cost? The game is shutdown, and a core group of fans are disenfranchised. You need not look further than [archive.org](https://archive.org/) to know that people generally have a hard time letting go of things.

Fixing this requires a new type of cloud which amortizes both the machine and human cost down. Furthermore, if the cost of a game can be driven so low, then investments could be leveraged to convert the ongoing cost to a single up-front payment.

Let's look at Amazon S3 as a model. As of 4/11/2020, Amazon S3 charges $0.023/mo per gigabyte. For modeling purposes, let's use $0.15/mo which was the price in 2010. Furthermore, embrace pessimism and assume there exists an investment vehicle which will only be able to guarantee 1% in a year forever. For the low up-front cost of $180 ($0.15 * 12 / 0.01), a single gigabyte could exist for as long as Amazon S3 exists. 

This $180 price tags feels reasonable for an important document, but a gigabyte is a lot of data. A board game at an extreme maximum may use 100KB of storage. This represents paying two cents to keep that board game's source code alive forever.

Sadly, Amazon S3 only provides storage. This is where Adama comes into the picture for compute. Adama allows a storage service to perform arbitrary-enough compute. In essence, Adama is the spark that turns dead durable storage into lively durable compute storage.

What this means is that not only will the code for a game be able to live forever, but also players will be able to play any game ever written in Adama forever. Future players will still be on the hook to pay for playing a game, but the cost of a single game now is already ridiculously cheap if you can leverage a $5/month machine to run thousands of _distinct_ games.