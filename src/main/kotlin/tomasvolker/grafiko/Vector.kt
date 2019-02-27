package tomasvolker.grafiko

import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.Vector4

val Vector2.xy00 get() = Vector4(x, y, 0.0, 0.0)

fun Vector2.translated(x: Double = 0.0, y: Double = 0.0) =
        Vector2(this.x + x, this.y + y)

fun Vector3.translated(
    x: Double = 0.0,
    y: Double = 0.0,
    z: Double = 0.0
) =
    Vector3(this.x + x, this.y + y, this.z + z)

val Vector4.xy get() = Vector2(x, y)


