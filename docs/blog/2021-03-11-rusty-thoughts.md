---
slug: rusty-thoughts-2021
title: Thoughts, rust, the dip, strategy forward
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [adama, ui, rust]
---

Well, February was a fun month, and I thought about many things. I’d like to share some of them now.

First, I’m learning [rust](https://www.rust-lang.org/) and I love it. It’s a good language, and it triggers the special feeling that I'm making something that will last. Part of this is from the discipline of the borrow checker where I have to be very careful with how I do memory. Sure, I could try to mimic the niceness that a garbage collected language provides or just go sloppy with how C++ works, but that’s just digging a hole. With rust, I have to plan, think hard, and design the shape of the beast more upfront. As an example, I’m writing a JSON parser to flex some parser skills. I’m proud to say that it was not easy, and I like the results so far.

Second, I am also looking at how to bridge between the rust code and the browser via WebAssembly. I’m impressed with the current ecosystem, and I wish I could go 100% rust. However, that would be following a siren song as there are many gaps between what the browser offers and where the rust ecosystem is. I hope to write more about these gaps in the future, but my goal is to find a reasonable balance now such that long term investments can migrate to rust over time. Simply put, I need to move faster on higher level design details without being bogged down with lack of features. The browser is a challenging foe to usurp.

Third, I realize now this project is in the dip. This is no small project, and I’ve recently re-read The Dip from Seth Godin](https://www.amazon.com/Dip-Little-Book-Teaches-Stick/dp/1591841666). It’s a small book that reminds one of the power of quitting early or being strategic to get out of the dip. The key is recognizing your situation. There is a long road ahead for this, and I have to recognize that I’m not going to get every detail right from the beginning. I’m going to make catastrophic mistakes, but I must soldier on with a strategy.

Fourth, I’m looking to leverage what I current have built in an intermediate product. As I was opening up the repo, I realized “I forgot practically everything practical.” This is the bad part of becoming battle aloof, but this is also an opportunity to look at the project with fresh eyes. I intend to focus on some of the accoutrements that help ease people into a project because I’m finding myself in need. It’s helpful as I don’t remember where the bodies are buried, so I intend to focus on that.

Fifth, I am getting the urge to extend the language based on rust (Rust’s enums remind me of the good days when I was in love with OCaml). Now, this would be the worst way to spend my time, but it would be fun. Should I do it? Maybe. Life is short, so why should I feel bad about delaying shipping? This isn’t a business, and I probably am not going to make a business play for a few years. Problematically, this only will extend the dip, but what is success? Is success numbers in a bank account? Is success a wikipedia page? Is success a huge number of stars and followers? Is success doing talks? No, I think success comes from contentedness that we spent our time well, so how do I measure that? Is it measurable?

Sixth, I feel like if I can make progress on the README, a tutorial, and maybe get cracking on polishing up the demo then March will be time well spent. At least for the meta project, and I need to think about how far and wide I can go on shipping real products. Now, problematically, I can go meta yet again and build all sorts of stuff before building the actual game, so that’s a problem. Instead, I need to focus on solving exactly one meta problem: the deck builder. If I think about limiting meta to smart components, then perhaps I don’t get too bogged down in empire building. I also need to not get bogged down in worrying too much about rendering performance or overhead...

Anyways, if you are reading this, then thank you.


