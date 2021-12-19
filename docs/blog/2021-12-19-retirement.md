---
slug: retirement-going-all-in
title: I retired so I can focus on my open source project
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, rambling, errors]
---
Adama is about to get a whole lot of love, and I intend to talk strategy today. As a side project, I could wander the desert for a while without much progress. It was easy to forgive as there was other shit distracting me, and my role as  principal engineer was already hard enough.

As I look forward as a retiree, I have to ask myself what I really want from this project and what does success mean? I will run into the contentious trinity of why, what, and how. This trinity spawn different roles starting with the entrepreneur who manifests a chaotic vision which describes the world in a new way to answer why. Since nothing emerges from chaos without effort, the manager role emerges to assemble the forces of good to bring order to the world via focus and priorities to identify the what. With the chaos and order in an epic struggle, some poor S.O.B has to get to work and suffer the how.

In my career, I have my interactions with all three elements of this trinity. For some reason, I enjoy the how too much, but I take it to a masochistic artistic level (100% unit test code coverage, really? yes). Unfortunately, a business born from the how is almost certainly doomed. The question then is can I take my ideas and pivot into something that is more useful for more people? Can I shoulder the burden of vision and the stress of management to marshal troops towards the a grand endeavor.

Well, my ego says yes, but do I want to?

Is this wise?

Is that who I am?

This is why the most important question is what kind of business do I want? Let’s suppose I start a new kind of infrastructure business. It would be fun to create a new kind of SaaS, and there are interesting business side things to go learn and do. Learning is fun!

However, I have a decade of infrastructure knowledge, and I can’t do that 100% alone as there will be issues that will outstrip my capacity to deal with them. Infrastructure is a hard job because you have to balancing pushing forward with an iron ball chained to your leg. This kind of business would ultimately become a prison of sorts, and I am [biasing towards maximum freedom.](https://sahillavingia.com/work) 

Paradoxically, the best way to demonstrate what I have is via some kind of SaaS offering. Now, I must reflect on why running infrastructure is so damn hard. At core, it boils down to minimizing downtime to maximize reliability. I could write much on what it takes to build reliable services, but the key is to contain and expect chaos. The hard part is being honest to customers about reality.

Honesty is probably the #1 job for any infrastructure service. Reality is hard to contend with.

Sadly, the biggest source of chaos is change from those pesky humans as all services tend to get more reliable during the holidays because engineers are not allowed to do anything. Perhaps, the game is all about expectation management. Since I don’t have the team(s)  to manage a six nine service sanely, I just need to set reasonable expectations.

This means that I need to think about how a pricing plan sets expectations, but what about those customers with high demands? That’s where open source comes into play. I neither can nor want to build a six nine back-end for the products I want to build with my ideas, and I will not have the abundance of failures to polish against (it’s entirely possible with a handful of hosts to wing it and never experience any problems). For the products I intend to ship (i.e. board games), I can have 10 minutes of down-time within a week. Thus, I just need to ship something remarkably simply and be honest about the failure modes.

The key challenge of building a six-nine service is all the stuff you need to do it, and no two companies are alike in their choices. So, the key Adama technologies will always be open source such that people can take the core language and plug into their ecosystem. Fortunately, I know enough about how to build six nine services such that I can properly layer everything.

By strategically punting on the exceptionally hard infrastructure problems, I don’t invest in an area where a company (like Amazon) could do it much better. In fact, if Amazon were to pick it up, then all the better. Perhaps, my legacy in this life will fully manifest if someone gets their principal promotion by doing the hard people work of selling Amazon on offering Adama as a Service.

This SaaS game may be winnable if I keep things simple, well documented, don’t over-promise, keep strong boundaries around hard problems, set customer expectations, and use my dependencies well. This also positions my efforts towards the important work of features and community.

Now, ultimately, I’m building Adama so I can use it for building board games, but there is so much potential when you combine a “micro-vm” with durable storage and a socket. It has this trinity of networking, storage, and compute in a nice box. The key question which I return to is: what does success mean? Well, it means that people find Adama fun to work with and helpful in building online products or games.

This helps focus my decision making since I can continue to open source practically everything beyond the language, and I’m exceptionally happy with a permissive license. As of today, the new license is MIT.

The mission for 2022 is to launch a reasonable and minimal SaaS offering to enable people to cut their teeth, launch small web properties, and unblock my board game ambitions. For massive scale, the blueprint will be available, but will require further investments.
