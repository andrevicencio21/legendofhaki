package com.haki.loh.handlers;

public class MyInput {
	public static boolean[] keys;
	public static boolean[] pkeys;

	public static final int NUM_KEYS = 20;
	public static final int ENTER = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;
	public static final int LEFT = 4;
	public static final int RIGHT = 5;
	public static final int JUMP = 6;
	public static final int FIRE = 7;
	public static final int ESCAPE = 8;
	public static final int ATTACK = 9;
	public static final int num1 = 10;
	public static final int num2 = 11;
	public static final int num3 = 12;
	public static final int num4 = 13;
	
	

	static {
		keys = new boolean[NUM_KEYS];
		pkeys = new boolean[NUM_KEYS];
	}

	public static void update() {
		for (int i = 0; i < NUM_KEYS; i++) {
			pkeys[i] = keys[i];
		}
	}

	public static void setKey(int i, boolean b) {
		keys[i] = b;
	}

	public static boolean isDown(int i) {
		return keys[i];
	}

	public static boolean isPressed(int i) {
		return keys[i] && !pkeys[i];
	}

}
