package com.github.gunkins.calculator

import com.github.gunkins.calculator.tokenizer.tokenize
import com.github.gunkins.calculator.visitor.CalculateVisitor
import com.github.gunkins.calculator.visitor.PrintVisitor
import com.github.gunkins.calculator.visitor.ShuntingYardVisitor
import com.github.gunkins.calculator.visitor.accept

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