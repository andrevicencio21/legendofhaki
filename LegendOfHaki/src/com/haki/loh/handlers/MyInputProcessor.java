package com.haki.loh.handlers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter{
	public boolean keyDown(int k){
		if(k == Keys.ENTER){
			MyInput.setKey(MyInput.ENTER, true);
		}
		if(k == Keys.SPACE){
			MyInput.setKey(MyInput.JUMP, true);
		}
		if(k == Keys.A){
			MyInput.setKey(MyInput.LEFT, true);
		}
		if(k == Keys.D){
			MyInput.setKey(MyInput.RIGHT, true);
		}
		if(k == Keys.F){
			MyInput.setKey(MyInput.FIRE, true);
		}
		
		return true;
	}
	public boolean keyUp(int k){
		if(k == Keys.ENTER){
			MyInput.setKey(MyInput.ENTER, false);
		}
		if(k == Keys.SPACE){
			MyInput.setKey(MyInput.JUMP, false);
		}
		if(k == Keys.A){
			MyInput.setKey(MyInput.LEFT, false);
		}
		if(k == Keys.D){
			MyInput.setKey(MyInput.RIGHT, false);
		}
		if(k == Keys.F){
			MyInput.setKey(MyInput.FIRE, false);
		}
		return true;
	}

}
