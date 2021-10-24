package com.github.ntngunkin.tokenizer

sealed class Token

class NumberToken(val value: Int) : Token()

sealed class OperationToken: Token()
object Plus : OperationToken()
object Minus : OperationToken()
object Multiple : OperationToken()
object Div : OperationToken()

sealed class BraceToken: Token()
object LeftBrace : BraceToken()
object RightBrace : BraceToken()
