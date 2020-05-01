package optional_semicolon

import marioandweegee3.parsegen.ast.Expression
import marioandweegee3.parsegen.buildParser

object TokenTypes {
	const val Semicolon = "semicolon"
	const val Number = "Number"
	const val Plus = "Plus"
}

object Exp {
	data class Num(val value:Int):Expression()
	data class Plus(val left:Expression, val right:Expression):Expression()
}

fun main(){
	val p = buildParser {
		skip(' ', '\r', '\t', '\n')
		token(TokenTypes.Semicolon, ";".toRegex(), 0) {
			infix { _, exp -> exp!! }
		}
		token(TokenTypes.Number, "-?(0|[1-9][0-9]*)".toRegex(), 0) {
			prefix { n, _ ->
				Exp.Num(n.text.toInt())
			}
		}
		token(TokenTypes.Plus, "\\+".toRegex(), 1) {
			infix { _, left ->
				val right = expression(1)
				Exp.Plus(left!!, right)
			}
		}
	}

	val expressions = p.parse("""
		5 + 9 + 77; 2 + 18
		7 + 50
	""".trimIndent())

	println(expressions.joinToString("\n"))
}