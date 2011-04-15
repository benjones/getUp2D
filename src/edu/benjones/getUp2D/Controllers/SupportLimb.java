package edu.benjones.getUp2D.Controllers;

import java.util.HashMap;

import org.jbox2d.collision.ContactID;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.contacts.ContactResult;

import edu.benjones.getUp2D.Character.Limb;
import edu.benjones.getUp2D.GetUpScenario;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;

public class SupportLimb {

	protected Limb limb;
	protected limbStatus lastStatus;

	protected double plantedLevel;

	public SupportLimb(Limb limb) {
		this.limb = limb;
		this.lastStatus = limbStatus.idle;
	}

	public void draw(DebugDraw g) {

	}

	public boolean canSupport() {
		return plantedLevel > 0f;
	}

	public boolean canRemoveSupport() {
		return false;
	}

	public void setPose(supportInfo info, float[] desiredPose) {

	}

	// this looks TERRIBLE. I THINK, that all the loops will be 1-2 elements max
	public void updateContactInfo(float dt) {
		float force = 0f;
		HashMap<ContactID, ContactResult> contactMap = GetUpScenario
				.getContactMap();
		for (Body b : limb.getBodies()) {
			for (ContactEdge e = b.getContactList(); e != null; e = e.next) {
				Contact c = e.contact;
				for (Manifold m : c.getManifolds()) {
					for (int i = 0; i < m.pointCount; ++i) {
						force += Math
								.abs(contactMap.get(m.points[i].id).normalImpulse);
					}
				}
			}

		}
	}
}
