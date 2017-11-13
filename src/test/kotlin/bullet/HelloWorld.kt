/*
Bullet Continuous Collision Detection and Physics Library
Copyright (c) 2003-2007 Erwin Coumans  http://continuousphysics.com/Bullet/

This software is provided 'as-is', without any express or implied warranty.
In no event will the authors be held liable for any damages arising from the use of this software.
Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it freely,
subject to the following restrictions:

1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not required.
2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
3. This notice may not be removed or altered from any source distribution.
*/

package bullet

import bullet.collision.broadphaseCollision.DbvtBroadphase
import bullet.collision.collisionDispatch.CollisionDispatcher
import bullet.collision.collisionDispatch.DefaultCollisionConfiguration
import bullet.dynamics.constraintSolver.SequentialImpulseConstraintSolver
import io.kotlintest.specs.StringSpec

/** This is a Hello World program for running a basic Bullet physics simulation */
class HelloWorld : StringSpec() {

    // -----initialization_start-----

    // Collision configuration contains default setup for memory, collision setup. Advanced users can create their own configuration.
    val collisionConfiguration = DefaultCollisionConfiguration()

    // Use the default collision dispatcher. For parallel processing you can use a diffent dispatcher (see Extras/BulletMultiThreaded)
    val dispatcher = CollisionDispatcher(collisionConfiguration)

    // DbvtBroadphase is a good general purpose broadphase. You can also try out btAxis3Sweep.
    val overlappingPairCache = DbvtBroadphase()

    // The default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
    val solver = SequentialImpulseConstraintSolver()

    val dynamicsWorld = DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
}
