package com.me.mygdxgame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.haki.loh.main.Game;

public class Main {

	private static boolean windowFullScreen = false;

	public static void main(String[] args) {

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		if (windowFullScreen) {
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			cfg.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
			cfg.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
			cfg.fullscreen = false;
		}
		else{
			cfg.width = Game.V_WIDTH * Game.SCALE;
			cfg.height = Game.V_HEIGHT * Game.SCALE;
			cfg.fullscreen = false;
		}

		new LwjglApplication(new Game(), cfg);
	}
}
