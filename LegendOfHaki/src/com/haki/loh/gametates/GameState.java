package com.haki.loh.gametates;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.haki.loh.entities.Entity;
import com.haki.loh.handlers.GameStateManager;
import com.haki.loh.handlers.MyContactListener;
import com.haki.loh.main.Game;

public abstract class GameState {
	protected GameStateManager gsm;
	protected Game game;
	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	protected OrthographicCamera hudCamera;
	protected World world;
	protected MyContactListener myContactListener;
	protected Array<Entity> entityArray;
	protected PolygonShape kunaiShape;

	protected GameState(GameStateManager gsm) {
		this.gsm = gsm;
		game = gsm.getGame();
		batch = game.getSpiteBatch();
		camera = game.getCamera();
		hudCamera = game.getHudCamera();

	}

	public abstract void handleInput();

	public abstract void update(float dt);

	public abstract void render();

	public abstract void dispose();

	public World getWorld() {
		return world;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public MyContactListener getMyContactListener() {
		return myContactListener;
	}
	public Array<Entity> getEntityArray(){
		return entityArray;
	}
	public PolygonShape getKunaiShape(){
		return kunaiShape;
	}

}
