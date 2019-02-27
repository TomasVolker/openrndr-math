package tomasvolker.grafiko

import numeriko.openrndr.pipeTransforms
import numeriko.openrndr.xy
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.transforms.scale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun main() {

    application {

        configure {
            width = 1000
            height = 800
            windowResizable = true
        }

        program {

            val r = 40e6 / (2 * PI)

            val t = List(1001) { i -> i.toDouble() / 1000.0 }
            val x = t.map { r * cos(2 * PI * it)  }
            val y = t.map { r * sin(2 * PI * it) + r }

            backgroundColor = ColorRGBa.WHITE

            //extend(Axis())

            extend(PanZoom()) {
                camera.view = scale(
                    x = 1.0,
                    y = -1.0,
                    z = 1.0
                )
            }
            extend(Grid2D())

            extend {
                drawer.fill = ColorRGBa.RED
                drawer.stroke = ColorRGBa.RED

                val pointList = x
                    .zip(y) { x, y -> (drawer.view * Vector2(x, y).xy01).xy }

                drawer.isolated {
                    view = Matrix44.IDENTITY
                    drawer.strokeWeight = 2.0
                    drawer.lineStrip(pointList)
                }

                val line = listOf(Vector2(0.0, 0.0), Vector2(0.0, -2.0))
                    .map { (drawer.view * it.xy01).xy }

                drawer.stroke = ColorRGBa.BLUE

                drawer.isolated {
                    view = Matrix44.IDENTITY
                    drawer.strokeWeight = 2.0
                    drawer.lineStrip(line)
                }

            }

        }

    }

}