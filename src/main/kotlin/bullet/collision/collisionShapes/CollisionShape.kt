package bullet.collision.collisionShapes

import bullet.collision.broadphaseCollision.BroadphaseNativeTypes
import bullet.linearMath.Transform
import bullet.linearMath.Vec3

/** The CollisionShape class provides an interface for collision shapes that can be shared among CollisionObjects.  */
abstract class CollisionShape {

    var shapeType = BroadphaseNativeTypes.INVALID_SHAPE_PROXYTYPE
    //    void* m_userPointer = null;
    var userIndex = -1

    /** getAabb returns the axis aligned bounding box in the coordinate frame of the given transform t. */
    abstract fun getAabb(t: Transform, aabbMin: Vec3, aabbMax: Vec3)

    open fun getBoundingSphere(center: Vec3, radius: FloatArray) {
        val tr = Transform()
        tr.setIdentity()
        val aabbMin = Vec3()
        val aabbMax = Vec3()

        getAabb(tr, aabbMin, aabbMax)

        radius[0] = (aabbMax - aabbMin).length() * 0.5f
        center put ((aabbMin + aabbMax) * 0.5f)
    }

    /** getAngularMotionDisc returns the maximum radius needed for Conservative Advancement to handle time-of-impact
     *  with rotations. */
    open fun getAngularMotionDisc(): Float {
        ///@todo cache this value, to improve performance
        val center = Vec3()
        val disc = FloatArray(1)
        getBoundingSphere(center, disc)
        disc[0] += center.length()
        return disc[0]
    }

    open fun getContactBreakingThreshold(defaultContactThresholdFactor: Float) = getAngularMotionDisc() * defaultContactThresholdFactor

    /** calculateTemporalAabb calculates the enclosing aabb for the moving object over interval [0..timeStep)
     *  result is conservative  */
    fun calculateTemporalAabb(curTrans: Transform, linvel: Vec3, angvel: Vec3, timeStep: Float, temporalAabbMin: Vec3,
                              temporalAabbMax: Vec3) {
        //start with static aabb
        getAabb(curTrans, temporalAabbMin, temporalAabbMax)

        var temporalAabbMaxx = temporalAabbMax.x
        var temporalAabbMaxy = temporalAabbMax.y
        var temporalAabbMaxz = temporalAabbMax.z
        var temporalAabbMinx = temporalAabbMin.x
        var temporalAabbMiny = temporalAabbMin.y
        var temporalAabbMinz = temporalAabbMin.z

        // add linear motion
        val linMotion = linvel * timeStep
        ///@todo: simd would have a vector max/min operation, instead of per-element access
        if (linMotion.x > 0f)
            temporalAabbMaxx += linMotion.x
        else
            temporalAabbMinx += linMotion.x
        if (linMotion.y > 0f)
            temporalAabbMaxy += linMotion.y
        else
            temporalAabbMiny += linMotion.y
        if (linMotion.z > 0f)
            temporalAabbMaxz += linMotion.z
        else
            temporalAabbMinz += linMotion.z

        //add conservative angular motion
        val angularMotion = angvel.length() * getAngularMotionDisc() * timeStep
        val angularMotion3d = Vec3(angularMotion)
        temporalAabbMin.put(temporalAabbMinx, temporalAabbMiny, temporalAabbMinz)
        temporalAabbMax .put(temporalAabbMaxx, temporalAabbMaxy, temporalAabbMaxz)

        temporalAabbMin -= angularMotion3d
        temporalAabbMax += angularMotion3d
    }
}