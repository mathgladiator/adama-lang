# Hearts

Many of the bugs have been fixed, this is from an old version.
```adama

@static {
  // anyone can create
  create { return true; }
}

// we define the suit of a card
enum Suit {
  Clubs:1,
  Hearts:2,
  Spades:3,
  Diamonds:4,
}

// the rank of a card
enum Rank {
  Two:2,
  Three:3,
  Four:4,
  Five:5,
  Six:6,
  Seven:7,
  Eight:8,
  Nine:9,
  Ten:10,
  Jack:11,
  Queen:12,
  King:13,
  Ace:14,
}

// where can a card be
enum Place {
  Deck:1,
  Hand:2,
  InPlay:3,
  Taken:4
}

// model the card and its location and ownership
record Card {
  public int id;
  public Suit suit;
  public Rank rank;
  private client owner;
  private int ordering;
  private Place place;
  private auto points = suit == Suit::Hearts ? 1 : (suit == Suit::Spades && rank==Rank::Queen ? 13 : 0);

  // define a policy as to who can see the card
  policy p {
    // if it is in hand on in the pot, then only the owner of the card can see it
    // the rules of hearts have cards face down
    if (place == Place::Hand || place == Place::Taken) {
      return @who == owner;
    }
    // if it is in the pot or in play, then anyone can see it
    if (place==Place::InPlay) {
      return true;
    }
    // otherwise, it is in the deck and thus not visible
    return false;
  }

  require p;
}

// the entire deck of cards
table<Card> deck;

// show the player hand (and let the privacy policy filter out by person)
public auto hand = iterate deck where place == Place::Hand;

// show all cards in the pot (this would be a different way of defining hand)
bubble<who> my_take = iterate deck where place == Place::Taken && owner == who;

// no real constructor
message Empty {}

client owner;

record Player {
  public int id;
  public client link;
  private bool playing;
  public int points;
  viewer_is<link> bool leader;
  viewer_is<link> int play_order;
  // sort out why this doesn't work
  // viewer_is<link> auto hand = iterate deck where owner == link;
}

table<Player> players;

@connected(cv) {
  // the first connection assumes a leadership position as the owner of the table/game
  if (owner == @no_one) {
    owner = cv;
    players <- {
      link:cv,
      playing:true,
      leader:true,
      play_order:0,
      points:0
    };
    transition #lobby;
  }

  // the owner is always allowed
  if (owner == cv) {
    return true;
  }

  // add the player if they are not already in the game
  if ( (iterate players where link==cv).size() == 0) {
    players <- {
      link:cv,
      playing:false,
      leader:false,
      play_order: players.size(),
      points:0
    };
  }

  return true;
}

@disconnected(cv) {
  // remove the player if they are not playing
  (iterate players where link==cv && !playing).delete();
}

// how many people are connected
public auto players_connected = (iterate players where playing).size();

// how many observers
public auto observers_connected = (iterate players where !playing).size();

// everyone in the game
public auto people = iterate players order by play_order;

// the players by their ordering
public auto players_ordered = iterate players where playing order by play_order;

// TODO: the client logic for who is what is going to be... interesting

// whether or not the game is ready to begin
public auto ready = players_connected == 4;

// are we actually playing the game?
public bool playing = false;

// we are waiting for an owner to arrive which will transition us into the lobby
// the lobby allows the leader to send messages of the following form

enum LeaderAction {
  PromoteObserver:1,
  DemotePlayer:2,
  ShufflePlayers:3,

  // BumpOrderUp:4, BumpOrderDown:5
  Begin:10
}

// the purpose of this action is to convert observers into players, and to arrange the players.


// the action is wrapped in a message
message LeaderActionMessage {
 LeaderAction action;
 int id;
}


// and the message is used as a channel which yields futures
channel<LeaderActionMessage> leader;

// the lobby is where leader will marshall the people
#lobby {
  // ask the leader to do something
  LeaderActionMessage decision = leader.fetch(owner).await();
  if (decision.action == LeaderAction::PromoteObserver && !ready) {
     // leader promoted an observer to
    (iterate players where id==decision.id).playing = true;
  } else if (decision.action == LeaderAction::DemotePlayer) {
    // leader demotes a player to an observer
    (iterate players where id==decision.id).playing = false;
  } else if (decision.action == LeaderAction::ShufflePlayers) {
    (iterate players where playing).play_order = Random.genInt();
  } else if (decision.action == LeaderAction::Begin) {
    // leader has selected people, and will now begin the game
    playing = true;
    if ( (iterate players where playing).size() == 4) {
      transition #setup;
    }
  }
  transition #lobby;
}

// how setup the game state
#setup {
  // build the deck
  foreach (s in Suit::*) {
    foreach (r in Rank::*) {
      deck <- {rank:r, suit:s, place:Place::Deck};
    }
  }

  // normalize observers to no play order
  (iterate players where !playing).play_order = 100;

  // normalize the players from 0 to 3
  int normativeOrder = 0;
  (iterate players where playing order by play_order asc).play_order = normativeOrder++;

  // shuffle and distribute the cards
  transition #shuffle_and_distribute;
}

enum PassingMode { Across:0, ToLeft:1, ToRight:2, None:3 }

public PassingMode passing_mode;

#shuffle_and_distribute {
  // it may be useful to allow methods on a record, fuck
  (iterate deck).ordering = Random.genInt();
  (iterate deck).owner = @no_one;
  (iterate deck).place = Place::Hand;

  // distribute cards to players
  Player[] op = (iterate players where playing order by play_order).toArray();
  for (int k = 0; k < 4; k++) {
    if (op[k] as player) {
      (iterate deck where owner == @no_one order by ordering limit 13).owner = player.link;
    }
  }
  transition #pass;
}

message CardDecision {
  int id;
}

channel<CardDecision[]> pass_channel;

// this is wanky, need arrays at a top level that are finite to help...
client player1;
client player2;
client player3;
client player4;

client current;

#pass {
  if (passing_mode == PassingMode::None) {
    transition #start_play;
  }

  // this is wanky as fuck, and I don't like it. We have this fundamental problem of what if there are not enough players, then how does this fail...
  // we should consider a @fatal keyword to signal that a game is just fucked

  Player[] op = (iterate players where playing order by play_order).toArray();
  if (op[0] as player) {
    player1 = player.link;
  }
  if (op[1] as player) {
    player2 = player.link;
  }
  if (op[2] as player) {
    player3 = player.link;
  }
  if (op[3] as player) {
    player4 = player.link;
  }
  // what does an await on no_one mean, it means the whole thing is fucked

  // we really need a future array since this has some awkward stuff
  future<maybe<CardDecision[]>> pass1 = pass_channel.choose(player1, @convert<CardDecision>(iterate deck where owner==player1), 3);
  future<maybe<CardDecision[]>> pass2 = pass_channel.choose(player2, @convert<CardDecision>(iterate deck where owner==player2), 3);
  future<maybe<CardDecision[]>> pass3 = pass_channel.choose(player3, @convert<CardDecision>(iterate deck where owner==player3), 3);
  future<maybe<CardDecision[]>> pass4 = pass_channel.choose(player4, @convert<CardDecision>(iterate deck where owner==player4), 3);

  // the reason we do the futures above and then await them below like this is so all players can pass at the same time.
  // the problem at hand is that the await will consume, so non-awaited futures will cause the client to sit dumbly... this can be fixed easily I think
  // by having the make_future<> check the stream and pre-drain the queue and allow the await to short-circuit with the provide option

  if (pass1.await() as decision1) {
  if (pass2.await() as decision2) {
  if (pass3.await() as decision3) {
  if (pass4.await() as decision4) {

  if (passing_mode == PassingMode::ToRight) {
    foreach (dec in decision1) {
      (iterate deck where id == dec.id).owner = player2;
    }
    foreach (dec in decision2) {
      (iterate deck where id == dec.id).owner = player3;
    }
    foreach (dec in decision3) {
      (iterate deck where id == dec.id).owner = player4;
    }
    foreach (dec in decision4) {
      (iterate deck where id == dec.id).owner = player1;
    }
  } else if (passing_mode == PassingMode::ToLeft) {
    foreach (dec in decision1) {
      (iterate deck where id == dec.id).owner = player4;
    }
    foreach (dec in decision2) {
      (iterate deck where id == dec.id).owner = player1;
    }
    foreach (dec in decision3) {
      (iterate deck where id == dec.id).owner = player2;
    }
    foreach (dec in decision4) {
      (iterate deck where id == dec.id).owner = player3;
    }
  } else if (passing_mode == PassingMode::Across) {
    foreach (dec in decision1) {
      (iterate deck where id == dec.id).owner = player3;
    }
    foreach (dec in decision2) {
      (iterate deck where id == dec.id).owner = player4;
    }
    foreach (dec in decision3) {
      (iterate deck where id == dec.id).owner = player1;
    }
    foreach (dec in decision4) {
      (iterate deck where id == dec.id).owner = player2;
    }
  }

  }}}}
  transition #start_play;
}

public int played = 0;

#start_play {
  // no cards hae been played
  played = 0;

  // assign a player to current
  current = player1;
  if ( (iterate deck where rank == Rank::Two && suit == Suit::Clubs)[0] as two_clubs) {
    current = two_clubs.owner;
  } // otherwise, @fatal

  transition #play;
}

channel<CardDecision[]> single_play;

public Suit suit_in_play;
public bool points_played = false;
public auto in_play = iterate deck where place == Place::InPlay order by rank desc;
// how to attribute this to a person

public client last_winner;

#play {
  list<Card> choices = iterate deck where
    owner==current &&
    place == Place::Hand &&
    (
       played == 0 && (points_played || points == 0) ||
       played > 0 && suit_in_play == suit
    );
  if (choices.size() == 0) {
    choices = iterate deck where owner==current && place == Place::Hand;
  }
  future<maybe<CardDecision[]>> playX = single_play.choose(current, @convert<CardDecision>(choices), 1);
  if (playX.await() as thePlay) {
  // TODO: don't think hearts can be played, there are some rules here
  foreach (dec in thePlay) {

    (iterate deck where id == dec.id).place = Place::InPlay;
    if ( (iterate deck where id == dec.id)[0] as cardPlayed) {
// TODO
//   cardPlayed.place = Place::InPlay;
// this doesn't work

      // points are open
      if (cardPlayed.points > 0) {
        points_played = true;
      }
      if (played == 0) {
        suit_in_play = cardPlayed.suit;
      }
    }
  }
  }

  // if the number of cards played is less than 4, then next player; otherwise, decide winner of pot and award points

  // TODO: need finite arrays and cyclic integers
  if (current == player1) {
    current = player2;
  } else if (current == player2) {
    current = player3;
  } else if (current == player3) {
    current = player4;
  } else if (current == player4) {
    current = player1;
  }

  if (played == 3) {
    // TODO: figure this out (why can't I limit and then)
    if ( (iterate deck where place == Place::InPlay && suit == suit_in_play order by rank desc)[0] as winner) {
      (iterate deck where place == Place::InPlay).owner = winner.owner;
      last_winner = winner.owner;
    }
    (iterate deck where place == Place::InPlay).place = Place::Taken;
    played = 0;
    current = last_winner;

    if( (iterate deck where owner == current && place == Place::Hand).size() == 0) {
      transition #score;
    }
  } else {
    played++;
  }
  transition #play;
}
public int points_awarded = 0;

#score {
  // award points
  foreach(p in iterate players where playing) {
    int local_points = 0;
    foreach(c in iterate deck where owner == p.link && place == Place::Taken) {
      local_points += c.points;
    }
    if (local_points == 26) {
      foreach(p2 in iterate players where playing && link != p.link) {
        p2.points += 26;
        points_awarded += 26;
      }
    } else {
      p.points += local_points;
      points_awarded += local_points;
    }
  }

  // this may not respect rules, but... hey
  if (passing_mode == PassingMode::Across) {
    passing_mode = PassingMode::ToRight;
  } else if (passing_mode == PassingMode::ToRight) {
    passing_mode = PassingMode::ToLeft;
  } else if (passing_mode == PassingMode::ToLeft) {
    passing_mode = PassingMode::None;
  } else if (passing_mode == PassingMode::None) {
    passing_mode = PassingMode::Across;
  }

  transition #shuffle_and_distribute;
}

```