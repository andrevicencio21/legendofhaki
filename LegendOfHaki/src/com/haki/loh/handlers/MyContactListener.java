package com.haki.loh.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.haki.loh.entities.Entity;
import com.haki.loh.entities.Kunai;

public class MyContactListener implements ContactListener {
	public int numFootContacts;
	private Array<Entity> bodiesToRemove;
	private Kunai kunaiHitWall;

	public MyContactListener() {
		bodiesToRemove = new Array<Entity>();
	}

	public void beginContact(Contact c) {
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();
		if (fa == null || fb == null)
			return;
		if (fa.getUserData() != null && fa.getUserData().equals("playerFoot")
				&& fb.getUserData().equals("wall")) {
			numFootContacts++;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("playerFoot")) {

			numFootContacts++;
		}
		if (fa.getUserData() != null && fa.getUserData().equals("kunai")
				&& fb.getUserData().equals("wall")) {
			kunaiHitWall = (Kunai) fb.getBody().getUserData();
			kunaiHitWall.setWallHitTime(System.currentTimeMillis());
			bodiesToRemove.add((Entity) fa.getBody().getUserData());
		}
		if (fb.getUserData() != null && fb.getUserData().equals("kunai")
				&& fa.getUserData().equals("wall")) {

			kunaiHitWall = (Kunai) fb.getBody().getUserData();
			kunaiHitWall.setWallHitTime(System.currentTimeMillis());
			bodiesToRemove.add((Entity) fb.getBody().getUserData());

		}
	}

	@Override
	public void endContact(Contact c) {
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();
		if (fa == null || fb == null)
			return;

		if (fa.getUserData() != null && fa.getUserData().equals("playerFoot")
				&& fb.getUserData().equals("wall")) {
			numFootContacts--;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("playerFoot")) {
			numFootContacts--;
		}

	}

	public Array<Entity> getBodiesToRemove() {
		return bodiesToRemove;
	}

	@Override
	public void postSolve(Contact c, ContactImpulse ci) {

	}

	@Override
	public void preSolve(Contact c, Manifold m) {

	}

}
