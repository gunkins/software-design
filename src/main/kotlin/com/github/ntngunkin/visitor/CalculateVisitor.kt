package com.github.ntngunkin.visitor

import com.github.ntngunkin.tokenizer.BraceToken
import com.github.ntngunkin.tokenizer.Div
import com.github.ntngunkin.tokenizer.Minus
import com.github.ntngunkin.tokenizer.Multiple
import com.github.ntngunkin.tokenizer.NumberToken
import com.github.ntngunkin.tokenizer.OperationToken
import com.github.ntngunkin.tokenizer.Plus

class CalculateVisitor : Visitor {
    private val stack: MutableList<Int> = mutableListOf()
    val result: Int
        get() {
            return stack.last()
        }

    override fun visit(token: NumberToken) {
        stack += token.value
    }

    override fun visit(token: BraceToken) {
        throw IllegalArgumentException("Unexpected brace token: $token")
    }

    override fun visit(token: OperationToken) {
        if (stack.size < 2) throw IllegalArgumentException("Incorrect reverse polish notation")

        val rhs = stack.removeLast()
        val lhs = stack.removeLast()

        stack += when (token) {
            Div -> lhs / rhs
            Minus -> lhs - rhs
            Multiple -> lhs * rhs
            Plus -> lhs + rhs
        }
    }

    override fun visitAfter() {
        if (stack.size != 1) throw IllegalStateException("Expression not fully evaluated, stack: $stack")
    }
}