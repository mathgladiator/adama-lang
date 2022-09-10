# Requirements driving Adama (the birth of Board Game Infrastructure)

Since answering "What is Adama" is hard, let's start with the requirements that drove Adama in the first place as that provides essential clues to why exists.

This all started after seeing TVs placed within tables to play games
(like [here](https://www.youtube.com/watch?v=o2INkNR3ap8)
, [here](https://www.reddit.com/r/DungeonsAndDragons/comments/oshm4b/new_game_tvtable_our_dm_made_200_lg_42_inch_tv/)
, [or here](https://makezine.com/article/home/fun-games/how-to-build-a-high-end-gaming-table-for-as-little-as-150/)).
These table are pretty neat, so I thought I would build one myself. Low and behold, I decided to focus on the software aspect to build a digital version of [battlestar galatica (BSG).](https://boardgamegeek.com/boardgame/37111/battlestar-galactica-board-game)

The core thesis of having the TV be the board is that this enables
easier setup and teardown,
rule enforcement,
rule automation,
rich media integration,
no lost pieces, and more.
Furthermore, by lowering the setup cost, it enables a quick play through to teach the rules and tutorials can be used to speed up the players' understanding of the game.

Since there is private state within BSG, I would need to bring people into the TV with their device.
Fortunately, everyone these days has a smart phone, so the TV would need to be a server.
We must contend with networking to deal with multiple devices owned by different people of varying degrees of capabilities.

**Requirement #1:** We need to deal with networking up-front.

As an ideal, players should be able to continue play during a network outage.
Power outages can be handled with a battery. We must allow players to be able to host locally without any cloud nonsense.

**Requirement #2:** Games should be hostable locally.

Sadly, home networking is a bit of a mess and outage are relatively a rare event.
For easier game play start, the cloud is a fantastic option for 99% of the time.

**Requirement #3:** Games should be hostable on the cloud.

The cloud opens up many possibilities, and people can play with multiple TVs distributed across various homes.
This would allow couples to share a TV while playing on their phones with friends in another state.
Online play opens more opportunities for more players to connect without the hassle of geography or existing relationships.

**Requirement #4:** Games should behave like a virtual room.

Connectivity to the cloud or local network can be spotty, or players can take exceptionally long time per turn; thus, players should be able to jump in/out easily.

**Requirement #5:** Players should receive notifications when it is their turn to take an action.

Some players are more social rather than competitive, so mistakes are common and easy enough to do in person with a physical board.

**Requirement #6:** Undo/rewind are important for social-driven players

Since some games have minimum player requirements, it may be interesting to include a bot to learn the game mechanics.
Furthermore, games are best when balanced fairly well, and there should not be any exceptionally obvious advantages of initial choices.

**Requirement #7:** Games should support A.I. to play and seek balance.

At this point, the user requirements are leaning us towards some technical decisions.
For instance, we need a client/server model due to the cloud, and we skip any kind of decentralized architecture because of privacy.

**Requirement #8:** We need privacy between players such that game integrity is maintained.

The client/server model requires a protocol, and the state complexity of some games can be staggering.
The mental model however can become much simpler if we see the controllers and TVs as thin/dumb clients similar to a virtual PC or game streaming service.
When you have a thin/dumb client, the server is responsible for everything.
Here, the server picks up the role of dungeon master and then asks players questions directly.

**Requirement #9:** Streams vastly reduce the client complexity.

Unfortunately, the modern cloud doesn't play well with streams for a variety of reasons.
See [woe of websocket](https://www.adama-platform.com/2021/12/22/woe.html) for more details.
Specifically, the cloud works really well with request/response and databases.
A process within the cloud may terminate for a variety of reasons: deployment, kernel upgrade, machine migration, capacity management, host failure, etc.

**Requirement #10:** The stream must be reliable over failures.  

Host failures can be accounted for in a variety of ways (VMs that float around), but the developer situation is important too.
As a developer play tests a game and pushes the boundary on all the rules, it is desirable to be able to upgrade/hot-reload the code.
This ability to hot-reload requires the need to rewind state to avoid the quadratic state build-up.

**Requirement #11:** The stream must be reliable over code changes.

Some games adopt house rules, and the server side should be fairly easy to mod or change in a social setting.
The client side should be a mostly dumb projection of the client. 

**Requirement #12:** The entire infrastructure should be represented by a single file.

---

That's a lot, and Adama addresses all these challenges.
