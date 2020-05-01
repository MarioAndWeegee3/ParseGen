package boolean

import marioandweegee3.parsegen.ast.Expression
import marioandweegee3.parsegen.lexer.ParseRule
import marioandweegee3.parsegen.lexer.Token
import marioandweegee3.parsegen.buildParser

object TokenType {
	const val Bool = "bool"
	const val And = "and"
	const val Or = "or"
	const val Xor = "xor"
	const val Not = "not"
	const val LParen = "left paren"
	const val RParen = "right paren"
}

object Exp {
	data class Bool(val value:Boolean):Expression() {
		override fun toString():String {
			return value.toString()
		}
	}
	data class Binary(val left:Expression, val op:Token, val right:Expression):Expression() {
		override fun toString():String {
			return "(${op.type} $left $right)"
		}
	}
	data class Unary(val op:Token, val exp:Expression):Expression() {
		override fun toString():String {
			return "(${op.type} $exp)"
		}
	}
}

fun main(){
	fun binary(precedence:Int):ParseRule = { op, left ->
		val right = expression(precedence+1)
		Exp.Binary(left!!, op, right)
	}

	fun unary(precedence:Int):ParseRule = {op, _ ->
		val exp = expression(precedence)
		Exp.Unary(op, exp)
	}

	fun eval(exp:Expression):Boolean {
		return when(exp) {
			is Exp.Bool -> exp.value
			is Exp.Binary -> {
				val left = eval(exp.left)
				when(exp.op.type) {
					TokenType.And ->
						if(!left) left else eval(exp.right)
					TokenType.Or ->
						if(left) left else eval(exp.right)
					TokenType.Xor -> {
						val right = eval(exp.right)
						!(left && right || !left && !right)
					}
					else -> false.also {
						print("Invalid ")
					}
				}
			}
			is Exp.Unary -> {
				val e = eval(exp.exp)
				when(exp.op.type) {
					TokenType.Not -> !e
					else -> false.also {
						print("Invalid ")
					}
				}
			}
			else -> false.also {
				print("Invalid ")
			}
		}
	}

	val p = buildParser {
		skip(' ', '\r', '\t', '\n')
		token(TokenType.Bool, "true|false".toRegex(), 0) {
			prefix { b, _ ->
				Exp.Bool(b.text.toBoolean())
			}
		}
		token(TokenType.LParen, "\\(".toRegex(), 0) {
			prefix {_, _ ->
				val exp = expression(0)
				consume("Unclosed grouping", TokenType.RParen)
				exp
			}
		}
		token(TokenType.RParen, "\\)".toRegex(), 0) {}
		token(TokenType.And, "and|&".toRegex(), 1) {
			infix(binary(1))
		}
		token(TokenType.Or, "or|\\|".toRegex(), 2) {
			infix(binary(2))
		}
		token(TokenType.Xor, "xor|\\^".toRegex(), 3) {
			infix(binary(3))
		}
		token(TokenType.Not, "not|!".toRegex(), 4) {
			prefix(unary(4))
		}
	}

	val expressions = p.parse("""
		false | !false
	""".trimIndent())

	for(e in expressions) {
		println("$e = ${eval(e)}")
	}
}