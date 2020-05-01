package marioandweegee3.parsegen.parser

import marioandweegee3.parsegen.ast.Expression
import marioandweegee3.parsegen.err.ParserError
import marioandweegee3.parsegen.lexer.Token
import marioandweegee3.parsegen.lexer.TokenRule

class Parser internal constructor(
		private val tokens:List<Token>,
		private val rules:Map<String, TokenRule>,
		private val statementEnd:String? = null
){
	private var current = 0

	private fun peekInternal(ahead:Int):Token? = when{
			current + ahead >= tokens.size -> null
			else -> tokens[current + ahead]
	}

	fun peek() = peekInternal(0)
	fun next() = peekInternal(1)
	fun previous() = peekInternal(-1)

	fun advance():Token? = when(val t = peek()){
		null -> null
		else -> {
			current++
			t
		}
	}

	fun consume(message:String, condition:Token.() -> Boolean):Token {
		return when{
			peek()?.condition()?:false -> advance()!!
			else -> throw ParserError(message, peek())
		}
	}

	fun consume(message:String, type:String):Token {
		return consume(message){this.type == type}
	}

	fun expression(precedence:Int):Expression {
		val leftT = advance()?:throw ParserError("Expected a token", null)
		val prefix = rules[leftT.type]?.prefix?:throw ParserError("Expected an expression", leftT)

		var left = prefix(leftT, null)

		while(precedence <= rules.getValue(peek()?.type?:return left).precedence) {
			val op = peek()!!
			val infix = rules[op.type]?.infix?:return left
			advance()
			left = infix(op, left)
		}

		return left
	}

	internal fun parse():List<Expression> {
		val expressions = ArrayList<Expression>()

		while(peek() != null) {
			expressions += expression(0)
			statementEnd?.let {
				consume("Expected $it at statement end", it)
			}
		}

		return expressions
	}
}