package com.github.gunkins.bridge.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

class JetpackComposeDrawingApi(override val width: Double, override val height: Double) : DrawingApi {
    private val circles: MutableList<Circle> = mutableListOf()
    private val lines: MutableList<Line> = mutableListOf()

    override fun drawCircle(centerX: Double, centerY: Double, radius: Double) {
        circles += Circle(centerX, centerY, radius)
    }

    override fun drawLine(startX: Double, startY: Double, endX: Double, endY: Double) {
        lines += Line(startX, startY, endX, endY)
    }

    override fun draw() = application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Graph",
            state = rememberWindowState(width = width.dp, height = height.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize().background(Color.White)) {
                val paint = Paint().also { it.color = Color.Black }

                drawIntoCanvas { canvas ->
                    for (circle in circles) {
                        canvas.drawCircle(Offset(toPx(circle.x), toPx(circle.y)), toPx(circle.radius), paint)
                    }
                    for (line in lines) {
                        val startOffset = Offset(toPx(line.startX), toPx(line.startY))
                        val endOffset = Offset(toPx(line.endX), toPx(line.endY))
                        canvas.drawLine(startOffset, endOffset, paint)
                    }
                }
            }
        }
    }

    private fun Density.toPx(value: Double) = value.dp.toPx()
}

data class Circle(val x: Double, val y: Double, val radius: Double)
private data class Line(val startX: Double, val startY: Double, val endX: Double, val endY: Double)
