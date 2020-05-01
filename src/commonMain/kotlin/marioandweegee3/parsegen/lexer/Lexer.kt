package marioandweegee3.parsegen.lexer

class Lexer internal constructor(
		private val source:String,
		private val rules:Map<String, TokenRule>,
		private val skips:Set<Char>
) {
	private var start = 0
	private var line = 1L

	private var skip = Regex(skips.joinToString("|"))

	internal fun tokenize():List<Token> {
		val tokens = ArrayList<Token>()

		var token = nextToken()
		while(token != null) {
			tokens += token
			token = nextToken()
		}

		return tokens
	}

	private fun nextToken():Token? {
		if(start >= source.length){
			return null
		}

		if(source[start] == '\n') {
			line++
		}

		if(skips.isNotEmpty()) {
			while(skip.find(source, start)?.takeIf {it.range.first == start} != null) {
				val m = skip.find(source, start)

				m?.takeIf {
					it.range.first == start
				}?.also {
					start = it.range.first+1
				}
			}
		}

		for((type, rule) in rules) {
			val regex = rule.regex
			val match = regex.find(source, start)

			match?.takeIf {
				it.range.first == start
			}?.also {
				start = it.range.last+1
				return Token(type, it.value, line)
			}
		}

		return null
	}
}