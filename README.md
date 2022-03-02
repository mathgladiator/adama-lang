<img align="right" src="docs/static/img/adama-height-196.jpg">

# Adama Platform (Mono Repo)

The mission of Adama is to simplify building online board games. With a bit of spit, polish, and
some [Yak shaving](https://en.wiktionary.org/wiki/yak_shaving), it ended up as a custom database and programming
language. So, let's talk about what this thing is, and this animated GIF is a good summary:

<img src="docs/static/img/20200804-adama-introduction-animated.gif">

Neat, right? Adama, the language, is a way of laying out state and then defining how to manipulate that state within a
closed container. Manipulation of state is done by people via messages, and the document is computed reactively similar
to Excel.

Now, the entire purpose is to play an online board game, but this extends well beyond board games. However, manifesting
those values will take time to communicate. For now, the key is that players connect to a shared document, and the
document enforces both rules and privacy via the Adama language.

For more information, please refer to the [website: https://www.adama-platform.com/](https://www.adama-platform.com/)

# What is the status
If you are interested in contributing, then let me know!
This README is scant on details since they are evolving fast.
As of 3/3/2022, the software is in an "early release" state which can be used with limited capacity right now.
Check out [early access launch preview](https://www.adama-platform.com/2022/03/02/early-access-launch-and-confession.html)

# License

Board games are the foothold killer use-case for Adama, but the ultimate goal of Adama is to redefine how applications
get built with user-centric privacy as a first-class consideration. This ambitious goal of changing the entire landscape
requires an open agenda, so the language is open source under the [MIT](LICENSE).

