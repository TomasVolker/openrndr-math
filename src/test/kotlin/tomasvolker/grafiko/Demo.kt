package tomasvolker.grafiko

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import kotlin.math.cos

fun main() {

    application {

        configure {
            width = 1000
            height = 800
        }

        program {

            val x = List(1000) { it.toDouble() }
            val y = x.map { 100.0 * cos(it / 20.0) }

            backgroundColor = ColorRGBa.WHITE

            extend(PanZoom())
            extend(Grid2D())

            extend {
                drawer.fill = ColorRGBa.RED
                drawer.stroke = ColorRGBa.RED
                drawer.lineStrip(
                    x.zip(y) { x, y -> Vector2(x, y) }
                )

            }

        }

    }

}