package tomasvolker.grafiko

import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import tomasvolker.grafiko.plot.plotLine
import tomasvolker.grafiko.plot.plotScatter
import tomasvolker.grafiko.plot.quickPlot
import tomasvolker.grafiko.primitives.d
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun main() {

    val size = 1000

    val x = (0 until size).map { 20.0 * it.d / size }
    val y = x.map { sin(2 * PI * it / 10.0) }
    val y2 = x.map { cos(2 * PI * it / 10.0) }

    val pointList = x
        .zip(y) { x, y -> Vector2(x, y) }

    val pointList2 = x
        .zip(y2) { x, y -> Vector2(x, y)}

    quickPlot {

        stroke = ColorRGBa.RED
        strokeWeight = 2.0
        plotLine(pointList)

        stroke = null
        fill = ColorRGBa.BLUE
        strokeWeight = 2.0
        plotScatter(pointList2)

    }


}