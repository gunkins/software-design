package com.github.gunkins.bridge.graph

import com.github.gunkins.bridge.draw.DrawingApi
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

abstract class Graph(private val drawingApi: DrawingApi) {
    abstract fun drawGraph()

    protected fun drawGraphFromAdjacencyLists(adjacencyLists: List<List<Int>>) {
        val radius = min(drawingApi.width, drawingApi.height) / 50
        val centerX = drawingApi.width / 2
        val centerY = drawingApi.height / 2
        val r = 0.8 * min(centerX, centerY)

        val nodes = List(adjacencyLists.size) { i ->
            val angle = 2 * PI * i / adjacencyLists.size
            Node(
                x = centerX + r * cos(angle),
                y = centerY + r * sin(angle)
            )
        }

        for (node in nodes) {
            drawingApi.drawCircle(node.x, node.y, radius)
        }

        for ((i, neighbours) in adjacencyLists.withIndex()) {
            for (j in neighbours) {
                val fromNode = nodes[i]
                val toNode = nodes[j]
                drawingApi.drawLine(fromNode.x, fromNode.y, toNode.x, toNode.y)
            }
        }

        drawingApi.draw()
    }
}

private data class Node(val x: Double, val y: Double)

