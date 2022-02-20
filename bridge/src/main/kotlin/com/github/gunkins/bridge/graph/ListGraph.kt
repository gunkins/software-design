package com.github.gunkins.bridge.graph

import com.github.gunkins.bridge.draw.DrawingApi

class ListGraph(
    private val adjacencyLists: List<List<Int>>,
    drawingApi: DrawingApi
) : Graph(drawingApi) {

    init {
        val nodes = adjacencyLists.size
        for (list in adjacencyLists) {
            for (value in list) {
                if (value !in 0 until nodes) throw IllegalArgumentException("Incorrect list value: $value")
            }
        }
    }

    override fun drawGraph() {
        drawGraphFromAdjacencyLists(adjacencyLists)
    }
}