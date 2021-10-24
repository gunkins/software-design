package com.github.gunkins.bridge

import com.github.gunkins.bridge.draw.TornadoFxDrawingApi
import com.github.gunkins.bridge.draw.JetpackComposeDrawingApi
import com.github.gunkins.bridge.graph.ListGraph


fun main(args: Array<String>) {
    val width = 1280.0
    val height = 720.0

    val adjacencyLists = listOf(
        listOf(1, 2, 3, 4),
        listOf(0, 2),
        listOf(),
        listOf(),
        listOf(),
        listOf(),
        listOf(3, 4, 5),
    )

    val matrix = arrayOf(
        booleanArrayOf(false, true, true, true, true, false, false),
        booleanArrayOf(true, false, true, false, false, false, false),
        booleanArrayOf(true, true, false, false, false, false, false),
        booleanArrayOf(true, false, false, false, false, false, true),
        booleanArrayOf(true, false, false, false, false, false, true),
        booleanArrayOf(false, false, false, false, false, false, true),
        booleanArrayOf(false, false, false, true, true, true, false),
    )


    val jetpackComposeDrawingApi = JetpackComposeDrawingApi(width, height)
    val tornadoFxDrawingApi = TornadoFxDrawingApi(width, height)

    val graph = ListGraph(adjacencyLists, jetpackComposeDrawingApi)

    graph.drawGraph()
}