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
		if(k == Keys.ESCAPE){
			MyInput.setKey(MyInput.ESCAPE, true);
		}
		if(k == Keys.H){
			MyInput.setKey(MyInput.ATTACK, true);
		}
		if(k == Keys.NUM_1){
			MyInput.setKey(MyInput.num1, true);
		}
		if(k == Keys.NUM_2){
			MyInput.setKey(MyInput.num2, true);
		}
		if(k == Keys.NUM_3){
			MyInput.setKey(MyInput.num3, true);
		}
		if(k == Keys.NUM_4){
			MyInput.setKey(MyInput.num4, true);
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
		if(k == Keys.ESCAPE){
			MyInput.setKey(MyInput.ESCAPE, false);
		}
		if(k == Keys.H){
			MyInput.setKey(MyInput.ATTACK, false);
		}
		if(k == Keys.NUM_1){
			MyInput.setKey(MyInput.num1, false);
		}
		if(k == Keys.NUM_2){
			MyInput.setKey(MyInput.num2, false);
		}
		if(k == Keys.NUM_3){
			MyInput.setKey(MyInput.num3, false);
		}
		if(k == Keys.NUM_4){
			MyInput.setKey(MyInput.num4, false);
		}
		return true;
	}

}
