package tomasvolker.grafiko

import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.math.*
import org.openrndr.math.transforms.lookAt
import kotlin.math.abs
import kotlin.math.*

fun Vector3.Companion.fromSphericalZ(s: Spherical): Vector3 {
    val sinPhiRadius = Math.sin(s.phi) * s.radius
    return Vector3(
        sinPhiRadius * cos(s.theta),
        sinPhiRadius * sin(s.theta),
        cos(s.phi) * s.radius
    )
}

fun Spherical.Companion.fromVectorZ(vector: Vector3): Spherical {
    val r = vector.length
    return Spherical(
        r,
        if (r == 0.0) 0.0 else Math.atan2(vector.y, vector.x),
        if (r == 0.0) 0.0 else Math.acos(clamp(vector.z / r, -1.0, 1.0)))
}

class FirstPersonCamera(eye: Vector3, lookAt: Vector3) {

    var position = eye
        private set

    var direction = Spherical.fromVectorZ((lookAt - eye).normalized)
        private set

    private var targetPosition = position
    private var targetDirection = this.direction
    private var dirty: Boolean = true

    var dampingFactor = 0.05

    val forwardVersor get() = Vector3.fromSphericalZ(direction)
    val rightVersor get() = (forwardVersor cross Vector3.UNIT_Z).normalized
    val upVersor get() = (rightVersor cross forwardVersor).normalized

    fun moveForward(delta: Double) {
        move(forwardVersor * delta)
    }

    fun moveRight(delta: Double) {
        move(rightVersor * delta)
    }

    fun moveUp(delta: Double) {
        move(upVersor * delta)
    }

    fun rotate(rotX: Double, rotY: Double) {
        targetDirection += Spherical(0.0, rotX, rotY)
        targetDirection = targetDirection.makeSafe()
        dirty = true
    }

    fun rotateTo(rotX: Double, rotY: Double) {
        targetDirection = targetDirection.copy(theta = rotX, phi = rotY)
        targetDirection = targetDirection.makeSafe()
        dirty = true
    }

    fun rotateTo(lookAt: Vector3) {
        targetDirection = Spherical.fromVectorZ(lookAt.normalized)
        targetDirection = targetDirection.makeSafe()
        dirty = true
    }

    fun move(delta: Vector3) {
        moveTo(targetPosition + delta)
    }

    fun moveTo(position: Vector3) {
        this.targetPosition = position
        dirty = true
    }
    fun update(timeDelta: Double) {
        if (!dirty) return
        dirty = false

        val dampingFactor = dampingFactor * timeDelta / 0.0060
        val positionDelta = targetPosition - position
        val lookAtDelta = targetDirection - direction

        if (
            abs(positionDelta.x) > EPSILON ||
            abs(positionDelta.y) > EPSILON ||
            abs(positionDelta.z) > EPSILON ||
            abs(lookAtDelta.radius) > EPSILON ||
            abs(lookAtDelta.theta) > EPSILON ||
            abs(lookAtDelta.phi) > EPSILON
        ) {
            position += (positionDelta * dampingFactor)
            direction += (lookAtDelta * dampingFactor)
            dirty = true

        } else {
            position = targetPosition.copy()
            direction = targetDirection.copy()
        }
        direction = direction.makeSafe()
    }

    fun viewMatrix(): Matrix44 {
        return lookAt(
            eye = position,
            target = position + Vector3.fromSphericalZ(direction), up = Vector3.UNIT_Z)
    }

    companion object {
        private const val EPSILON = 0.000001
    }
}

class FirstPersonControls(
    val firstPersonCamera: FirstPersonCamera
) {
    enum class State {
        NONE,
        ROTATE
    }

    private var state = State.ROTATE

    private lateinit var program: Program
    private var lastMousePosition: Vector2 = Vector2.ZERO

    private val keyPressedMap = mutableSetOf<Int>()

    private fun mouseScrolled(event: MouseEvent) {

        if (Math.abs(event.rotation.x) > 0.1) return

        when {
            event.rotation.y > 0 -> firstPersonCamera.moveUp(1.0)
            event.rotation.y < 0 -> firstPersonCamera.moveUp(-1.0)
        }
    }

    private fun mouseMoved(event: MouseEvent) {

        if (state != State.NONE) {
            val delta = lastMousePosition - event.position
            lastMousePosition = event.position

            when (state) {
                State.ROTATE -> {
                    val rotX = 2 * Math.PI * delta.x / program.window.size.x
                    val rotY = 2 * Math.PI * delta.y / program.window.size.y
                    firstPersonCamera.rotate(rotX, -rotY)
                }
                else -> {}
            }

        }

    }

    private fun mouseButtonDown(event: MouseEvent) {
        val previousState = state

        when (event.button) {
            MouseButton.LEFT -> {
                state = State.ROTATE
            }
            else -> {}
        }

        if (previousState == State.NONE) {
            lastMousePosition = event.position
        }

    }

    fun setup(program: Program) {
        this.program = program
        program.mouse.moved.listen { mouseMoved(it) }
/*
        program.mouse.buttonDown.listen { mouseButtonDown(it) }
        program.mouse.buttonUp.listen { state = State.NONE }
*/
        program.mouse.scrolled.listen { mouseScrolled(it) }
        program.keyboard.keyDown.listen { keyPressedMap.add(it.key) }
        program.keyboard.keyUp.listen { keyPressedMap.remove(it.key) }
    }

    fun update(timeDelta: Double) {

        keyPressedMap.forEach {
            val direction = when (it.toChar()) {
                'D' -> firstPersonCamera.rightVersor
                'A' -> -firstPersonCamera.rightVersor
                'W' -> firstPersonCamera.forwardVersor
                'S' -> -firstPersonCamera.forwardVersor
                'E' -> firstPersonCamera.upVersor
                'Q' -> -firstPersonCamera.upVersor
                else -> Vector3.ZERO
            }

            firstPersonCamera.move(direction * timeDelta * 10.0)
        }

    }
}


class FirstPerson(
    eye: Vector3 = Vector3(1.0, 1.0, 1.0),
    lookAt: Vector3 = Vector3.ZERO,
    private val fov: Double = 90.0
) : Extension {

    override var enabled: Boolean = true
    val firstPersonCamera = FirstPersonCamera(eye, lookAt)
    private val firstPersonControls = FirstPersonControls(firstPersonCamera)
    private var lastSeconds: Double = -1.0

    override fun beforeDraw(drawer: Drawer, program: Program) {
        if (lastSeconds == -1.0) lastSeconds = program.seconds

        val delta = program.seconds - lastSeconds
        lastSeconds = program.seconds
        firstPersonCamera.update(delta)
        firstPersonControls.update(delta)

        drawer.depthTestPass = DepthTestPass.LESS_OR_EQUAL
        drawer.depthWrite = true

        drawer.background(ColorRGBa.BLACK)
        drawer.perspective(fov, program.window.size.x / program.window.size.y, 0.1, 1000.0)
        drawer.view = firstPersonCamera.viewMatrix()
    }

    override fun afterDraw(drawer: Drawer, program: Program) {
        drawer.isolated {
            drawer.view = Matrix44.IDENTITY
            drawer.ortho()
        }
    }

    override fun setup(program: Program) {
        firstPersonControls.setup(program)
    }
}