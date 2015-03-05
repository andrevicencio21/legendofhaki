package com.haki.loh.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.haki.loh.gametates.GameState;
import com.haki.loh.handlers.B2DVariables;

public abstract class Entity {
	protected GameState state;
	protected Body body;
	protected World world;
	protected final float PPM = B2DVariables.PPM;
	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	protected float x, y, linearVelocityX, linearVelocityY;
	protected TextureAtlas textureAtlas;
	protected boolean removable = false;
	

	public boolean isRemovable() {
		return removable;
	}

	public void setRemovable(boolean removable) {
		this.removable = removable;
	}

	public Entity(GameState state) {
		this.state = state;
	}

	public abstract void update(float dt);

	public abstract void render();

	public Body getBody() {
		return body;
	}
	public TextureAtlas getTextureAtlas(){
		return textureAtlas;
	}
}
