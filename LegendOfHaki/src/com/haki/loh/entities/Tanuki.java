package com.haki.loh.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.TimeUtils;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.LibGdxDrawer;
import com.brashmonkey.spriter.LibGdxLoader;
import com.brashmonkey.spriter.Mainline.Key;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Player.PlayerListener;
import com.brashmonkey.spriter.PlayerTweener;
import com.brashmonkey.spriter.SCMLReader;
import com.haki.loh.gametates.Play;
import com.haki.loh.handlers.MyInput;
import com.haki.loh.handlers.SaveManager;

public class Tanuki extends Entity {
	private LibGdxLoader loader;

	// this PlayerListener listens to the Spriter Class animation.
	// Documentation found
	// from Trixor's gitHub
	private PlayerListener listener;

	// There booleans determine whether the player States and it decides what
	// animation
	// should be played or what can or cannot happen
	private boolean busy, directionPressed, leftPressed, rightPressed,
			jumpApplied = false, jumpPressed, run, idle, jump, falling,
			attack1, attackRight;

	// Timers

	private long attack1Time = 0;
	private long attack1Delay = 300;
	private long landingDelay = 30;
	private long jumpPressTime = 0;
	private long rightPressTime = 0, leftPressTime = 0;

	private float startingX, startingY;
	private LibGdxDrawer drawer;
	private Animation prepared;
	// Interpolation variables
	PlayerTweener characters;

	public Tanuki(Play play) {
		super(play);
		batch = play.getBatch();
		world = play.getWorld();
		drawer = play.getDrawer();
		FileHandle handle = Gdx.files.local("bin/savefile.json");
		if (handle.exists()) {
			SaveManager sm = new SaveManager(this);
			sm.load();
			startingX = sm.saveFile.startingX;
			startingY = sm.saveFile.startingY;
		} else {
			System.out.println("doesnt exist");
			startingX = 100 / PPM;
			startingY = 120 / PPM;
		}
		loadAssets();
		createBody();
		forward = true;
		changedDirection = false;

	}

	private void loadAssets() {
		FileHandle handle = Gdx.files.internal("images/haki/Haki.scml");
		Data data = new SCMLReader(handle.read()).getData();
		loader = new LibGdxLoader(data);
		loader.load(handle.file());
		characters = new PlayerTweener(data.getEntity("Tanuki"));
		characters.setScale(0.3f);
		setListener();
		characters.addListener(listener);
		prepared = data.getEntity("Tanuki").getAnimation("jumpLeft");
		prepared.prepare();
	}

	private void setListener() {
		listener = new PlayerListener() {

			@Override
			public void animationFinished(Animation animation) {

			}

			@Override
			public void animationChanged(Animation oldAnim, Animation newAnim) {
			}

			@Override
			public void preProcess(Player player) {

			}

			@Override
			public void postProcess(Player player) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mainlineKeyChanged(Key prevKey, Key newKey) {
				// TODO Auto-generated method stub

			}
		};
	}

	@Override
	// Creates box2D body
	public void createBody() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(startingX, startingY);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);
		PolygonShape shape = new PolygonShape();

		// Measurements are always 1/2 of the side. so if shape is set as 5,5
		// the actulay diameter is 10, 10
		shape.setAsBox(8 / PPM, 17 / PPM);

		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		body.createFixture(fdef);

		// UserData of the Body is the entity that owns it
		body.setUserData(this);

		// create foot
		shape.setAsBox(7f / PPM, 4 / PPM, new Vector2(0, -17 / PPM), 0);
		fdef.shape = shape;

		// isSensor Determines whether the fixture is a "ghost fixture" it can
		// detect
		// collision but cannot physically collide
		fdef.isSensor = true;

		body.createFixture(fdef).setUserData("playerFoot");
	}

	// This method checks the conditions of the player based on certain
	// varaibles and other conditions
	// then determines the correct state of the player.
	public void setStates() {
		if (grounded) {
			if (jumpPressTime != 0) {
				if (!jumpApplied) {
					body.applyForceToCenter(0f, 250f, true);
					jumpApplied = true;
				}
			}
		} else if (!grounded) {
			if (jumpApplied) {
				jump = true;
			}
		}
		// check isGrounded
		if (play.getMyContactListener().numFootContacts > 0) {
			grounded = true;
		} else if (play.getMyContactListener().numFootContacts == 0) {
			grounded = false;
		}
		// Check Attack States
		if (attack1Time != 0) {
			attack1 = true;
		}
		if ((TimeUtils.millis() - attack1Time) >= attack1Delay) {
			attack1Time = 0;
			attack1 = false;
		}

		// check if Busy
		if (attack1)
			busy = true;
		else
			busy = false;

		// Check isForward
		if (directionPressed) {
			if (rightPressTime > leftPressTime) {
				forward = true;
			}
			if (rightPressTime < leftPressTime) {
				forward = false;
			}
		}
		// // Check Fallimg Jumping
		// if (!isGrounded) {
		// if (linearVelocityY > 0) {
		// jump = true;
		// falling = false;
		// } else if (linearVelocityY < 0) {
		// jump = false;
		// falling = true;
		// }
		// }

		// check Idle States
		if (!directionPressed && grounded) {
			idle = true;
		} else
			idle = false;
		// Check Run States
		if (directionPressed && grounded)
			run = true;
		else
			run = false;

	}

	// Set the Spriter animation based on the character's current state,
	// setAnimation(String) the String is the animation name on the Spriter SCML
	// File
	public void setAnimationDetails(int firstSpeed, int secondSpeed,
			String baseBoneName, String baseAnimation, String firstAnimation,
			String secondAnimation, float weight) {
		characters.getFirstPlayer().speed = firstSpeed;
		characters.getSecondPlayer().speed = secondSpeed;
		characters.baseBoneName = baseBoneName;
		characters.setBaseAnimation(baseAnimation);
		characters.getFirstPlayer().setAnimation(firstAnimation);
		characters.getSecondPlayer().setAnimation(secondAnimation);
		characters.setWeight(weight);

	}

	public void setAnimation() {
		if (run && !attack1 && forward) {
			setAnimationDetails(18, 18, null, "run", "run", "run", 0.50f);
		}
		if (run && !attack1 && !forward) {
			setAnimationDetails(18, 18, null, "runLeft", "runLeft", "runLeft",
					0.50f);
		}
		if (idle && !attack1 && forward) {
			setAnimationDetails(15, 15, null, "idle", "idle", "idle", 0.50f);
		}
		if (idle && !attack1 && !forward) {
			setAnimationDetails(15, 15, null, "idleLeft", "idleLeft",
					"idleLeft", 0.50f);
		}
		if (jump && forward) {
			setAnimationDetails(15, 15, "swordbone", "jump", "jump", "jump", 0f);
		}
		if (jump && !forward) {
			setAnimationDetails(15, 15, "swordbone", "jump", "jumpLeft",
					"jump", 0f);

		}
		if (attack1 && run) {
			setAnimationDetails(15, 15, "torsobone", "run", "runAttack", "run",
					0f);
		}
		if (attack1 && !run) {
			setAnimationDetails(15, 0, "collarbone", "run", "runAttack", "run",
					0f);
		}
		if (forward) {
			if (characters.flippedX() != 1)
				characters.flipX();
		} else {
			if (characters.flippedX() == 1)
				characters.flipX();
		}
		characters.setPosition(x * PPM, y * PPM - 17);
		characters.update();

	}

	@Override
	public void update(float dt) {
		super.update(dt);

		handleInput();
		setStates();
		setAnimation();

	}

	public void handleInput() {
		if (MyInput.isDown(MyInput.RIGHT)) {
			if (!attack1 || (attack1 && attackRight)) {
				if (body.getLinearVelocity().x <= 2.3f)
					body.applyLinearImpulse(0.2f, 0, 0, 0, true);
			}
			rightPressTime = TimeUtils.millis();
			directionPressed = true;
		}
		rightPressed = false;
		if (MyInput.isDown(MyInput.LEFT)) {
			if (!attack1 || (attack1 && !attackRight)) {
				if (body.getLinearVelocity().x >= -2.3f)
					body.applyLinearImpulse(-0.2f, 0, 0, 0, true);
			}
			leftPressTime = TimeUtils.millis();
			directionPressed = true;
		}
		if (!MyInput.isDown(MyInput.RIGHT) && !MyInput.isDown(MyInput.LEFT)) {
			directionPressed = false;
			leftPressed = false;
			rightPressed = false;
		}

		if (MyInput.isPressed(MyInput.JUMP)) {
			jumpPressed = true;
			jumpPressTime = TimeUtils.millis();
		} else if (!MyInput.isDown(MyInput.JUMP)) {
			jumpPressed = false;
		}
		if (MyInput.isPressed(MyInput.FIRE) && grounded) {
			SaveManager sm = new SaveManager(this);
			sm.save();
			if (attack1Time == 0) {
				attack1Time = TimeUtils.millis();
				if (forward)
					attackRight = true;
				else
					attackRight = false;
			}
		}
	}

	@Override
	public void render() {
		drawer.draw(characters);

		// DRAW BONES
		// drawer.drawBones(characters);

	}
}
