---
slug: performance-funday
title: Performance Funday
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, ui]
---

A non-productive theme for July is to jump into performance and measure a few things. The hope is that this search will produce some low-hanging fruit that I can exploit, and I also intend to validate correctness on many things since there are some slap dashing stupid stuff. A core reason for the urgency beyond being interesting work is that I want to utilize permutation [testing as a way of finding novel test-cases](/blog/going-ham-on-parser). Given that it took a bit over half a day to simulate playing 1.2 M games, I would like to be able to cut that time down.

Test Setup
----------

I am going to leverage the current game prototype which comes in at a massive 4.8 KLOC, and the goal is simulate random players playing a game. To factor out the randomness, I’m going to seed the random number generator such that the game play and decisions are predictable. I tweaked the seed parameters until I found a meaty enough game to stress the system.

This meaty game will be played 101 times, and the results from the first test will be thrown away as it is heavily influenced by the JVM warming up. Each test afterwards will produce these data columns

* Time: proxy measure for CPU (and memory induced GC pressure) within a single thread.
* “Billing Cost”: number proportional to the amount of work done (the language bills by the statement and a few other things)
* Decisions: number of decisions taken by all the agents within a game

It is worth noting that “billing cost“ is expected to remain constant within a single test run, and the number of decisions is expected to remain constant over all runs because it is a measure of correctness. Decisions must be fixed at 798. Over those 100 runs, the average time will be a sufficient measure of performance. I intend to eyeball variance, but I leaning on an average to factor out GC cost. With all this said and done, the initial data is thusly:

| ms | billing cost |
| --- | --- |
| 739 | 12810563 |

That is approximate 1ms per game decision. This actually feels super slow, and as a secondary goal of this work is to build tools to dive into issues. My intent during this day is to just address the language and not push back on the user-space changes.


Exploit Primary Key
-------------------

Every record has a primary key called ```id``` which is unique and an integer, and it is fairly common to leverage this to find items by primary key. Since the only way to get access to data within a table is via a [language integrated query](/docs/reference-tables-linq), we must contend with the query syntax. The first task was to analyze ```where``` expressions to extract associations mapping the primary key to an expression. The initial algorithm for this is simple, and I'll write with pseudo-ish-code here (I'm actually using Java, but it is too verbose):

```python
function extract(expr):
  if expr is Parenthesis:
    return extract(expr)
  if expr is BinaryOp(&&):
    result = extract(expr.left)
    if !result:
      result = extract(expr.right)
    return result
  if expr is BinaryOp(==):
  	if expr.left == Lookup("id"):
      return expr.right
    if expr.right == Lookup("id"):
      return expr.left
    return null
  return null
```

This identified many cases where the primary key was part of the where clause, and the results were.... disappointing. The billing cost went down 2.5% while the time cost went down only 1.4%.

| ms | billing cost |
| --- | --- |
| 728 | 12483470 |

Ok, this is going to be a slog.

Direction Seeking: What if we don’t update client views
-------------------------------------------------------

The tool at hand doesn’t really need to see the individual view per agent, so as a method of developing a direction we can experiment and simply turn that feature off and see what happens. This enables a quick way of developing a sense of where time is worth spending. We do that, and the data is revealing.


| ms | billing cost |
| --- | --- |
| 510 | 3689641 |

This shows potential in that the billing cost is overblown and the client views represent close a third of our CPU cost, but billing was reduced by a whopping 70%. This fundamentally requires investigation. Now, this is not the way, but it gives a tingle to explore. Please ignore the above data.

Cache Record to Json
--------------------

In thinking about how to optimize client views, we can exploit caching of viewable records between the four agents. However, this requires a consultation with each record’s privacy policy. Statically, we can devise a very simple predicate to eliminate the need to compute per-user views. A record's per-user view can be cached if:

* Has no [bubbles](/docs/reference-privacy-and-bubbles)
* Has no visibility requirements.
* The privacy for each field is either [public or private](/docs/reference-privacy-and-bubbles)
* The fields are all primary data (i.e. ints and strings) (i.e. neither records nor tables)

With this, we can stash a copy of the JSON within the record and let record invalidation blow it away on change. By leveraging the reactive elements, we not only cache between viewers but also over time as well. This result in new data.

| ms | billing cost |
| --- | --- |
| 647 | 10125290 |

This is very encouraging as the billing cost has now been reduced by 20% and CPU cost is down 12.4%. In terms of the goal, that’s over an hour of time saved in the experimental test.

Quick Experiment: Turning Off Code Coverage
-------------------------------------------

Currently, the document keeps track of every single statement which gets executed in a giant list... The question at hand is how much that costs.

| ms | billing cost |
| --- | --- |
| 620 | 10125290 |

This is slightly exciting for production as this takes us towards a 16% CPU reduction, but this is a requirement for the testing I hope to achieve. As I hope to guide agent decisions towards maximimzing code coverage, I must turn this off... for now. Please ignore the above data.

Direction Seeking: Bad Indexing the Tables
------------------------------------------

Indexing is the bees knees, so let’s introduce indexing to our tables!

This is not an easy endeavor, so we first set out to measure opportunity. We do that by doing half the hard work now, then exploit that work to measure and validate correctness. What we measure is the potential to do good work if we finish the other half of the hard work.

First, we have to leverage and extend the analysis work done via extracting the primary key and generalizing it to any field. This was added to the code-generation such that a ```where``` could generate a flat array of which columns map to which value. Second, each item must reveal their data in an easy to consume way. Third, we must measure how effective the where cause could be enhanced with indexing. This work enables a very simple algorithm to test

```java
  /**
   * @param clause an integer array mapping columns to values [col0, testValue0, col1, testValue2, ..., colN, testValueN]
   * @param value an integer array represent a particular item's columns [value0, value1, ..., valueM]
   * @param effectiveness how effective a column was at rejecting the item
   */
  private static boolean slowTest(int[] clause, int[] value, int[] effectiveness) {
    boolean result = true;
    for (int k = 0; k + 1 < clause.length; k += 2) {
      if (value[clause[k]] != clause[k+1]) {
        effectiveness[k/2]++;
        result = false;
      }
    }
    return result;
  }
```
Now, this code is not great for a variety of reasons, but we can run it prior to executing the ```where``` clause and measure changes to billing cost. Well, data came in, and it was both suprising and not so.

| ms | billing cost |
| --- | --- |
| 810 | 2170041 |

A 10% CPU bump (yikes), but a 83% reduction to billing cost (woah). OK, this is very pro-customer, however this does not help me. It is however encouraging that if I roll up my sleeves, then I can get some better results.

Index all things?!?
-------------------

The challenge of indexing in a reactive system is not paying the implicit insertion cost. That is, stuff will change, and you don’t want to constantly insert and remove items. You need to be a bit lazy, and for indexing this requires some book-keeping and allowing some slack to emerge. Each column must have its own index, but when things change they must be either be removed or included in a catch-all bucket since the indexing is indeterminate in a lazy system.

```java
  private final ReactiveIndex<Ty>[] indices;
  private final TreeSet<Ty> unknowns;
```

The key is that the combinatation of column specific indicies plus this catch all bucket form a super-set of the right answers you seek, and the hope is that the resulting set is smaller consideration pool. First, we index everything and measure the overhead:

| ms | billing cost |
| --- | --- |
| 695 | 10125290 |

Yikes! It is already adding cost to just index the data. However, we have yet to use the data. One more blind trial, but let's compute the needed super set.

| ms | billing cost |
| --- | --- |
| 832 | 10125290 |
 
Oh no, this is not trending good at all! Let's use it.....

| ms | billing cost |
| --- | --- |
| 734 | 2361554 |

I am Jack's sense of disappointment. OK, this is proving to be my [white whale](https://en.wikipedia.org/wiki/Moby_Dick_(whale)). There are many problems with the approach I have taken on this... Part of the problem is that I’m indexing every enum, integer, and client variable. [Pareto is proving a relevant observation](https://en.wikipedia.org/wiki/Pareto_principle), so I’m going to need to take a break... *Several hours pass*

I’m going to restore the code I used for the bad indexing, then put it behind a condition and introduce a DocumentMonitor. The role of the DocumentMonitor is to stream data out to me which I can use, and I have a flag called “measureTablePerformance” which will use that bad code in special circumstances.

```java
public interface DocumentMonitor {

  /** should the runtime measure table performance */
  public boolean measureTablePerformance();

  /** emit a single datapoint about table performance */
  public void recordTablePerformanceInstance(
    String tableName,
    String colummName,
    int total,
    int effectiveness);
}
```

With hope, the if statement doesn't regress the performance...

| ms | billing cost |
| --- | --- |
| 646 | 10125290 |

It's basically the same, so let's use it to emit some data, collect that data into a table, and then review the table.

| table | column | calls | total | effectiveness | % |
| --- | --- | --- | --- | --- | --- |
| skill_cards | location | 71199 | 6763905 | 5298816 | 78.34% |
| skill_cards | skill | 57681 | 5479695 | 4383756 | 80% |
| skill_cards | owner | 30504 | 2897880 | 2260607 | 78.01% |
| civilian_ships | status | 31507 | 378084 | 363870 | 96.24% |
| military | type | 9394 | 396314 | 340569 | 85.93% |
| civilian_ships | space_key | 28281 | 339372 | 284757 | 83.91% |
| crisis_deck | revealed_to | 4000 | 280000 | 280000 | 100% |
| military | space_key | 9093 | 383151 | 237907 | 62.09% |
| destinations | revealed_to | 4000 | 174328 | 174328 | 100% |
| civilian_ships | revealed_to | 3924 | 47088 | 47088 | 100% |
| loyalty_deck | revealed_to | 3924 | 42396 | 42395 | 100% |
| loyalty_deck | owner | 4068 | 43938 | 38841 | 88.4% |
| players | play_id | 7454 | 29816 | 22364 | 75.01% |
| players | location_key | 5079 | 20316 | 19538 | 96.17% |
| players | space_key | 5052 | 20208 | 16086 | 79.6% |
| locations | key | 549 | 10431 | 9971 | 95.59% |
| characters | owner | 119 | 1190 | 934 | 78.49% |
| base_stars | space_key | 496 | 992 | 846 | 85.28% |
| players | character | 188 | 752 | 684 | 90.96% |
| characters | type | 51 | 510 | 357 | 70% |
| characters | key | 22 | 220 | 198 | 90% |
| players | link | 4 | 16 | 12 | 75% |
| super_crisis_deck | owned_by | 1 | 5 | 0 | 0% |

Clearly, I want to exploit the skill_cards table since it is highly rejective. Out of 6.7M tests, 5.2M could be quickly rejected via an index. This is great! We should exploit that, so let's do it. This requires a bunch of working like: upgrading the parser, updating the code generation to present the indexing information to tables, and then upgrade where clause code generation to build the needed set. Given that the last code was mostly thrown away due to complexity, this is going to require care... _Even more hours pass_

| ms | billing cost |
| --- | --- |
| 630 | 2912569 |

OK... seriously. All that work for an additional 2% in CPU reduction. _Sigh_. On the bright side, the billing is now more customer-friendly and is down 77%. If the ```where``` clauses were more expensive, then this would be a very productive thing, so we will keep it as it is customer-friendly feature that encourages better focus on more controllable things by the customer.

Fixing a bug in the reactive tree
---------------------------------

As part of this work, something felt off as to why so much computation was happening in the first place. This triggered an audit into the code, and I had to rethink a few things from core first principles. At hand, how formulas were being computed was done poorly due to sloppy mixing of invalidation and dirty signals. The core two principles at play were:

* invalidation alwayss flow from data down to formulas
* dirty signals flow up from data to root

![invalidations and dirty signals](/img/20200704-dirty-invalids.png)

This diagram became crucial for investigating each class and making sure it conformed to the principles. All but two of the classes behaved, and the one that did not behave had a giant TODO on it. Fixing that TODO and the other class caused the system to behave, and produced new data.

| ms | billing cost |
| --- | --- |
| 550 | 2328882 |

At this point, 25% of the CPU has been reduced and 82% of the billing cost has been reduced.

Until next time.
---------------

I'll take 25% as a win for now, but I want to invest in tooling to provide better insights. There is some super dumb code within the prototype at the moment because the focus then was to ship the game in a workable form.
