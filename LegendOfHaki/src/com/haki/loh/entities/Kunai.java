package com.haki.loh.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.haki.loh.gametates.Play;
import com.haki.loh.handlers.BodyEditorLoader;

public class Kunai extends Entity {
	private Entity entityCaller;
	private Sprite kunaiSprite;
	private boolean isForward;
	private long kunaiDelay = 0;
	private long wallHitDuration = 0;
	private long wallHitTime = 0;

	private long timeCalled;
	private BodyEditorLoader loader;
	private FixtureDef fdef;

	public Kunai(Play play) {
		super(play);
		world = play.getWorld();

	}

	public Kunai(Play play, Entity entityCaller, boolean isForward) {
		super(play);
		timeCalled = System.currentTimeMillis();
		this.entityCaller = entityCaller;
		this.batch = play.getBatch();
		this.isForward = isForward;
		// loadAssets();
		loader = new BodyEditorLoader(
				Gdx.files.internal("bodyloader/kunai.json"));
		world = play.getWorld();

	}

	public void createBody() {
		BodyDef bdef = new BodyDef();

		if (System.currentTimeMillis() - timeCalled >= kunaiDelay) {
			if (isForward) {
				bdef.position
						.set(entityCaller.getBody().getPosition().x + 15 / PPM,
								entityCaller.getBody().getPosition().y + 5
										/ PPM);
			}
			if (!isForward) {
				bdef.position
						.set(entityCaller.getBody().getPosition().x - 3 / PPM,
								entityCaller.getBody().getPosition().y + 5
										/ PPM);
			}
			bdef.type = BodyType.DynamicBody;
			bdef.bullet = true;

			PolygonShape shape = new PolygonShape();
			shape.setAsBox(8 / PPM, 2 / PPM);
			fdef = new FixtureDef();
			fdef.density = 1;
			fdef.friction = 0.5f;
			fdef.restitution = 0.3f;
			body = world.createBody(bdef);
			body.setGravityScale(0f);
			body.setUserData(this);
			loader.attachFixture(body, "kunai", fdef, 1f);
			body.getFixtureList().get(0).setUserData("kunai");

			// body.setGravityScale(0.f);
			if (!isForward)
				body.applyForceToCenter(new Vector2(-3f, 0f), true);
			else if (isForward)
				body.applyForceToCenter(new Vector2(3f, 0f), true);
		}

	}

	@Override
	public void update(float dt) {
		if (wallHitTime != 0) {
			if ((System.currentTimeMillis() - wallHitTime) >= wallHitDuration)
				setRemovable(true);
			else
				setRemovable(false);
		}
		if (body == null)
			createBody();
		if (body != null) {
			x = body.getPosition().x;
			y = body.getPosition().y;
			linearVelocityX = body.getLinearVelocity().x;
			linearVelocityY = body.getLinearVelocity().y;

			// kunaiSprite.setOrigin(kunaiSprite.getWidth() / 2,
			// kunaiSprite.getHeight() / 2);
			// kunaiSprite.setRotation(MathUtils.radiansToDegrees
			// * body.getAngle());
			// kunaiSprite.setPosition(x * PPM - kunaiSprite.getRegionWidth() /
			// 2,
			// y * PPM - kunaiSprite.getRegionHeight() / 2);
		}

	}

	@Override
	public void render() {
		// if (body != null) {
		// kunaiSprite.draw(batch);
		// }
	}

	public void loadAssets() {
		this.textureAtlas = entityCaller.getTextureAtlas();
		kunaiSprite = textureAtlas.createSprite("kunai");
		kunaiSprite.setScale(0.6f, 0.6f);

		if (!isForward)
			kunaiSprite.setFlip(true, false);
	}

	public long getWallHitTime() {
		return wallHitTime;
	}

	public void setWallHitTime(long wallHitTime) {
		this.wallHitTime = wallHitTime;
	}

}
