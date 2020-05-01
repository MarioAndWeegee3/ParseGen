package marioandweegee3.parsegen.lexer

import marioandweegee3.parsegen.ast.Expression
import marioandweegee3.parsegen.parser.Parser

typealias ParseRule = Parser.(Token, Expression?) -> Expression

data class Token internal constructor(
		val type:String,
		val text:String,
		val line:Long
) {
	override fun toString():String {
		return "[$type] $text"
	}
}