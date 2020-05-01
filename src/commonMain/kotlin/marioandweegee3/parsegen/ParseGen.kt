package marioandweegee3.parsegen

import marioandweegee3.parsegen.builder.ParserBuilder

fun buildParser(p:ParserBuilder.() -> Unit):GeneratedParser {
	val builder = ParserBuilder()

	builder.p()

	return builder.build()
}