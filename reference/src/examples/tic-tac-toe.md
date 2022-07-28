# Tic Tac Toe

## Back-end

```adama
@static {
  // As this is going to be a live home-page sample, let anyone create
  create { return true; }
  invent { return true; }

  // As this will spawn on demand, let's clean up when the viewer goes away
  delete_on_close = true;
}

// What is the state of a square
enum SquareState { Open, X, O }

// who are the two players
public principal playerX;
public principal playerO;

// who is the current player
public principal current;

// how many wins per player
public int wins_X;
public int wins_O;

// how many stalemates
public int stalemates;

// personalized data for the connected player:
// show the player their role, a signal if it is their turn, and their wins
bubble your_role = playerX == @who ? "X" : (playerO == @who ? "O" : "Observer");
bubble your_turn = current == @who;
bubble your_wins = playerX == @who ? wins_X : (playerO == @who ? wins_O : 0);

// a record of the data in the square
record Square {
  public int id;
  public int x;
  public int y;
  public SquareState state;
}

// the collection of all squares
table<Square> _squares;

// show the board to all players
public formula board = iterate _squares;

// for visualization, we break the squares into rows
public formula row1 = iterate _squares where y == 0;
public formula row2 = iterate _squares where y == 1;
public formula row3 = iterate _squares where y == 2;

// when the document is created, initialize the squares and zero out the totals
@construct {
  for (int y = 0; y < 3; y++) {
    for (int x = 0; x < 3; x++) {
      _squares <- { x:x, y:y, state: SquareState::Open };
    }
  }
  wins_X = 0;
  wins_O = 0;
  stalemates = 0;
}

// when a player connects, assign them to either the X or O role. If there are more than two players, then they can observe.
@connected {
  if (playerX == @no_one) {
    playerX = @who;
    if (playerO != @no_one) {
      transition #initiate;
    }
  } else if (playerO == @no_one) {
    playerO = @who;
    if (playerX != @no_one) {
      transition #initiate;
    }
  }
  return true;
}

// open a channel for players to select a move
message Play { int id; }
channel<Play> play;

// the game is afoot
#initiate {
  current = playerX;
  transition #turn;
}

// test if the placed square produced a winning combination
procedure test_placed_for_victory(SquareState placed) -> bool {
  for (int k = 0; k < 3; k++) {
    // vertical lines
    if ( (iterate _squares where x == k && state == placed).size() == 3) {
      return true;
    }
    // horizontal lines
    if ( (iterate _squares where y == k && state == placed).size() == 3) {
      return true;
    }
  }
  // diagonals
  if ( (iterate _squares where y == x && state == placed).size() == 3 || (iterate _squares where y == 2 - x && state == placed).size() == 3 ) {
    return true;
  }
  return false;
}

#turn {
  // find the open spaces
  list<Square> open = iterate _squares where state == SquareState::Open;
  if (open.size() == 0) {
    stalemates++;
    transition #end;
    return;
  }
  // ask the current play to choose an open space
  if (play.decide(current, @convert<Play>(open)).await() as pick) {
    // assign the open space to the player
    let placed = playerX == current ? SquareState::X : SquareState::O;;
    (iterate _squares where id == pick.id).state = placed;
    if (test_placed_for_victory(placed)) {
      if (playerX == current) {
        wins_X++;
      } else {
        wins_O++;
      }
      transition #end;
    } else {
      transition #turn;
    }
    current = playerX == current ? playerO : playerX;
  }
}

#end {
  (iterate _squares).state = SquareState::Open;
  transition #turn;
}
```

## Front-end using RxHTML

```html
<forest>
  <template name="cell">
    <div rx:switch="state">
      <div rx:case="0">
        <decide channel="play">
          <button>Play here</button>
        </decide>
      </div>
      <div rx:case="1">X</div>
      <div rx:case="2">O</div>
    </div>
  </template>
  <template name="game">
    <table>
      <tr>
        <td>Role</td><td><lookup path="your_role" /></td>
        <td>Wins</td><td><lookup path="your_wins" /></td>
      </tr>
    </table>
    <div>
      <test path="your_turn">
        <div>
          It is your turn!
        </div>
      </test>
    </div>
    <div class="[your_turn]text-indigo-600[#your_turn]text-gray-900[/your_turn]">
      CHANGE
    </div>
    <table border="1">
      <tr rx:iterate="row1">
        <td>
          <use name="cell" />
        </td>
      </tr>
      <tr rx:iterate="row2">
        <td>
          <use name="cell" />
        </td>
      </tr>
      <tr rx:iterate="row3">
        <td>
          <use name="cell" />
        </td>
      </tr>
    </table>
  </template>
  <page uri="/#game">
    <connection name="player1" identity="eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiJ1c2VyMDAxIiwiaXNzIjoiWUlTUjNZTUpSSzNHMlo2MkFWWVdCWUNITjI5WFoyIn0.oKZOXHJUFPyxMT7j6X4WQRLy4VVeGGOvZgqMS2hsU6W1lALW-teOdoHAj2t5K3oHDBj6zH_3NFt6fR6fthfyzA" space="tic-tac-toe" key="demo" random-key-suffix>
      <use name="game" />
    </connection>
    <connection name="player2" identity="eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiJ1c2VyMDAyIiwiaXNzIjoiWUlTUjNZTUpSSzNHMlo2MkFWWVdCWUNITjI5WFoyIn0.uS2LyhmDh1gg35Zpa1yd-JKxxu4EjzggQlL9tc2zFxZYPD0SZykgtjvL0PeKH0X67ot84Xb6Hk9mmMpRqDyRMA" space="tic-tac-toe" key="demo" random-key-suffix>
      <use name="game" />
    </connection>
  </page>
</forest>
```