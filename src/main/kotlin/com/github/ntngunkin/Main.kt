package com.github.ntngunkin

import com.github.ntngunkin.tokenizer.tokenize
import com.github.ntngunkin.visitor.CalculateVisitor
import com.github.ntngunkin.visitor.PrintVisitor
import com.github.ntngunkin.visitor.ShuntingYardVisitor
import com.github.ntngunkin.visitor.accept

fun main() {
    val printVisitor = PrintVisitor()
    val shuntingYardVisitor = ShuntingYardVisitor()
    val calculateVisitor = CalculateVisitor()

    val tokens = tokenize("5 - 100 - 100/10 + 3 -  34*12*34")

    tokens.accept(printVisitor)
    tokens.accept(shuntingYardVisitor)

    val parsedTokens = shuntingYardVisitor.reversPolishNotationTokens

    parsedTokens.accept(printVisitor)
    parsedTokens.accept(calculateVisitor)

    println(calculateVisitor.result)
}