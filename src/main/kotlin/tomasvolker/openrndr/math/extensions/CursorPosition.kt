package tomasvolker.openrndr.math.extensions

import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import tomasvolker.openrndr.math.isolatedOrtho
import tomasvolker.openrndr.math.xy

class CursorPosition: Extension {

    override var enabled: Boolean = true

    val font = Resources.defaultFont

    val boxWidth = 200.0
    val boxHeight = 16.0

    override fun afterDraw(drawer: Drawer, program: Program) {

        val cursorPosition = (drawer.view.inversed * program.mouse.position.xy01).xy

        drawer.isolatedOrtho {

            fontMap = font
            fill = ColorRGBa.WHITE.shade(0.9)
            stroke = null

            rectangle(
                x = width - boxWidth,
                y = height - boxHeight,
                width = boxWidth,
                height = boxHeight
            )

            fill = ColorRGBa.BLACK
            text(
                "cursor: %.4g, %.4g".format(cursorPosition.x, cursorPosition.y),
                x = width - boxWidth,
                y = height - 4.0
            )

        }

    }

}