package bullet.collision

import bullet.f
import bullet.linearMath.Vec3
import bullet.linearMath.times
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.math.abs
import bullet.collision.Collision.SphereSphereTestMethod as SSTM

class Collision : StringSpec() {

    enum class SphereSphereTestMethod { ANALYTIC, GJKEPA, GJKEPA_RADIUS_NOT_FULL_MARGIN, GJKMPR }

    init {

    }

    fun testSphereSphereDistance(method: SSTM, absError: Float) {

        run {
            val ssd = SphereSphereCollisionDescription()
            ssd.sphereTransformA.setIdentity()
            ssd.sphereTransformB.setIdentity()
            val distInfo = DistanceInfo()
            val result = computeSphereSphereCollision(ssd, distInfo)
            result shouldBe 0
            distInfo.distance shouldBe 0f
        }

        for (rb in 1..4)
            for (z in -5..4) {
                for (j in 1..4) {
                    for (i in -5..4) {
                        if (i != z) {   //skip co-centric spheres for now (todo(erwincoumans) fix this)
                            val ssd = SphereSphereCollisionDescription().apply {
                                sphereTransformA.setIdentity()
                                sphereTransformA.origin = Vec3(0f, i.f, 0f)
                                sphereTransformB.setIdentity()
                                sphereTransformB.origin = Vec3(0f, z.f, 0f)
                                radiusA = j.f
                                radiusB = rb.f * 0.1f
                            }
                            val distInfo = DistanceInfo()
                            var result = -1
                            result = when (method) {
                                SSTM.ANALYTIC -> computeSphereSphereCollision(ssd, distInfo)
                                SSTM.GJKMPR, SSTM.GJKEPA, SSTM.GJKEPA_RADIUS_NOT_FULL_MARGIN -> computeGjkEpaSphereSphereCollision(ssd, distInfo, method)
                                else -> throw Error()
                            }
                            //  int result = btComputeSphereSphereCollision(ssd,&distInfo);
                            result shouldBe 0
                            val distance = abs((i - z).f) - j.f - ssd.radiusB
                            assert(distInfo.distance in distance - absError..distance + absError) // TODO plusOrMinus kotlinTest
                            val computedA = distInfo.pointOnB + distInfo.distance * distInfo.normalBtoA
                            val v = distInfo.pointOnA
                            assert(computedA.x in v.x - absError..v.x + absError)
                            assert(computedA.y in v.y - absError..v.y + absError)
                            assert(computedA.z in v.z - absError..v.z + absError)
                        }
                    }
                }
            }
    }
}