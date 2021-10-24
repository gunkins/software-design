package com.github.ntngunkin.visitor

import com.github.ntngunkin.tokenizer.BraceToken
import com.github.ntngunkin.tokenizer.NumberToken
import com.github.ntngunkin.tokenizer.OperationToken
import com.github.ntngunkin.tokenizer.Token

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

