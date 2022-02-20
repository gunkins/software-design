package com.github.gunkins.bridge.draw

interface DrawingApi {
    val width: Double
    val height: Double

    fun drawCircle(centerX: Double, centerY: Double, radius: Double)
    fun drawLine(startX: Double, startY: Double, endX: Double, endY: Double)
    fun draw()
}