package calculator

import marioandweegee3.parsegen.ast.Expression
import marioandweegee3.parsegen.err.ParserError
import marioandweegee3.parsegen.lexer.ParseRule
import marioandweegee3.parsegen.lexer.Token
import marioandweegee3.parsegen.buildParser

data class Binary(val left:Expression, val op:Token, val right:Expression):Expression()
data class Unary(val exp:Expression, val op:Token):Expression()
data class Number(val value:Double):Expression()
object None:Expression()

object TokenTypes {
	const val Number = "number"
	const val Plus = "plus"
	const val Minus = "minus"
	const val Times = "times"
	const val Divide = "divide"
	const val Semicolon = "semicolon"
	const val LParen = "left paren"
	const val RParen = "right paren"

	val operators = setOf(Plus, Minus, Times, Divide)
}


fun main(){
	fun binary(precedence:Int):ParseRule = {op, left ->
		left!!
		val right = expression(precedence+1)
		Binary(left, op, right)
	}
	val unary:ParseRule = {op, _ ->
		val exp = expression(1)
		Unary(exp, op)
	}
	val parser = buildParser {
		skip(' ', '\r', '\t', '\n')
		end(TokenTypes.Semicolon)
		token(TokenTypes.Semicolon, ";".toRegex(), 3) {}
		token(TokenTypes.Number, "0|([1-9][0-9]+(\\.[0-9])*)".toRegex(), 0) {
			prefix {n, _ ->
				Number(n.text.toDouble())
			}
		}
		token(TokenTypes.Plus, "\\+".toRegex(), 1) {
			infix(binary(1))
		}
		token(TokenTypes.Minus, "-".toRegex(), 1) {
			prefix(unary)
			infix(binary(1))
		}
		token(TokenTypes.Divide, "/".toRegex(), 2) {
			infix(binary(2))
		}
		token(TokenTypes.Times, "\\*".toRegex(), 2) {
			infix(binary(2))
		}
		token(TokenTypes.LParen, "\\(".toRegex(), 0) {
			prefix { _, _ ->
				val exp = expression(0)
				consume("Expected ')' after group", TokenTypes.RParen)
				exp
			}
		}
		token(TokenTypes.RParen, "\\)".toRegex(), 0) {}
	}

	try{
		val expressions = parser.parse("""
			2 + 2;
			(5 + 4) / 2;
		""".trimIndent())

		println(expressions.joinToString("\n"))
	} catch(p:ParserError) {
		println("[Error] ${p.message} ${
		when(p.at){
			null -> ""
			else -> "at ${p.at}"
		}
		}")
	}
}