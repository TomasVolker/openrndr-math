package tomasvolker.openrndr.math.extensions

import tomasvolker.openrndr.math.pipeTransforms
import org.openrndr.Extension
import org.openrndr.MouseButton
import org.openrndr.MouseEvent
import org.openrndr.Program
import org.openrndr.draw.Drawer
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import tomasvolker.openrndr.math.xy
import kotlin.math.exp

class Camera2D {

    var scrollSpeed: Double = 0.1
    var zoomDragSpeed: Double = 0.002
    var damping = 20.0

    var view = Matrix44.IDENTITY
    var target = Matrix44.IDENTITY

    fun cameraToWorld(position: Vector2) = (view.inversed * position.xy01).xy
    fun worldToCamera(position: Vector2) = (view * position.xy01).xy

    sealed class State {
        object Idle: State()
        object Pan: State()
        class Zoom(val pivot: Vector2): State()
    }


    var state: State = State.Idle

    var lastTime: Double = 0.0

    fun update(time: Double) {
        val delta = time - lastTime

        //view = target

        view += (target - view) * (damping * delta).coerceAtMost(1.0)

        lastTime = time
    }

    fun mouseDown(event: MouseEvent) {

        state = when(event.button) {
            MouseButton.LEFT -> State.Pan
            MouseButton.RIGHT -> State.Zoom(cameraToWorld(event.position))
            else -> State.Idle
        }

    }

    fun mouseUp(event: MouseEvent) {
        state = State.Idle
    }

    fun mouseDragged(event: MouseEvent) {
        val state = state

        val delta = event.dragDisplacement

        target *= when(state) {
            is State.Pan -> pipeTransforms {
                translate((view.inversed * delta.xy0).xy)
            }
            is State.Zoom -> pipeTransforms {
                pivot(state.pivot) {
                    scale(
                        x = exp(zoomDragSpeed * delta.x),
                        y = exp(-zoomDragSpeed * delta.y)
                    )
                }
            }
            is State.Idle -> Matrix44.IDENTITY
        }

    }

    fun mouseScrolled(event: MouseEvent) {
        val worldPosition = cameraToWorld(event.position)

        target *= pipeTransforms {
            pivot(worldPosition) {
                scale(exp(scrollSpeed * event.rotation.y))
            }
        }
    }
}

class PanZoom : Extension {

    override var enabled: Boolean = true

    val camera = Camera2D()

    override fun setup(program: Program) {

        program.mouse.buttonDown.listen {
            if (!it.propagationCancelled) {
                camera.mouseDown(it)
            }
        }

        program.mouse.buttonUp.listen {
            if (!it.propagationCancelled) {
                camera.mouseUp(it)
            }
        }

        program.mouse.dragged.listen {
            if (!it.propagationCancelled) {
                camera.mouseDragged(it)
            }
        }

        program.mouse.scrolled.listen {
            if (!it.propagationCancelled) {
                camera.mouseScrolled(it)
            }
        }

    }
    override fun beforeDraw(drawer: Drawer, program: Program) {
        if (enabled) {
            camera.update(program.seconds)
            drawer.view = camera.view
        }
    }
}