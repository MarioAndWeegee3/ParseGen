package marioandweegee3.parsegen.err

import marioandweegee3.parsegen.lexer.Token

class ParserError(override val message:String, val at:Token?):Exception()