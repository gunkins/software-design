package com.github.gunkins.bridge.graph

import com.github.gunkins.bridge.draw.DrawingApi

class MatrixGraph(
    private val matrix: Array<BooleanArray>,
    drawingApi: DrawingApi
) : Graph(drawingApi) {

    init {
        if (matrix.any { row -> row.size != matrix.size }) {
            throw IllegalArgumentException("Expected square matrix")
        }
    }

    override fun drawGraph() {
        val adjacencyLists = List<MutableList<Int>>(matrix.size) { mutableListOf() }
        matrix.indices.forEach { i ->
            matrix[i].indices.forEach { j ->
                if (matrix[i][j]) {
                    adjacencyLists[i] += j
                }
            }
        }

        drawGraphFromAdjacencyLists(adjacencyLists)
    }
}