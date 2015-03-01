package com.haki.loh.handlers;

import java.util.Stack;

import com.haki.loh.gametates.GameState;
import com.haki.loh.gametates.Menu;
import com.haki.loh.gametates.Play;
import com.haki.loh.main.Game;

public class GameStateManager {
	protected Game game;
	private Stack<GameState> gameStates;
	public static final int PLAY = 107;
	public static final int MENU = 101;

	public GameStateManager(Game game) {
		this.game = game;
		gameStates = new Stack<GameState>();
		pushState(PLAY);
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public void update(float dt) {

		gameStates.peek().update(dt);
	}

	public void render() {
		gameStates.peek().render();
	}

	private GameState getGameState(int state) {
		if (state == MENU)
			return new Menu(this);
		if (state == PLAY)
			return new Play(this);
		return null;

	}

	public void setState(int state) {
		popState();
		pushState(state);
	}

	public void pushState(int state) {
		gameStates.push(getGameState(state));
	}

	public void popState() {
		GameState g = gameStates.pop();
		g.dispose();
	}

}
