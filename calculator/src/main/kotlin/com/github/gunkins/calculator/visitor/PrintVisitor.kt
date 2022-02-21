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