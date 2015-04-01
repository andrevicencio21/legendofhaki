package com.haki.loh.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.LibGdxDrawer;
import com.brashmonkey.spriter.LibGdxLoader;
import com.brashmonkey.spriter.Mainline.Key;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Player.PlayerListener;
import com.brashmonkey.spriter.PlayerTweener;
import com.brashmonkey.spriter.SCMLReader;
import com.brashmonkey.spriter.Spriter;
import com.haki.loh.gametates.Play;
import com.haki.loh.handlers.MyInput;
import com.haki.loh.handlers.SaveManager;

public class Tanuki extends Entity {
	private Player character;
	private LibGdxLoader loader;

	// this PlayerListener listens to the Spriter Class animation.
	// Documentation found
	// from Trixor's gitHub
	private PlayerListener listener;

	// There booleans determine whether the player States and it decides what
	// animation
	// should be played or what can or cannot happen
	private boolean isBusy, isDirectionPressed, isRun, isIdle, isJumping,
			isFalling, isAttack1, isAttack2, isAttack3;

	// Attack Timers
	private long attack1Time = 0, attack2Time = 0, attack3Time = 0;

	// Attack Delays
	private long attack1Delay = 500, attack2Delay = 500, attack3Delay = 500;
	private float startingX, startingY;
	private LibGdxDrawer drawer;

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
		isForward = true;
	}

	private void loadAssets() {
		FileHandle handle = Gdx.files.internal("images/haki/Haki.scml");
		Data data = new SCMLReader(handle.read()).getData();
		loader = new LibGdxLoader(data);
		loader.load(handle.file());
		characters = new PlayerTweener(data.getEntity(0));


		character = Spriter.newPlayer("images/haki/Haki.scml", "Haki");
		character.setScale(0.3f);
		setListener();
		character.addListener(listener);
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
		// check isGrounded
		if (play.getMyContactListener().numFootContacts > 0) {
			isGrounded = true;
		} else if (play.getMyContactListener().numFootContacts == 0) {
			isGrounded = false;
		}
		// Check Attack States
		if (attack1Time != 0) {
			isAttack1 = true;
		}
		if ((System.currentTimeMillis() - attack1Time) >= attack1Delay) {
			attack1Time = 0;
			isAttack1 = false;
		}
		if (attack2Time != 0) {
			isAttack2 = true;
		}
		if ((System.currentTimeMillis() - attack2Time) >= attack2Delay) {
			attack2Time = 0;
			isAttack2 = false;
		}
		if (attack3Time != 0) {
			isAttack3 = true;
		}
		if ((System.currentTimeMillis() - attack3Time) >= attack3Delay) {
			attack3Time = 0;
			isAttack3 = false;
		}
		// check if Busy
		if (isAttack1 || isAttack2 || isAttack3)
			isBusy = true;
		else
			isBusy = false;

		// Check isForward
		if (isDirectionPressed) {
			if (linearVelocityX > 0)
				isForward = true;
			else if (linearVelocityX < 0)
				isForward = false;
		}
		// Check Fallimg Jumping
		if (!isGrounded) {
			if (linearVelocityY > 0) {
				isJumping = true;
				isFalling = false;
			} else if (linearVelocityY < 0) {
				isJumping = false;
				isFalling = true;
			}
		} else {
			isJumping = false;
			isFalling = false;
		}
		// check Idle States
		if (!isDirectionPressed && isGrounded) {
			isIdle = true;
		} else
			isIdle = false;
		// Check Run States
		if (isDirectionPressed && isGrounded)
			isRun = true;
		else
			isRun = false;

	}

	// Set the Spriter animation based on the character's current state,
	// setAnimation(String) the String is the animation name on the Spriter SCML
	// File
	public void setAnimation() {
		if (isRun && !isAttack1 && !isAttack2 && !isAttack3) {
			character.setAnimation("run");
			character.speed = 15;

		}
		if (isIdle && !isAttack1 && !isAttack2 && !isAttack3) {
			character.setAnimation("idle");
			character.speed = 15;
		}
		if (isJumping) {
			character.setAnimation("jump1");
			character.speed = 15;
		}
		if (isFalling) {
			character.setAnimation("falling");
			character.speed = 15;
		}
		if (isAttack1) {
			character.setAnimation("runAttack1");
			character.speed = 10;
		}
		if (isAttack2) {
			character.setAnimation("attack2");
			character.speed = 30;
		}
		if (isAttack3) {
			character.setAnimation("attack3");
			character.speed = 30;
		}
		if (isForward) {
			if (character.flippedX() != 1)
				character.flipX();
		} else {
			if (character.flippedX() == 1)
				character.flipX();
		}
		character.setPosition(x * PPM, y * PPM - 17);
		character.update();

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
			if (body.getLinearVelocity().x <= 2.3f)
				body.applyLinearImpulse(0.2f, 0, 0, 0, true);
			isDirectionPressed = true;
		}
		if (MyInput.isDown(MyInput.LEFT)) {
			if (body.getLinearVelocity().x >= -2.3f)
				body.applyLinearImpulse(-0.2f, 0, 0, 0, true);
			isDirectionPressed = true;
		}
		if (!MyInput.isDown(MyInput.RIGHT) && !MyInput.isDown(MyInput.LEFT)) {
			isDirectionPressed = false;
		}
		if (MyInput.isPressed(MyInput.JUMP)) {
			body.applyForceToCenter(0f, 250f, true);
		}
		if (MyInput.isPressed(MyInput.FIRE) && isGrounded) {
			SaveManager sm = new SaveManager(this);
			sm.save();
			if (attack1Time == 0 && attack2Time == 0 && attack3Time == 0) {
				attack1Time = System.currentTimeMillis();
			}
			if ((System.currentTimeMillis() - attack1Time) >= 230
					&& attack1Time != 0) {
				attack1Time = 0;
				attack2Time = System.currentTimeMillis();
			}
			if (attack2Time != 0
					&& (System.currentTimeMillis() - attack2Time) >= 230) {
				attack2Time = 0;
				attack3Time = System.currentTimeMillis();
			}
		}

	}

	@Override
	public void render() {
		characters.update();
		drawer.draw(characters.getFirstPlayer());
	}
}
