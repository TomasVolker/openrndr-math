package tomasvolker.openrndr.math.extensions

import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import tomasvolker.openrndr.math.isolatedOrtho
import tomasvolker.openrndr.math.primitives.d
import tomasvolker.openrndr.math.scaleCentered

class Axis: Extension {

    override var enabled: Boolean = true

    private lateinit var offscreen: RenderTarget

    lateinit var bounds: Rectangle

    override fun setup(program: Program) {

        offscreen = renderTarget(program.width, program.height) {
            colorBuffer()
        }

        bounds = Rectangle(
            Vector2.ZERO,
            program.width.d,
            program.height.d
        )

    }

    override fun beforeDraw(drawer: Drawer, program: Program) {
        offscreen.bind()
        drawer.background(program.backgroundColor ?: ColorRGBa.WHITE)
    }

    override fun afterDraw(drawer: Drawer, program: Program) {
        offscreen.unbind()

        drawer.isolatedOrtho {
            val buffer = offscreen.colorBuffer(0)
            image(
                colorBuffer = buffer,
                source = buffer.bounds,
                target = bounds.scaleCentered(0.8)
            )
        }
    }

}