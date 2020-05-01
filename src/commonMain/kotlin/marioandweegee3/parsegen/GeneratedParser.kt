package marioandweegee3.parsegen

import marioandweegee3.parsegen.ast.Expression
import marioandweegee3.parsegen.lexer.Lexer
import marioandweegee3.parsegen.lexer.TokenRule
import marioandweegee3.parsegen.parser.Parser

class GeneratedParser internal constructor(
		private val rules:Map<String, TokenRule>,
		private val skips:Set<Char>,
		private val statementEnd:String? = null
) {
	fun parse(source:String):List<Expression> {
		val lexer = Lexer(source, rules, skips)

		val tokens = lexer.tokenize()

		val parser = Parser(tokens, rules, statementEnd)

		return parser.parse()
	}
}