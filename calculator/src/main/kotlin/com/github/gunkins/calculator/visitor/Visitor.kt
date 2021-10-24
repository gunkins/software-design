package com.github.gunkins.calculator.visitor

import com.github.gunkins.calculator.tokenizer.BraceToken
import com.github.gunkins.calculator.tokenizer.NumberToken
import com.github.gunkins.calculator.tokenizer.OperationToken
import com.github.gunkins.calculator.tokenizer.Token

interface Visitor {
    fun visit(token: NumberToken)
    fun visit(token: BraceToken)
    fun visit(token: OperationToken)
    fun visitAfter()
}

fun List<Token>.accept(visitor: Visitor) {
    for (token in this) {
        when (token) {
            is NumberToken -> visitor.visit(token)
            is BraceToken -> visitor.visit(token)
            is OperationToken -> visitor.visit(token)
        }
    }
    visitor.visitAfter()
}

