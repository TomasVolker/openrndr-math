package tomasvolker.openrndr.math.extensions

import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import tomasvolker.openrndr.math.primitives.d
import kotlin.math.*

class Grid2D: Extension {

    override var enabled: Boolean = true

    var font = Resources.defaultFont

    override fun beforeDraw(drawer: Drawer, program: Program) {

        if (!enabled) return

        drawer.run {

            val xTicks = ticks(
                viewScaling = view.c0r0,
                viewDelta = view.c3r0,
                length = width.d
            )

            val yTicks = ticks(
                viewScaling = view.c1r1,
                viewDelta = view.c3r1,
                length = height.d
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
                Vector2(tick.cameraPosition, height.d)
            )
        )
        tick.tag?.let {
            text(
                it,
                x = tick.cameraPosition,
                y = height.d
            )
        }

    }

    fun Drawer.drawYTick(tick: Tick) {
        stroke = tick.color
        strokeWeight = tick.weight
        lineStrip(
            listOf(
                Vector2(0.0, tick.cameraPosition),
                Vector2(width.d, tick.cameraPosition)
            )
        )
        tick.tag?.let {
            text(
                it,
                x = 0.0,
                y = tick.cameraPosition
            )
        }

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
                color = if (worldPosition == 0.0) ColorRGBa.BLACK else ColorRGBa.GRAY,
                weight = if (worldPosition == 0.0) 1.1 else 1.0,
                tag = "%.1g".format(worldPosition)
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
        val tag: String? = null
    )

}
