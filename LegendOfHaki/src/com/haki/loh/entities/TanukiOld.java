package com.haki.loh.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.LibGdxDrawer;
import com.brashmonkey.spriter.LibGdxLoader;
import com.brashmonkey.spriter.Mainline;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Player.PlayerListener;
import com.brashmonkey.spriter.SCMLReader;
import com.haki.loh.gametates.GameState;
import com.haki.loh.handlers.MyInput;

public class TanukiOld extends Entity {
	private TextureAtlas atlasRun, atlasJump, atlasIdle, atlasThrow,
			atlasAttack;
	private Array<Sprite> spriteArrayToDraw, previousSpriteArrayToDraw,
			idleRight, idleLeft, runRight, runLeft, jumpRight, jumpLeft,
			throwRight, throwLeft, attackRight, attackLeft;
	private Animation animationToDraw;
	private Sprite spriteToDraw;
	private boolean isOnGround, isJumping, isThrowing, isAttacking;
	private boolean isBusy = false;
	private boolean isForward = true;
	private boolean directionPressed = false;
	private long throwTime;
	private long throwDelay = 500;
	private float animationTimer;
	private long attackDelay = 400;
	private long attackTime;
	private Player character;
	private Drawer<Sprite> drawer;
	private LibGdxLoader loader;
	ShapeRenderer renderer;
	PlayerListener listener;

	/*
	 * public void create() { cam = new OrthographicCamera(); cam.zoom = 1f;
	 * renderer = new ShapeRenderer(); batch = new SpriteBatch(); FileHandle
	 * handle = Gdx.files.internal("assets/haki/Haki.scml"); Data data = new
	 * SCMLReader(handle.read()).getData();
	 * 
	 * loader = new LibGdxLoader(data); loader.load(handle.file());
	 * 
	 * drawer = new LibGdxDrawer(loader, batch, renderer);
	 * 
	 * player = new Player(data.getEntity("entity_000")); player.setScale(0.5f);
	 * 
	 * }
	 * 
	 * @Override public void resize(int width, int height) {
	 * cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	 * cam.position.set(0, 0, 0f); cam.update();
	 * renderer.setProjectionMatrix(cam.combined);
	 * batch.setProjectionMatrix(cam.combined); }
	 * 
	 * @Override public void render() {
	 * Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	 * 
	 * player.update();
	 * 
	 * batch.begin(); drawer.draw(player); batch.end();
	 * 
	 * }
	 * 
	 * @Override public void pause() { }
	 * 
	 * @Override public void resume() { }
	 * 
	 * @Override public void dispose() { renderer.dispose(); loader.dispose(); }
	 * 
	 * public static void main(String[] args){ LwjglApplicationConfiguration cfg
	 * = new LwjglApplicationConfiguration(); cfg.title = "LibGdx test";
	 * cfg.width = 1280; cfg.height = 720;
	 * 
	 * new LwjglApplication(new LibGdxTest(), cfg); }
	 */

	public TanukiOld(GameState state) {
		super(state);
		this.batch = state.getBatch();
		world = state.getWorld();
		// Load Texture and SoundAssets
		loadAssets();
		createBody();
	}

	@Override
	public void update(float dt) {
		animationTimer += Gdx.graphics.getDeltaTime();
		linearVelocityX = body.getLinearVelocity().x;
		linearVelocityY = body.getLinearVelocity().y;
		x = body.getPosition().x;
		y = body.getPosition().y;
		if (throwTime != 0) {
			if ((System.currentTimeMillis() - throwTime) >= throwDelay) {
				throwTime = 0;
			}
		}
		if (attackTime != 0) {
			if ((System.currentTimeMillis() - attackTime) >= attackDelay) {
				attackTime = 0;
			}
		}

		setPlayerState();
		setAnimation();

	}

	public void setPlayerState() {
		if (isThrowing || isAttacking) {
			isBusy = true;
		} else if (!isThrowing && !isAttacking) {
			isBusy = false;
		}
		if (state.getMyContactListener().numFootContacts > 0) {
			isOnGround = true;
			isJumping = false;
		} else if (state.getMyContactListener().numFootContacts == 0) {
			isOnGround = false;
			isJumping = true;
		}
		if (throwTime != 0) {
			isThrowing = true;
		} else if (throwTime == 0) {
			isThrowing = false;
		}
		if (attackTime != 0) {
			isAttacking = true;
		} else if (attackTime == 0) {
			isAttacking = false;
		}

	}

	public void createBody() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(100 / PPM, 200 / PPM);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(8 / PPM, 17 / PPM);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		body.createFixture(fdef).setUserData(body);

		// create foot
		shape.setAsBox(7f / PPM, 4 / PPM, new Vector2(0, -17 / PPM), 0);
		fdef.shape = shape;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("playerFoot");

	}

	public void handleInput() {

		if (MyInput.isDown(MyInput.RIGHT)) {
			if (!isBusy || (isBusy && isForward)) {
				directionPressed = true;
				isForward = true;
				if (body.getLinearVelocity().x <= 2f)
					body.applyLinearImpulse(0.2f, 0, 0, 0, true);
			}

		}

		if (MyInput.isDown(MyInput.LEFT)) {
			if (!isBusy || (isBusy && !isForward)) {
				directionPressed = true;
				isForward = false;
				if (body.getLinearVelocity().x >= -2f)
					body.applyLinearImpulse(-0.2f, 0, 0, 0, true);
			}
		}

		if ((!MyInput.isDown(MyInput.LEFT) && (!MyInput.isDown(MyInput.RIGHT)))) {
			directionPressed = false;
		}

		if (MyInput.isPressed(MyInput.JUMP)) {
			if (isOnGround)
				body.applyForceToCenter(new Vector2(0, 250), true);
		}
		if (!isBusy) {
			if (MyInput.isPressed(MyInput.FIRE)) {
				if (throwTime == 0) {
					state.getEntityArray().add(
							new Kunai(state, this, isForward));
					throwTime = System.currentTimeMillis();
				}
			}
			if (MyInput.isPressed(MyInput.ATTACK)) {
				if (attackTime == 0)
					attackTime = System.currentTimeMillis();
			}
		}

	}

	@Override
	public void render() {
		renderer.setProjectionMatrix(state.getCamera().combined);
		spriteToDraw.draw(batch);
		character.setPosition(x * PPM, y * PPM);
		character.update();
		//drawer.draw(character);
	}

	public void loadAssets() {
		FileHandle handle = Gdx.files.internal("images/haki/Haki.scml");
		Data data = new SCMLReader(handle.read()).getData();
		loader = new LibGdxLoader(data);
		loader.load(handle.file());
		renderer = new ShapeRenderer();
		drawer = new LibGdxDrawer(loader, batch, renderer);
	

		character = new Player(data.getEntity("Haki"));
		character.setScale(0.3f);
		character.speed = 20;
		character.setAnimation("attack1");
		listener = new PlayerListener() {

			/**
			 * Gets called before a player updates the current animation.
			 * 
			 * @param player
			 *            the player which is calling this method.
			 */
			public void preProcess(Player player) {
			}

			/**
			 * Gets called after a player updated the current animation.
			 * 
			 * @param player
			 *            the player which is calling this method.
			 */
			public void postProcess(Player player) {
			}

			/**
			 * Gets called if the mainline key gets changed. If
			 * {@link Player#speed} is big enough it can happen that mainline
			 * keys between the previous and the new mainline key will be
			 * ignored.
			 * 
			 * @param prevKey
			 *            the previous mainline key
			 * @param newKey
			 *            the new mainline key
			 */
			public void mainlineKeyChanged(Mainline.Key prevKey,
					Mainline.Key newKey) {
			}

			@Override
			public void animationFinished(
					com.brashmonkey.spriter.Animation animation) {
				if(animation.looping == false){
	
				}
			}

			@Override
			public void animationChanged(
					com.brashmonkey.spriter.Animation oldAnim,
					com.brashmonkey.spriter.Animation newAnim) {
				System.out.println("changed");
			}

		};
		character.addListener(listener);

		// load texture Atlas
		atlasIdle = new TextureAtlas(
				Gdx.files.internal("images/haki/animations/idle.txt"));
		idleRight = atlasIdle.createSprites("idle_");
		for (int i = 0; i < idleRight.size; i++)
			idleRight.get(i).setScale(0.3f);

		idleLeft = atlasIdle.createSprites("idle_");
		for (int i = 0; i < idleLeft.size; i++) {
			idleLeft.get(i).flip(true, false);
			idleLeft.get(i).setScale(0.3f);
		}

		atlasRun = new TextureAtlas(
				Gdx.files.internal("images/haki/animations/run.txt"));
		runRight = atlasRun.createSprites("run_");
		for (int i = 0; i < runRight.size; i++)
			runRight.get(i).setScale(0.3f);
		runLeft = atlasRun.createSprites("run_");
		for (int i = 0; i < runLeft.size; i++) {
			runLeft.get(i).setScale(0.3f);
			runLeft.get(i).flip(true, false);
		}

		atlasJump = new TextureAtlas(
				Gdx.files.internal("images/haki/animations/jump.txt"));
		jumpRight = atlasJump.createSprites("jump_");
		for (int i = 0; i < jumpRight.size; i++)
			jumpRight.get(i).setScale(0.3f);
		jumpLeft = atlasJump.createSprites("jump_");
		for (int i = 0; i < jumpLeft.size; i++) {
			jumpLeft.get(i).setScale(0.3f);
			jumpLeft.get(i).flip(true, false);
		}

		atlasThrow = new TextureAtlas(
				Gdx.files.internal("images/haki/animations/throw.txt"));
		throwRight = atlasThrow.createSprites("throw_");
		for (int i = 0; i < throwRight.size; i++)
			throwRight.get(i).setScale(0.3f, 0.3f);
		throwLeft = atlasThrow.createSprites("throw_");
		for (int i = 0; i < throwLeft.size; i++) {
			throwLeft.get(i).setScale(0.3f);
			throwLeft.get(i).flip(true, false);
		}

		atlasAttack = new TextureAtlas(
				Gdx.files.internal("images/haki/animations/attack.txt"));
		attackRight = atlasAttack.createSprites("attack_");
		for (int i = 0; i < attackRight.size; i++)
			attackRight.get(i).setScale(0.3f, 0.3f);
		attackLeft = atlasAttack.createSprites("attack_");
		for (int i = 0; i < attackLeft.size; i++) {
			attackLeft.get(i).setScale(0.3f);
			attackLeft.get(i).flip(true, false);
		}

	}

	public void setAnimation() {
		if (!directionPressed && isForward && !isJumping)
			spriteArrayToDraw = idleRight;
		if (!directionPressed && !isForward && !isJumping)
			spriteArrayToDraw = idleLeft;
		if (linearVelocityX > 0 && directionPressed && isOnGround)
			spriteArrayToDraw = runRight;
		if (linearVelocityX < 0 && directionPressed && isOnGround)
			spriteArrayToDraw = runLeft;
		// if (linearVelocityX == 0 && isForward && linearVelocityY == 0)
		// spriteArrayToDraw = idleRight;
		// if (linearVelocityX == 0 && !isForward && linearVelocityY == 0)
		// spriteArrayToDraw = idleLeft;
		if (linearVelocityX >= 0 && linearVelocityY != 0 && isForward
				&& isJumping)
			spriteArrayToDraw = jumpRight;
		if (linearVelocityX <= 0 && linearVelocityY != 0 && !isForward
				&& isJumping)
			spriteArrayToDraw = jumpLeft;
		if (isThrowing && ((System.currentTimeMillis() - throwTime) <= 200)) {
			if (isForward)
				spriteArrayToDraw = throwRight;
			else
				spriteArrayToDraw = throwLeft;
		}
		if (attackTime != 0) {
			if (isForward)
				spriteArrayToDraw = attackRight;
			else if (!isForward)
				spriteArrayToDraw = attackLeft;
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
			animationToDraw = new Animation(0.03f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, true));
		}
		if (spriteArrayToDraw == jumpRight || spriteArrayToDraw == jumpLeft) {
			animationToDraw = new Animation(0.03f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}
		if (spriteArrayToDraw == throwRight || spriteArrayToDraw == throwLeft) {
			animationToDraw = new Animation(0.03f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}
		if (spriteArrayToDraw == attackRight || spriteArrayToDraw == attackLeft) {
			animationToDraw = new Animation(0.03f, spriteArrayToDraw);
			spriteToDraw = ((Sprite) animationToDraw.getKeyFrame(
					animationTimer, false));
		}

		spriteToDraw.setPosition(x * PPM - spriteToDraw.getRegionWidth() / 2, y
				* PPM - spriteToDraw.getRegionHeight() / 2 + 25);
		previousSpriteArrayToDraw = spriteArrayToDraw;
	}

}