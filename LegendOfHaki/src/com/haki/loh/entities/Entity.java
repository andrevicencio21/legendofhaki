package com.haki.loh.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.haki.loh.gametates.GameState;
import com.haki.loh.gametates.Play;
import com.haki.loh.handlers.B2DVariables;

public abstract class Entity {
	protected Play play;
	protected Body body;
	protected World world;
	protected final float PPM = B2DVariables.PPM;
	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	protected float x, y, linearVelocityX, linearVelocityY;
	protected TextureAtlas textureAtlas;
	protected boolean removable = false;
	protected boolean isGrounded, isForward, changedDirection;
	

	public boolean isGrounded() {
		return isGrounded;
	}

	public void setGrounded(boolean isGrounded) {
		this.isGrounded = isGrounded;
	}

	public boolean isRemovable() {
		return removable;
	}

	public void setRemovable(boolean removable) {
		this.removable = removable;
	}

	public Entity(Play play) {
		this.play = play;
	}

	public abstract void createBody();

	public void update(float dt) {
		if (body != null) {
			x = body.getPosition().x;
			y = body.getPosition().y;
			linearVelocityX = body.getLinearVelocity().x;
			linearVelocityY = body.getLinearVelocity().y;
		}
	}

	public abstract void render();

	public Body getBody() {
		return body;
	}

	public TextureAtlas getTextureAtlas() {
		return textureAtlas;
	}
}
