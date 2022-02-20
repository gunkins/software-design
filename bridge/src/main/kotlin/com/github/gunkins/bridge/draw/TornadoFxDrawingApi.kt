package com.github.gunkins.bridge.draw

import javafx.scene.layout.StackPane
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import tornadofx.*

class TornadoFxDrawingApi(override val width: Double, override val height: Double) : DrawingApi {

    init {
        TornadoFxViewConfig.width = width
        TornadoFxViewConfig.height = height
    }

    override fun drawCircle(centerX: Double, centerY: Double, radius: Double) {
        TornadoFxViewConfig.circles += Circle(centerX, centerY, radius)
    }

    override fun drawLine(startX: Double, startY: Double, endX: Double, endY: Double) {
        TornadoFxViewConfig.lines += Line(startX, startY, endX, endY)
    }

    override fun draw() {
        launch<TornadoFxApp>()
    }
}

internal class TornadoFxApp : App(TornadoFxView::class)

internal class TornadoFxView : View() {
    override val root: StackPane = stackpane {
        setPrefSize(TornadoFxViewConfig.width, TornadoFxViewConfig.height)
        group(TornadoFxViewConfig.circles + TornadoFxViewConfig.lines)
    }
}

private object TornadoFxViewConfig {
    val circles: MutableList<Circle> = mutableListOf()
    val lines: MutableList<Line> = mutableListOf()
    var width: Double = 800.0
    var height: Double = 600.0
}
