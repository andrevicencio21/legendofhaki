package com.haki.loh.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.haki.loh.gametates.GameState;
import com.haki.loh.handlers.MyInput;

public class Player extends Entity {
	private Array<Sprite> spriteArrayToDraw, previousSpriteArrayToDraw,
			idleRight, idleLeft, runRight, runLeft, attackRight, attackLeft,
			deadRight, deadLeft, jumpRight, jumpLeft, jumpAttackRight,
			jumpAttackLeft, jumpThrowRight, jumpThrowLeft, slideRight,
			slideLeft, throwRight, throwLeft;
	private Animation animationToDraw;
	private Sprite spriteToDraw;
	private boolean isOnGround, isJumping, isThrowing;
	private boolean isForward = true;
	private boolean directionPressed = false;
	private long throwTime;
	private long throwDuration = 700;
	private float animationTimer;

	public Player(GameState state) {
		super(state);
		this.batch = state.getBatch();
		// Load Texture and SoundAssets
		loadAssets();

		world = state.getWorld();
		BodyDef bdef = new BodyDef();
		bdef.position.set(20 / PPM, 200 / PPM);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(20 / PPM, 14 / PPM);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		body.createFixture(fdef).setUserData(body);

		// create foot
		shape.setAsBox(7f / PPM, 4 / PPM, new Vector2(0, -14 / PPM), 0);
		fdef.shape = shape;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("playerFoot");
		;

	}

	public void handleInput() {
		if (MyInput.isDown(MyInput.RIGHT)) {
			directionPressed = true;
			isForward = true;
			if (body.getLinearVelocity().x <= 2f)
				body.applyLinearImpulse(0.2f, 0, 0, 0, true);
		}

		if (MyInput.isDown(MyInput.LEFT)) {
			directionPressed = true;
			isForward = false;
			if (body.getLinearVelocity().x >= -2f)
				body.applyLinearImpulse(-0.2f, 0, 0, 0, true);
		}

		if ((!MyInput.isDown(MyInput.LEFT) && (!MyInput.isDown(MyInput.RIGHT)))) {
			directionPressed = false;
		}

		if (MyInput.isPressed(MyInput.JUMP)) {
			if (isOnGround)
				body.applyForceToCenter(new Vector2(0, 50), true);
		}
		if (MyInput.isPressed(MyInput.FIRE)) {
			if (!isThrowing) {
				state.getEntityArray().add(new Kunai(state, this, isForward));
				throwTime = System.currentTimeMillis();
				isThrowing = true;
			}
		}

	}

	@Override
	public void update(float dt) {
		animationTimer += Gdx.graphics.getDeltaTime();
		if ((System.currentTimeMillis() - throwTime) >= throwDuration
				&& isThrowing) {

			if (!MyInput.isDown(MyInput.FIRE)) {
				throwTime = 0;
				isThrowing = false;
			}
		}
		linearVelocityX = body.getLinearVelocity().x;
		linearVelocityY = body.getLinearVelocity().y;
		x = body.getPosition().x;
		y = body.getPosition().y;

		if (state.getMyContactListener().numFootContacts > 0) {
			isOnGround = true;
			isJumping = false;
		} else if (state.getMyContactListener().numFootContacts == 0) {
			isOnGround = false;
			isJumping = true;
		}
		setAnimation();

	}

	@Override
	public void render() {
		spriteToDraw.draw(batch);
	}

	public void loadAssets() {
		// load texture Assets
		textureAtlas = new TextureAtlas(
				Gdx.files.internal("images/ninjaboy/ninjaboy.txt"));

		// create Array of Sprites
		// Idle
		idleRight = textureAtlas.createSprites("idle_");
		for (int i = 0; i < idleRight.size; i++) {
			idleRight.get(i).setScale(0.3f, 0.3f);
		}
		idleLeft = textureAtlas.createSprites("idle_");
		for (int i = 0; i < idleLeft.size; i++) {
			idleLeft.get(i).setScale(0.3f, 0.3f);
			idleLeft.get(i).flip(true, false);
		}

		// Run
		runRight = textureAtlas.createSprites("run_");
		for (int i = 0; i < runRight.size; i++) {
			runRight.get(i).setScale(0.3f, 0.3f);
		}

		runLeft = textureAtlas.createSprites("run_");
		for (int i = 0; i < runLeft.size; i++) {
			runLeft.get(i).setScale(0.3f, 0.3f);
			runLeft.get(i).flip(true, false);
		}

		// Attack
		attackRight = textureAtlas.createSprites("attack_");
		for (int i = 0; i < attackRight.size; i++) {
			attackRight.get(i).setScale(0.3f, 0.3f);
		}

		attackLeft = textureAtlas.createSprites("attack_");
		for (int i = 0; i < attackLeft.size; i++) {
			attackLeft.get(i).setScale(0.3f, 0.3f);
			attackLeft.get(i).flip(true, false);
		}

		// dead
		deadRight = textureAtlas.createSprites("dead_");
		for (int i = 0; i < deadRight.size; i++) {
			deadRight.get(i).setScale(0.3f, 0.3f);
		}

		deadLeft = textureAtlas.createSprites("dead_");
		for (int i = 0; i < deadLeft.size; i++) {
			deadLeft.get(i).setScale(0.3f, 0.3f);
			deadLeft.get(i).flip(true, false);
		}

		// jump
		jumpRight = textureAtlas.createSprites("jump_");
		for (int i = 0; i < jumpRight.size; i++) {
			jumpRight.get(i).setScale(0.3f, 0.3f);
		}

		jumpLeft = textureAtlas.createSprites("jump_");
		for (int i = 0; i < jumpLeft.size; i++) {
			jumpLeft.get(i).setScale(0.3f, 0.3f);
			jumpLeft.get(i).flip(true, false);
		}

		// jumpAttack
		jumpAttackRight = textureAtlas.createSprites("jumpAttack_");
		for (int i = 0; i < jumpAttackRight.size; i++) {
			jumpAttackRight.get(i).setScale(0.3f, 0.3f);
		}

		jumpAttackLeft = textureAtlas.createSprites("jumpAttack_");
		for (int i = 0; i < jumpAttackLeft.size; i++) {
			jumpAttackLeft.get(i).setScale(0.3f, 0.3f);
			jumpAttackLeft.get(i).flip(true, false);
		}

		// jumpThrow
		jumpThrowRight = textureAtlas.createSprites("jump_throw_");
		for (int i = 0; i < jumpThrowRight.size; i++) {
			jumpThrowRight.get(i).setScale(0.3f, 0.3f);
		}

		jumpThrowLeft = textureAtlas.createSprites("jump_throw_");
		for (int i = 0; i < jumpThrowLeft.size; i++) {
			jumpThrowLeft.get(i).setScale(0.3f, 0.3f);
			jumpThrowLeft.get(i).flip(true, false);
		}

		// slide
		slideRight = textureAtlas.createSprites("slide_");
		for (int i = 0; i < slideRight.size; i++) {
			slideRight.get(i).setScale(0.3f, 0.3f);
		}

		slideLeft = textureAtlas.createSprites("slide_");
		for (int i = 0; i < slideLeft.size; i++) {
			slideLeft.get(i).setScale(0.3f, 0.3f);
			slideLeft.get(i).flip(true, false);

		}

		// throw
		throwRight = textureAtlas.createSprites("throw_");
		for (int i = 0; i < throwRight.size; i++) {
			throwRight.get(i).setScale(0.3f, 0.3f);
		}

		throwLeft = textureAtlas.createSprites("throw_");
		for (int i = 0; i < throwLeft.size; i++) {
			throwLeft.get(i).setScale(0.3f, 0.3f);
			throwLeft.get(i).flip(true, false);
		}

	}

	public void setAnimation() {
		if (directionPressed == false && isForward && !isJumping)
			spriteArrayToDraw = idleRight;
		if (directionPressed == false && !isForward && !isJumping)
			spriteArrayToDraw = idleLeft;
		if (linearVelocityX > 0 && directionPressed && isOnGround)
			spriteArrayToDraw = runRight;
		if (linearVelocityX < 0 && directionPressed && isOnGround)
			spriteArrayToDraw = runLeft;
		if (linearVelocityX == 0 && isForward && linearVelocityY == 0)
			spriteArrayToDraw = idleRight;
		if (linearVelocityX == 0 && !isForward && linearVelocityY == 0)
			spriteArrayToDraw = idleLeft;
		if (linearVelocityX >= 0 && linearVelocityY != 0 && isForward
				&& isJumping)
			spriteArrayToDraw = jumpRight;
		if (linearVelocityX <= 0 && linearVelocityY != 0 && !isForward
				&& isJumping)
			spriteArrayToDraw = jumpLeft;
		if (isThrowing && isOnGround && isForward && (System.currentTimeMillis() - throwTime) <= throwDuration) {
			spriteArrayToDraw = throwRight;
		}
		if (isThrowing && isOnGround && !isForward && (System.currentTimeMillis() - throwTime) <= throwDuration) {
			spriteArrayToDraw = throwLeft;
		}
		if (isThrowing && isJumping && isForward && (System.currentTimeMillis() - throwTime) <= throwDuration) {
			spriteArrayToDraw = jumpThrowRight;
		}
		if (isThrowing && isJumping && !isForward && (System.currentTimeMillis() - throwTime) <= throwDuration) {
			spriteArrayToDraw = jumpThrowLeft;
		}
		if (previousSpriteArrayToDraw != spriteArrayToDraw) {
			animationTimer = 0;
		}

		if (spriteArrayToDraw == idleRight || spriteArrayToDraw == idleLeft) {
			animationToDraw = new Animation(0.05f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, true));
		}
		if (spriteArrayToDraw == runRight || spriteArrayToDraw == runLeft) {
			animationToDraw = new Animation(0.05f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, true));
		}
		if (spriteArrayToDraw == jumpRight || spriteArrayToDraw == jumpLeft) {
			animationToDraw = new Animation(0.03f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}
		if (spriteArrayToDraw == attackRight || spriteArrayToDraw == attackLeft) {
			animationToDraw = new Animation(0.05f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}
		if (spriteArrayToDraw == jumpAttackRight
				|| spriteArrayToDraw == jumpAttackLeft) {
			animationToDraw = new Animation(0.05f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}
		if (spriteArrayToDraw == throwRight || spriteArrayToDraw == throwLeft) {
			animationToDraw = new Animation(0.05f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}
		if (spriteArrayToDraw == jumpThrowRight
				|| spriteArrayToDraw == jumpThrowLeft) {
			animationToDraw = new Animation(0.03f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}
		if (spriteArrayToDraw == slideRight || spriteArrayToDraw == slideLeft) {
			animationToDraw = new Animation(0.03f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}
		if (spriteArrayToDraw == deadRight || spriteArrayToDraw == deadLeft) {
			animationToDraw = new Animation(0.05f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}

		spriteToDraw.setPosition(x * PPM - spriteToDraw.getRegionWidth() / 2, y
				* PPM - spriteToDraw.getRegionHeight() / 2);
		previousSpriteArrayToDraw = spriteArrayToDraw;
	}
}
