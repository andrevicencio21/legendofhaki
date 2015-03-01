package com.haki.loh.gametates;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.haki.loh.handlers.GameStateManager;
import com.haki.loh.handlers.MyInput;

public class Menu extends GameState {
	private BitmapFont font;

	public Menu(GameStateManager gsm) {
		super(gsm);
		font = new BitmapFont();
	}

	@Override
	public void handleInput() {
		if(MyInput.isPressed(MyInput.ENTER)){
			gsm.setState(GameStateManager.PLAY);
		}
		if(MyInput.isDown(MyInput.ENTER)){
			System.out.println("ENTER is down");
		}
	}

	@Override
	public void update(float dt) {
		handleInput();
	}

	@Override
	public void render() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		font.draw(batch, "MENU STATE", 100, 100);
		batch.end();
	}

	@Override
	public void dispose() {

	}

}
