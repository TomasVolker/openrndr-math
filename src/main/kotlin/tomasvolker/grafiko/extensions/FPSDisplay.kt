package tomasvolker.grafiko.extensions

import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import tomasvolker.grafiko.isolatedOrtho
import tomasvolker.grafiko.primitives.d

class FPSDisplay : Extension {
    override var enabled: Boolean = true

    var lastTime: Double = 0.0

    val font = Resources.defaultFont

    override fun setup(program: Program) {
        lastTime = program.seconds
    }

    override fun afterDraw(drawer: Drawer, program: Program) {

        val now = program.seconds

        drawer.isolatedOrtho {

            fontMap = font

            fill = ColorRGBa.WHITE
            stroke = null

            rectangle(
                x = 0.0,
                y = height - 16.0,
                width = 100.0,
                height = 16.0
            )

            fill = ColorRGBa.BLACK
            text(
                "fps: %.2f".format(1.0 / (now - lastTime)),
                x = 10.0,
                y = height.d - 10.0
            )
        }

        lastTime = now
    }
}