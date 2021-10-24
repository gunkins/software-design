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

class PrintVisitor : Visitor {
    override fun visit(token: NumberToken) {
        print("Number(${token.value}) ")
    }

    override fun visit(token: BraceToken) {
        when (token) {
            LeftBrace -> print("Left ")
            RightBrace -> print("Right ")
        }
    }

    override fun visit(token: OperationToken) {
        when (token) {
            Div -> print("Div ")
            Minus -> print("Minus ")
            Multiple -> print("Multiple ")
            Plus -> print("Plus ")
        }
    }

    override fun visitAfter() {
        println()
    }
}