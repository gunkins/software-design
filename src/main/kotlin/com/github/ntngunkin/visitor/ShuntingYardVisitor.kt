package com.github.ntngunkin.visitor

import com.github.ntngunkin.tokenizer.BraceToken
import com.github.ntngunkin.tokenizer.Div
import com.github.ntngunkin.tokenizer.LeftBrace
import com.github.ntngunkin.tokenizer.Minus
import com.github.ntngunkin.tokenizer.Multiple
import com.github.ntngunkin.tokenizer.NumberToken
import com.github.ntngunkin.tokenizer.OperationToken
import com.github.ntngunkin.tokenizer.Plus
import com.github.ntngunkin.tokenizer.RightBrace
import com.github.ntngunkin.tokenizer.Token

class ShuntingYardVisitor : Visitor {
    private val stack: MutableList<Token> = mutableListOf()
    private val _reversPolishNotationTokens: MutableList<Token> = mutableListOf()

    val reversPolishNotationTokens: List<Token>
        get() {
            return _reversPolishNotationTokens
        }

    override fun visit(token: NumberToken) {
        _reversPolishNotationTokens += token
    }

    override fun visit(token: BraceToken) {
        when (token) {
            is LeftBrace -> stack += token
            is RightBrace -> {
                while (stack.isNotEmpty() && stack.last() != LeftBrace) {
                    _reversPolishNotationTokens += stack.removeLast()
                }
                if (stack.isEmpty()) throw IllegalArgumentException("Incorrect expression: unpaired braces")
                stack.removeLast()
            }
        }
    }

    override fun visit(token: OperationToken) {
        while (stack.isNotEmpty()) {
            val last = stack.last()
            if (last is OperationToken && last.precedence() >= token.precedence()) {
                _reversPolishNotationTokens += stack.removeLast()
            } else {
                break
            }
        }
        stack += token
    }

    override fun visitAfter() {
        while (stack.isNotEmpty()) {
            if (stack.last() !is OperationToken) {
               throw IllegalArgumentException("Incorrect expression: unpaired braces")
            }
            _reversPolishNotationTokens += stack.removeLast()
        }
    }

    private fun OperationToken.precedence(): Int =
        when (this) {
            is Div -> 1
            is Multiple -> 1
            is Minus -> 0
            is Plus -> 0
        }
}