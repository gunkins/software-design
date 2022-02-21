package com.github.gunkins.calculator.visitor

import com.github.gunkins.calculator.tokenizer.BraceToken
import com.github.gunkins.calculator.tokenizer.Div
import com.github.gunkins.calculator.tokenizer.LeftBrace
import com.github.gunkins.calculator.tokenizer.Minus
import com.github.gunkins.calculator.tokenizer.Multiple
import com.github.gunkins.calculator.tokenizer.NumberToken
import com.github.gunkins.calculator.tokenizer.OperationToken
import com.github.gunkins.calculator.tokenizer.Plus
import com.github.gunkins.calculator.tokenizer.RightBrace
import com.github.gunkins.calculator.tokenizer.Token

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