package tomasvolker.grafiko

import org.openrndr.math.Matrix44
import org.openrndr.math.Quaternion
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.transforms.rotate as _rotate
import org.openrndr.math.transforms.translate as _translate
import org.openrndr.math.transforms.scale as _scale

class TransformPipeBuilder {
    var transform: Matrix44 = Matrix44.IDENTITY

    fun pipe(matrix: Matrix44) {
        transform = matrix * transform
    }

    fun rotate(axis: Quaternion) = pipe(axis.matrix)
    fun rotate(axis: Vector3 = Vector3.UNIT_Z, degrees:Double) = pipe(_rotate(axis, degrees))

    fun translate(offset: Vector3)= pipe(_translate(offset))
    fun translate(offset: Vector2) = pipe(_translate(offset.xy0))
    fun translate(x:Double, y:Double, z:Double = 0.0) = pipe(_translate(Vector3(x, y, z)))

    fun scale(factor: Double) = pipe(_scale(factor, factor, factor))
    fun scale(x: Double = 1.0, y: Double = 1.0, z: Double = 1.0) = pipe(_scale(x, y, z))
    fun scale(factor: Vector3) = pipe(_scale(factor.x, factor.y, factor.z))

    inline fun pivot(position: Vector2, block: TransformPipeBuilder.()->Unit) {
        translate(-position)
        block()
        translate(position)
    }

    inline fun pivot(position: Vector3, block: TransformPipeBuilder.()->Unit) {
        translate(-position)
        block()
        translate(position)
    }

}

inline fun pipeTransforms(builder: TransformPipeBuilder.() -> Unit) =
    TransformPipeBuilder().apply(builder).transform
