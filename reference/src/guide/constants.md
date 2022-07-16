# Constants

You want numbers? We got numbers. You want strings? We got strings. You want complex numbers, we got complex numbers! Constants are a fast way to place data into a document. For instance, the following code outlines some basic constants:

```adama
#we_got_constants {
  int x = 123;
  int y = 0x04;
  double z = 3.14;
  bool b = true;
  client c = @no_one;
  complex cx = 1 + @i;
}
```

## Details

There are a variety of ways to conjure up constants. The following table illustrates examples:

| type | syntax | examples |
| --- | --- | --- |
| bool | (true, false) | false, true |
| int | [0-9]+ | 42, 123, 0 |
| int | 0x[0-9a-fA-F]+ | 0xff, 0xdeadbeef |
| double | [0-9]*.?[0-9]*(\[eE\](0-9)+)? | 3.14, 10e19, 2.72e10 |
| string | "(^"|escape)*" | "", "hello world", "\" |
| label | #[a-z]+ | #foo, #start |
| client | @no_one | @no_one |
| maybe&lt;?&gt; | @maybe&lt;Type&gt; | @maybe&lt;int&gt; |
| maybe(?) | @maybe(Expr) | @maybe(123) |
| complex | @i | 1 + @i |


## who is executing
The ```@who``` constant refers to the client that is executing the current code.

## String escaping

A character following a backslash (\\) is an escape sequence within a string, and it is an escape for the parser to inject special characters. For instance, if you are parsing a string initiated by double quotes, then how does one get a double quote into the string? Well, that's what the escape is for. Adama's strings support the following escape sequences:

| escape code | behavior |
| --- | --- |
| \t | a tab character |
| \b | a backspace character |
| \n | a newline |
| \r | a carriage return |
| \f | a form feed |
| \" | a double quote (") |
| \\\\ | a backslash character (\\) |
| \uABCD | a unicode character formed by the four adjoined hex characters after the \u |
