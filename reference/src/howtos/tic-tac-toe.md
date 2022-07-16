# How to create a Tic Tac Toe Game using Adama Platform 

*written by [David Asaolu](https://www.linkedin.com/in/david-asaolu234/)*

Building an efficient board game can be tedious if you're not using the programming language and resources best suited for creating such gaming applications. 
In this article, I'll guide you through building a Tic Tac Toe game with Adama, a programming language that allows you to create board games easily.    

Adama is a reactive programming language that utilizes an event-driven architecture that enables us to build scalable and efficient applications. Adama started as a tool that provides a better way of representing states in an application, then gradually grew into a fully-fledged programming language. Adama uses a serverless infrastructure whereby a single file can contain an infinite space of documents acting like tiny computers with storage and networking.    

Before we go further, let's learn why you should choose Adama when building your applications.

## Why choose Adama?   

In this section, you'll learn about some of the features provided by Adama that enable us to build efficient real-time applications.

**Fast compilation and deployment**     
Programming in Adama is fast, and compilation and deployment happen immediately after initiating the actions. Adama is a language designed to achieve more functionalities with less effort, cost, and time. 
In Adama, the validator runs in a single digit millisecond for a moderately large code base.

**Backward compatibility**   
Adama is an innovative language that can run early versions of the program in newer environments without errors. Programs written in the older versions of Adama can run efficiently without issues.

**Excellent tool for creating efficient applications**      
Being a reactive programming language, Adama handles events asynchronously; this enables your program to process real-time updates efficiently and accommodate many users at a time. Adama started as a tool for representing states conveniently before becoming a programming language. Adama aims to make application development easy and even provide more capabilities that will enable you to build affordable and reliable applications.

## How to start building with Adama   

Here, I will guide you through setting up Adama and how you can start creating efficient applications with Adama. Before building with Adama, you need to install Java on your computer. Head over to Oracle's website and install [Java 17](https://www.oracle.com/java/technologies/downloads/#java17).        
Once you have completed the installation process, run the code below in your terminal to confirm if the installation was successful.
```shell
java -version
```   
It should return something similar to the code below.

```shell  
openjdk version "11.0.13" 2021-10-19
OpenJDK Runtime Environment (build 11.0.13+8-Ubuntu-0ubuntu1.20.04)
OpenJDK 64-Bit Server VM (build 11.0.13+8-Ubuntu-0ubuntu1.20.04, mixed mode, sharing)
```    

Next, download the latest Adama jar file from GitHub by running this code.

```shell
wget https://github.com/mathgladiator/adama-lang/releases/download/nightly/adama.jar
```  

Create an Adama developer account by running the code below. Read through the information and supply your email address. Enter the verification code sent to your email in your terminal.  

```shell
java -jar adama.jar init
```  

Run the code below to create a space for your Adama document. Adama space is similar to buckets in AWS. It is the container for your Adama documents.   

```shell 
java -jar adama.jar space create --space <your_space_name>
```    

Congratulations! You've just created a space for your Adama document. You can now start creating the backend for the Tic Tac Toe game.  

## Building the backend for your Tic Tac Toe game   

In this section, we'll leverage the tools provided by Adama to build a Tic Tac Toe game. Tic Tac Toe is a game that consists of two users, one is X, and the other is O. The player that succeeds in placing three of its symbols horizontally, vertically, or diagonally is the winner.      

Before writing Adama's code, we need to state the document's policy. The code snippet below allows anyone to create a document.    
```adama
@static {
  // This makes it possible for everyone to create a document.
  create { return true; }
  invent { return true; }

  // As this will spawn on demand, let's clean up when the viewer goes away
  delete_on_close = true;
}  
```   
Below the document's policy, declare the states of each square box - empty or contains the player X or O. From the code snippet below, we created an enum variable type that represents all the three possible states of the application.    
```adama  
// What is the state of a square
enum SquareState { Open, X, O }
```   
Next, let's declare a public variable representing each player. In Adama, the client keyword is assigned to users and contains information related to the user; @no_one is its default value.   
```adama  
// The two players
public client playerX;
public client playerO;

// The current player
public client current;
```  
Create another set of variables containing the draws and win in the game.   
```adama  
// how many wins
public int wins_X;
public int wins_O;

// how many stalemates or player draws
public int stalemates;

```  
Assign roles to each player. In Adama, there is a data type called bubble whose values change depending on the connected user viewing the document; this allows users to have a personalized view of the document.   
```adama  
// personalized data for the connected player:
// show the player their role, a signal if it is their turn, and their wins
bubble your_role = playerX == @who ? "X" : (playerO == @who ? "O" : "Observer");
bubble your_turn = current == @who;
bubble your_wins = playerX == @who ? wins_X : (playerO == @who ? wins_O : 0);
```  
From the code snippet above, your_role assigns the current player viewing the document,  "X" and the other "O". The your_turn variable is equal to whether the user viewing the document is the current player. The your_wins variable contains the number of times the current user wins the game.     

Next, let's create a record containing every data in each square block. Record is a variable that groups variables related to an entity under a single variable.
```adama  
// a record of the data in the square
record Square {
  public int id;
  public int x;
  public int y;
  public SquareState state;
}
```  
Since we've been able to represent each box in the tic-tac-toe grid as records, create a table containing every box on the tic-tac-toe table.   
```adama  
// the collection of all square boxes
table<Square> _squares;  
```   
From the code snippet above, the `table` is the keyword for creating a table. The angle brackets accept the record type as a parameter and convert it to a table. `_squares` represent the name of the table.   

Convert the table to a list and separate the board by using its rows.
```adama  
// converts the table to a list using the iterate keyword
public formula board = iterate _squares;

// breaks the square into its rows
public formula row1 = iterate _squares where y == 0;
public formula row2 = iterate _squares where y == 1;
public formula row3 = iterate _squares where y == 2;
```   
From the code snippet above, the formula identifier enables us to write an expression on the right side of the equal-to sign. Each row is differentiated using the variable y, which represents the y-axis of the table.    

Add the code below to the document. The code snippet loops through the x and y variable from the Square record and saves the result into the `_squares` table. 
```adama
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
```  
Note that the `@construct` event will be fired once after creating the document. Initially, the number of wins for the X, O players, and draws is 0.   

Update the document by adding the code below. The code snippet assigns the current player an X or O. After giving both values to the players, the game starts. This event returns true indicating that the player is allowed in the game to either play or observe.  
```adama
@connected {
  if (playerX == @no_one) { //if no one has been assigned playerX
    playerX = @who;     //assign playerX to the current user
    if (playerO != @no_one) { //if playerO has been assigned to a user
      transition #initiate; //start the game
    }
  } else if (playerO == @no_one) { //if no one has been assigned playerO
    playerO = @who;  //assign playerO to the current user viewing the document
    if (playerX != @no_one) { //if playerX has been assigned to a user
      transition #initiate;   //start the game
    }
  }
  return true; //The user has been successfully connected
}

// the game is afoot
#initiate {
  current = playerX; //playerX is the first person to play
  transition #turn;
}
```  
Create the turn state. The turn state checks for empty spaces in the square table; if there are none, the game records a stalemate, and the `stalemates` variable is increased by one before it ends.  
```adama
#turn {
  // find the open spaces
  list<Square> open = iterate _squares where state == SquareState::Open;
  if (open.size() == 0) {
    stalemates++;
    transition #end;
    return;
  }
}

```
Create a channel to enable players to move between each square box via its id.   
```adama 
// open a channel for players to select a move
message Play { int id; }
channel<Play> play;
```  
Next, add a procedure that determines if there is a win. The code snippet below accepts the values in each square box and returns true if the vertical, horizontal, and diagonal spaces contain the same values of either X or O.  
```adama 
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
  if ( (iterate _squares where y == x && state == placed).size() == 3 ||
       (iterate _squares where y == 2 - x && state == placed).size() == 3 ) {
    return true;
  }
  return false;
}
```  
Update the turn state by copying the code below. The play channel created earlier allows the player to select an open space until one of the players wins or when there is no empty space.  
```adama
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
```  
Finally, create the end state and make all the square boxes empty and ready for another round of play.
```adama  
#end {
  (iterate _squares).state = SquareState::Open;
  transition #turn;
}
```  

## Conclusion

In this article, you've learnt about the different features Adama provides, how you can start building with Adama, and how to build the backend of a Tic Tac Toe game using Adama.

Adama is a programming language that leverages the reactive property to enable us to build efficient and scalable real-time applications at a minimal cost. Adama provides a fun way of building applications. If you are looking forward to building an efficient real-time gaming application, Adama is an excellent choice.

Thank you for reading!






