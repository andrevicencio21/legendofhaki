package com.haki.loh.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.haki.loh.handlers.GameStateManager;
import com.haki.loh.handlers.MyInputProcessor;

public class Game implements ApplicationListener {
	public static final String title = "Legend of Haki";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int SCALE = 2;
	public static final float STEP = 1 / 60f;

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private OrthographicCamera hudCamera;
	private GameStateManager gsm;

	public void create() {
		Gdx.input.setInputProcessor(new MyInputProcessor());
		batch = new SpriteBatch();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, V_WIDTH, V_HEIGHT);
		gsm = new GameStateManager(this);

	}

	public void render() {
		gsm.update(STEP);
		gsm.render();

	}

	public void dispose() {
	}

	public void resize(int w, int h) {
	}

	public void pause() {
	}

	public void resume() {
	}

	public SpriteBatch getSpiteBatch() {
		return batch;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public OrthographicCamera getHudCamera() {
		return hudCamera;
	}

}
