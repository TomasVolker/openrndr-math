package tomasvolker.grafiko

import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2

/**
 * Equivalent to (view * model * point.xy01).xy
 */
fun Drawer.project(point: Vector2): Vector2 {
    val worldX = model.c0r0 * point.x + model.c1r0 * point.y + model.c3r0
    val worldY = model.c0r1 * point.x + model.c1r1 * point.y + model.c3r1
    val worldZ = model.c0r2 * point.x + model.c1r2 * point.y + model.c3r2

    val viewX = view.c0r0 * worldX + view.c1r0 * worldY + view.c2r0 * worldZ + view.c3r0
    val viewY = view.c0r1 * worldX + view.c1r1 * worldY + view.c2r1 * worldZ + view.c3r1
    return Vector2(viewX, viewY)
}


fun Drawer.project(pointList: List<Vector2>): List<Vector2> =
    pointList.map { project(it) }

fun Drawer.isolatedOrtho(block: Drawer.()->Unit) {
    isolated {
        model = Matrix44.IDENTITY
        view = Matrix44.IDENTITY
        ortho()

        block()

    }
}

fun Drawer.projected(
    point: Vector2,
    block: Drawer.(point: Vector2)->Unit
) {
    val projected = project(point)
    isolatedOrtho {
        block(projected)
    }
}

fun Drawer.projected(
    point: List<Vector2>,
    block: Drawer.(pointList: List<Vector2>)->Unit
) {
    val projected = project(point)
    isolatedOrtho {
        block(projected)
    }
}
