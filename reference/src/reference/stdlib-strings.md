# Strings

The core string type is extended with a variety of methods which can be invoked on string objects. For example,

```adama
  public string x;
  formula x_x = (x + x).reverse();
```

## Methods

| Method | Description | Result type |
| --- | --- | --- |
| length() | Returns the length of a string | int |
| split(string word) | Splits the string into a list of parts seperated by the given word | list&lt;string&gt; |
| split(maybe&lt;string&gt; word) | Splits the string into a list of parts seperated by the given word if the word is available | maybe&lt;list&lt;string&gt;&gt; |
| contains(string word) | Tests if the string contains the given word | bool |
| contains(maybe&lt;string&gt; word) | Tests if the string contains the given word if the word is available | maybe&lt;bool&gt; |
| indexOf(string word) | Returns the position of the given word | int |
| indexOf(maybe&lt;string&gt; word) | Returns the position of the given word if the word is available | maybe&lt;int&gt; |
| indexOf(string word, int offset) | Returns the position of the given word after the given offset| int |
| indexOf(maybe&lt;string&gt; word, int offset) | Returns the position of the given word if the word is available after the given offset | maybe&lt;int&gt; |
| trim() | Returns a new version of the string with whitespace removed from both the head and tail | string |
| trimLeft() | Returns a new version of the string with whitespace removed from the head/start/left-side of the string | string |
| trimRight() | Returns a new version of the string with whitespace removed from the tail/end/right-side of the string | string |
| upper() | Returns a new version of the string with all upper case characters | string |
| lower() | Returns a new version of the string with all lower case characters | string |
| mid(int start, int length) | Returns the part of the string starting at the indicated start position with the length provided. | maybe&lt;string&gt; |
| left(int start) | Returns the part of the string starting at the indicated start position until the end of the string | maybe&lt;string&gt; |
| right(int length) | Returns a string with the indicated length coming from the end of the string | maybe&lt;string&gt; |
| substr(int start, int end) | Returns a string that starts at the given position and ends with the given position | maybe&lt;string&gt; |
| startsWith(string prefix) | Returns whether or not string is prefixed by the given string | bool |
| endsWith(string prefix) | Returns whether or not string is suffixed by the given string | bool |
| multiply(int n) | Returns the concatentation of the string n times | string |
| reverse() | Returns a copy of the string with the characters reversed | string |

## Functions
| Function | Description | Result type |
| --- | --- | --- |
| String.charOf(int ch) | Returns a string with the give integer character converted into a string | string | 