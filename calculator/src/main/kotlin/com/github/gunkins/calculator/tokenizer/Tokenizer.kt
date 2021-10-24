package com.github.gunkins.calculator.tokenizer

import java.lang.StringBuilder

fun tokenize(expression: String): List<Token> {
    var state: TokenizerState = Start
    val tokens = mutableListOf<Token>()

    for (char in expression) {
        val (nextState, producedTokens) = state.handleChar(char)

        if (nextState is Error) {
            throw IllegalArgumentException(nextState.message)
        }

        state = nextState
        tokens += producedTokens
    }

    val (_, producedTokens) = state.handleEnd()
    tokens += producedTokens

    return tokens
}

private data class StateTransition(val nextState: TokenizerState, val producedTokens: List<Token>)

private sealed class TokenizerState {
    abstract fun handleChar(char: Char): StateTransition

    abstract fun handleEnd(): StateTransition

    protected fun stateTransition(nextState: TokenizerState, token: Token? = null): StateTransition {
        val tokens = mutableListOf<Token>()
        token?.let { tokens += it }

        return StateTransition(nextState, tokens)
    }

    protected fun transitionToStart(token: Token? = null) = stateTransition(Start, token)
}

private object Start : TokenizerState() {
    override fun handleChar(char: Char): StateTransition {
        if (char.isWhitespace()) {
            return transitionToStart()
        }

        return when (char) {
            '+' -> transitionToStart(token = Plus)
            '-' -> transitionToStart(token = Minus)
            '*' -> transitionToStart(token = Multiple)
            '/' -> transitionToStart(token = Div)
            '(' -> transitionToStart(token = LeftBrace)
            ')' -> transitionToStart(token = RightBrace)
            in '0'..'9' -> stateTransition(nextState = Number.withFirstChar(char))
            else -> stateTransition(nextState = Error("Unexpected character: $char"))
        }
    }

    override fun handleEnd(): StateTransition = transitionToStart()
}

private data class Number(private val numberBuilder: StringBuilder) : TokenizerState() {
    companion object {
        fun withFirstChar(char: Char) = Number(StringBuilder().append(char))
    }

    override fun handleChar(char: Char): StateTransition {
        if (char.isDigit()) {
            numberBuilder.append(char)
            return stateTransition(this)
        }

        val producedTokens = mutableListOf<Token>(getNumberToken())

        val (nextState, tokens) = Start.handleChar(char)
        producedTokens += tokens

        return StateTransition(nextState, producedTokens)
    }

    override fun handleEnd(): StateTransition {
        return stateTransition(End, token = getNumberToken())
    }

    private fun getNumberToken() = NumberToken(numberBuilder.toString().toInt())
}

private class Error(val message: String) : TokenizerState() {
    override fun handleChar(char: Char): StateTransition = stateTransition(nextState = this)
    override fun handleEnd(): StateTransition = stateTransition(nextState = this)
}

private object End : TokenizerState() {
    override fun handleChar(char: Char): StateTransition = stateTransition(this)
    override fun handleEnd(): StateTransition = stateTransition(this)
}