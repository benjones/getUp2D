package edu.benjones.getUp2D.test;

import static org.junit.Assert.*;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.junit.Before;
import org.junit.Test;

import edu.benjones.getUp2D.Utils.PhysicsUtils;


public class PhysicsUtilsTest {

	protected World world;
	@Before
	public void setupWorld(){
		AABB worldAABB = new AABB();
		worldAABB.lowerBound.set(-100, -100f);
		worldAABB.upperBound.set(100,100f);
		world = new World(worldAABB, new Vec2(0f,0f), false);
	}
	
	@Test
	public void getChildJointTest(){
		Body b1, b2, b3;
		RevoluteJoint j1, j2;
		BodyDef bd = new BodyDef();
		b1 = world.createBody(bd);
		b2 = world.createBody(bd);
		b3 = world.createBody(bd);
		
		RevoluteJointDef jd = new RevoluteJointDef();
		jd.initialize(b1, b2, new Vec2(0f,0f));
		j1 = (RevoluteJoint)world.createJoint(jd);
		
		jd.initialize(b2, b3, new Vec2(0f,0f));
		j2 = (RevoluteJoint)world.createJoint(jd);
	
		assertEquals(PhysicsUtils.getChildJoint(j1), j2);
		assertEquals(PhysicsUtils.getChildJoint(j2), null);
	}
}
