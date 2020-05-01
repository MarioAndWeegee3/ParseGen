package marioandweegee3.parsegen.builder

import marioandweegee3.parsegen.lexer.ParseRule
import marioandweegee3.parsegen.lexer.TokenRule

class TokenBuilder internal constructor(private val type:String, private val regex:Regex, private val precedence:Int) {
	private var prefix:ParseRule? = null
	private var infix:ParseRule? = null

	fun prefix(rule:ParseRule){
		if(prefix != null)
			println("[Warning] Token $type's prefix rule should only be assigned once")
		prefix = rule
	}

	fun infix(rule:ParseRule){
		if(infix != null)
			println("[Warning] Token $type's infix rule should only be assigned once")
		infix = rule
	}

	internal fun build():TokenRule {
		return TokenRule(prefix, infix, precedence, regex)
	}
}