package marioandweegee3.parsegen.builder

import marioandweegee3.parsegen.GeneratedParser
import marioandweegee3.parsegen.lexer.TokenRule
import marioandweegee3.parsegen.parser.Parser

class ParserBuilder {
	private val rules = HashMap<String, TokenRule>()

	private var skips:Set<Char>? = null

	private var statementEnd:String? = null

	fun skip(vararg cs:Char) {
		if(skips != null)
			println("[Warning] Characters to skip have already been set and will be overridden")
		skips = cs.toSet()
	}

	fun token(type:String, regex:Regex, precedence:Int, tb:TokenBuilder.() -> Unit) {
		val builder = TokenBuilder(type, regex, precedence)

		builder.tb()

		if(type in rules)
			println("[Warning] Token Type $type is being overwritten")

		rules[type] = builder.build()
	}

	fun end(type:String) {
		if(statementEnd != null)
			println("[Warning] Overriding statement end token")

		statementEnd = type
	}

	internal fun build():GeneratedParser {
		return GeneratedParser(rules, skips?:emptySet(), statementEnd)
	}
}