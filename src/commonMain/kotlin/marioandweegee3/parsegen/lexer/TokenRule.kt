package marioandweegee3.parsegen.lexer

data class TokenRule internal constructor(
		val prefix:ParseRule?,
		val infix:ParseRule?,
		val precedence:Int,
		val regex:Regex
)