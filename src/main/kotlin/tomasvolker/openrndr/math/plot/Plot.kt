package tomasvolker.openrndr.math.plot

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.DrawQuality
import org.openrndr.draw.Drawer
import org.openrndr.extensions.Debug3D
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import tomasvolker.openrndr.math.extensions.CursorPosition
import tomasvolker.openrndr.math.extensions.Grid2D
import tomasvolker.openrndr.math.extensions.PanZoom
import tomasvolker.openrndr.math.fromCorners
import tomasvolker.openrndr.math.pipeTransforms
import tomasvolker.openrndr.math.projected
import tomasvolker.openrndr.math.translated

fun Drawer.plotBars(width: Double, pointList: List<Vector2>) {
    rectangles(
        pointList.map {
            Rectangle.fromCorners(
                it.translated(x = -width / 2),
                Vector2(it.x + width / 2, 0.0)
            )
        }
    )
}

fun Drawer.plotLine(pointList: List<Vector2>) = when(drawStyle.quality) {
    DrawQuality.QUALITY -> projected(pointList) { lineStrip(it) }
    DrawQuality.PERFORMANCE -> lineStrip(pointList)
}

fun Drawer.plotScatter(pointList: List<Vector2>, radius: Double = 5.0) {
    projected(pointList) { list ->
        circles(list, radius)
    }
}

fun quickPlot2D(
    title: String = "Plot",
    width: Int = 640,
    height: Int = 480,
    yUpwards: Boolean = true,
    block: Drawer.()->Unit
) {

    application {

        configure {
            this.title = title
            this.width = width
            this.height = height
            windowResizable = true
        }

        program {

            backgroundColor = ColorRGBa.WHITE

            extend(PanZoom()) {
                camera.view = pipeTransforms {
                    if (yUpwards) scale(y = -1.0)
                    scale(100.0)
                }
            }
            extend(Grid2D())
            extend(CursorPosition())

            extend {
                drawer.block()
            }

        }

    }

}

fun quickPlot3D(
    title: String = "Plot",
    width: Int = 640,
    height: Int = 480,
    yUpwards: Boolean = true,
    block: Drawer.()->Unit
) {

    application {

        configure {
            this.title = title
            this.width = width
            this.height = height
            windowResizable = true
        }

        program {

            extend(Debug3D())

            extend {
                drawer.background(ColorRGBa.WHITE)
                drawer.block()
            }

        }

    }

}