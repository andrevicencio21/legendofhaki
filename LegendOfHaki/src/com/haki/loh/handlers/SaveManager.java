package com.haki.loh.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.haki.loh.entities.Tanuki;
import com.haki.loh.gametates.Play;

public class SaveManager {
	public Tanuki tanuki;
	public Play play;
	public SaveFile saveFile;

	public SaveManager(Tanuki tanuki) {
		this.tanuki = tanuki;
		saveFile = new SaveFile();
	}

	public static class SaveFile {
		public float startingX, startingY;

		public void setXY(SaveManager saveManager) {
			startingX = saveManager.tanuki.getBody().getPosition().x;
			startingY = saveManager.tanuki.getBody().getPosition().y;
		}

	}

	public void save() {
		saveFile.setXY(this);
		Json json = new Json();
		
		FileHandle handle = Gdx.files.local("bin/savefile.json");
		handle.writeString(json.prettyPrint(saveFile), false);
		//System.out.println(json.toJson(saveFile));

	}
	public void load(){
		Json json = new Json();
		FileHandle handle = Gdx.files.local("bin/savefile.json");
		saveFile = json.fromJson(SaveFile.class, handle);
		//System.out.println("Load Complete");
	}

}
