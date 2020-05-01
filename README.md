#ParseGen

ParseGen is a DSL library for building parsers in Kotlin. To build a parser, use the buildParser function.

## Tokens

Tokens are matched using regular expressions - Regex - from the source string.

Each token has a type, which is a unique String that identifies the structure of the token's string. It also stores the 
text representing the token and the line in the original source it was found.

In buildParser, you build a type of token for the parser to create. You set the Regex used to build the token, the 
precedence the token has as an operator, and parse rules that govern the parser's behavior if it encounters that type of 
token.

## Expressions

Because the parser is internally represented by a [Pratt parser](https://en.wikipedia.org/wiki/Pratt_parser), it is 
relatively easy to think of your language as a series of Expressions. 

You define your Expression classes and build them using your parsing rules. The parser will run through the source and 
build expressions based on the parse rules you have set.

## Parse Rules

Parse rules are functions that operate on the Parser and take two parameters - a Token matching the associated type, and 
an optional Expression. The Expression is only defined for infix rules. The Expression is the previously parsed 
expression, or null for prefix rules. Parse rules are defined in the Token type's block in buildParser.

Parse rules are optional for any given token type. A type can a prefix, infix, both, or neither.

Prefix rules are called at the beginning of an expression, the parser throws a ParserError if the token it finds doesn't 
have a prefix rule.

Infix rules are called after a matching prefix rule for a previous token. These are the last rules called before the end 
of a statement.

## Precedence

Precedence is an Int value used to bind expressions to infix operators. Lower precedence -> lower priority. Other 
explanations of Pratt parsers call this "binding power", but it is the same concept.

## Statements

Because ParseGen uses a Pratt parser, all statements are a single expression. Parsers can be given a token type to match 
for at the end of the statement using the end function in buildParser. If no ending token type is defined, the parser 
will not require that a statement have any terminator. It is recommended that the type used as the statement terminator 
has higher precedence than any other token, forcing the parser to never try to match it as an infix operator. It is also 
recommended that the statement terminator have no defined prefix or infix rules; they typically aren't meant to signify 
anything. However, you can use an infix rule to allow for a token at the end of a statement, without forcing it:
````Kotlin
token("semicolon", ";".toRegex(), 0) {
    infix{_, exp ->
        exp!! // Return the expression already parsed
    }
}
````

You can put this code into the buildParser block. This allows for expressions that can end in a semicolon, or not.

## Full Syntax for buildParser

_Note: ParseRule = Parser.(Token, Expression?) -> Expression_

buildParser ->
* skip(vararg cs:Char) -> Makes the parser skip these characters during lexing.

* end(type:String) -> Sets the token type corresponding to the string to be the statement terminator

* token(type:String, regex:Regex, precedence:Int, tb:TokenBuilder.() -> Unit) ->
    * Type is the name of the type. Regex is the regex that tokens of this type must match. Precedence is used for parsing.
    * prefix (rule:ParseRule)
    * infix (rule:ParseRule)
    * _Note: for prefix, the Expression? parameter is always null, while with infix, it is never null_

## Parser API

There are functions available to Parse Rules that aid in making expressions.

* peek():Token? -> Returns the currently observed token, without changing the state of the parser.
* next():Token? -> Returns the next token - indexOf(peek())+1 - also without changing any state.
* previous():Token? -> Returns the previous token without changing the parser's state.
* advance():Token? -> Returns the currently observed token and moves the parser to the next token.
* consume(message:String, condition:Token.() -> Boolean):Token -> Returns the next token if it is not null and it matches the passed condition. Throws a ParserError if the currently observed token doesn't match.
* consume(message:String, type:String):Token -> Checks the condition that the next token is of the passed type.
* expression(precedence:Int):Expression -> Parses an expression with the given precedence. Left-associative by default. Use custom logic in an infix block to handle Right-associativity. 