package tomasvolker.grafiko

import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import kotlin.math.*

class Grid2D : Extension {

    override var enabled: Boolean = true

    var color = ColorRGBa.GRAY
    var gridWeight = 1.0

    val font = Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0)

    infix fun Double.modulo(other: Double) = ((this % other) + other) % other

    val Drawer.viewBounds get() =
        Rectangle(
            x = - view.c3r0 / view.c0r0,
            y = - view.c3r1 / view.c1r1,
            width = width / view.c0r0,
            height = height / view.c1r1
        )

    override fun beforeDraw(drawer: Drawer, program: Program) {

        if (!enabled) return

        drawer.run {

            val xTicks = ticks(
                viewScaling = view.c0r0,
                viewDelta = view.c3r0,
                length = width.toDouble()
            )

            val yTicks = ticks(
                viewScaling = view.c1r1,
                viewDelta = view.c3r1,
                length = height.toDouble()
            )

            isolated {
                model = Matrix44.IDENTITY
                view = Matrix44.IDENTITY
                ortho()

                fontMap = font
                fill = ColorRGBa.BLACK

                xTicks.forEach { drawXTick(it) }
                yTicks.forEach { drawYTick(it) }

            }

        }

    }

    fun Drawer.drawXTick(tick: Tick) {
        stroke = tick.color
        strokeWeight = tick.weight
        lineStrip(
            listOf(
                Vector2(tick.cameraPosition, 0.0),
                Vector2(tick.cameraPosition, height.toDouble())
            )
        )
        text(
            tick.tag,
            x = tick.cameraPosition,
            y = height.toDouble()
        )
    }

    fun Drawer.drawYTick(tick: Tick) {
        stroke = tick.color
        strokeWeight = tick.weight
        lineStrip(
            listOf(
                Vector2(0.0, tick.cameraPosition),
                Vector2(width.toDouble(), tick.cameraPosition)
            )
        )
        text(
            tick.tag,
            x = 0.0,
            y = tick.cameraPosition
        )
    }

    fun ticks(viewScaling: Double, viewDelta: Double, length: Double): List<Tick> {

        val delta = deltaFromViewScaling(viewScaling, length)
        val left = - viewDelta / viewScaling
        val right = length / viewScaling + left
        val range = right - left
        val first = ceil(left / delta) * delta
        val count = ceil(range / delta).roundToInt()

        return List(count) { i ->
            val worldPosition = first + delta * i
            Tick(
                worldPosition = worldPosition,
                cameraPosition = viewScaling * worldPosition + viewDelta,
                color = if(worldPosition == 0.0) ColorRGBa.BLACK else color,
                tag = String.format("%.1g", worldPosition)
            )
        }

    }

    fun deltaFromViewScaling(viewScaling: Double, length: Double): Double =
            10.0.pow(floor(log10(400.0 / viewScaling.absoluteValue))) * viewScaling.sign

    data class Tick(
        val worldPosition: Double,
        val cameraPosition: Double,
        val color: ColorRGBa = ColorRGBa.GRAY,
        val weight: Double = 1.0,
        val tag: String = ""
    )

}
