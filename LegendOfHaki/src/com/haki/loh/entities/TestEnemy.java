package com.haki.loh.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.haki.loh.gametates.GameState;

public class TestEnemy extends Entity {
	private boolean isRight, isLeft;
	private long timeRight = 0, timeLeft = 0;
	private long directionTime = 1500;

	public TestEnemy(GameState state) {
		super(state);
		world = state.getWorld();
		loadAssets();
		createBody();
	}

	@Override
	public void update(float dt) {
		x = body.getPosition().x;
		y = body.getPosition().y;
		linearVelocityX = body.getLinearVelocity().x;
		linearVelocityY = body.getLinearVelocity().y;
		if (isRight && timeRight == 0) {
			timeRight = System.currentTimeMillis();
		}
		if (isRight
				&& (System.currentTimeMillis() - timeRight) >= directionTime) {
			System.out.println("change direction");
			timeRight = 0;
			isRight = false;
			isLeft = true;
		}
		if (isLeft && timeLeft == 0) {
			timeLeft = System.currentTimeMillis();
		}
		if (isLeft && (System.currentTimeMillis() - timeLeft) >= directionTime) {
			timeLeft = 0;
			isLeft = false;
			isRight = true;

		}
		if (isRight) {

			if (linearVelocityX <= 0.5f) {
				body.applyLinearImpulse(0.1f, 0, 0, 0, true);
				System.out.println("right");
			}
		}
		if (isLeft) {
			if (linearVelocityX >= -0.5f) {
				body.applyLinearImpulse(-0.1f, 0, 0, 0, true);
				System.out.println("left");
			}
		}

	}

	public void createBody() {

		BodyDef bdef = new BodyDef();
		bdef.position.set(150 / PPM, 200 / PPM);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(8 / PPM, 14 / PPM);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		body.createFixture(fdef).setUserData(body);
		isRight = true;
		shape.dispose();

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	public void loadAssets() {
	}

}
