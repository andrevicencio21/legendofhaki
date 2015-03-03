package com.haki.loh.gametates;

import static com.haki.loh.handlers.B2DVariables.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.haki.loh.entities.Entity;
import com.haki.loh.entities.Player;
import com.haki.loh.entities.TestEnemy;
import com.haki.loh.handlers.GameStateManager;
import com.haki.loh.handlers.MyContactListener;
import com.haki.loh.handlers.MyInput;
import com.haki.loh.main.Game;

public class Play extends GameState {

	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera b2DCam;
	private TiledMap tiledMap;
	private TiledMapRenderer tmr;
	private Player player;
	private boolean debugMode = true;
	private BitmapFont debugText;

	public Play(GameStateManager gsm) {
		super(gsm);
		debugRenderer = new Box2DDebugRenderer();
		entityArray = new Array<Entity>();
		debugText = new BitmapFont();
		debugText.setColor(Color.WHITE);

		createWorld();
		createLevel();
		createPlayer();
		entityArray.add(new TestEnemy(this));

		// set up box 2d camera
		b2DCam = new OrthographicCamera();
		b2DCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

		// set up tiledMapRenderer
		tmr = new OrthogonalTiledMapRenderer(tiledMap);
	}

	@Override
	public void handleInput() {
		player.handleInput();

		if (MyInput.isPressed(MyInput.ESCAPE)) {
			System.exit(0);
		}
	}

	@Override
	public void update(float dt) {
		world.step(dt, 6, 2);
		handleInput();
		player.update(dt);

		for (int i = 0; i < entityArray.size; i++) {

			entityArray.get(i).update(dt);
			if (entityArray.get(i).isRemovable()) {
				world.destroyBody(entityArray.get(i).getBody());
				entityArray.removeValue(entityArray.get(i), true);
			}
		}
	}

	@Override
	public void render() {

		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear Screan

		// fix DebugRenderer Camera to follow Player

		// fix regular camera to follow player
		camera.position.set(player.getBody().getPosition().x * PPM + 30, player
				.getBody().getPosition().y * PPM + 30, 0);
		camera.update();

		// render tile map
		tmr.setView(camera);
		tmr.render();

		// fix debug camera and render Debug World
		if (debugMode) {
			b2DCam.position.set(player.getBody().getPosition().x + 30 / PPM,
					player.getBody().getPosition().y + 30 / PPM, 0);
			b2DCam.update();
			debugRenderer.render(world, b2DCam.combined);
		}

		// render sprites
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		player.render();
		for (int i = 0; i < entityArray.size; i++) {
			entityArray.get(i).render();
		}

		if (debugMode) {
			batch.setProjectionMatrix(hudCamera.combined);
	
			debugText.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
					40, 200);
			
		}
		batch.end();
		

	}

	public void createPlayer() {
		player = new Player(this);
	}

	public void createWorld() {
		world = new World(new Vector2(0, -9.81f), true);
		myContactListener = new MyContactListener();
		world.setContactListener(myContactListener);
	}

	public void createLevel() {
		tiledMap = new TmxMapLoader().load("maps/testmap.tmx"); // Load Tile Map
		PolygonShape shape = new PolygonShape();
		ChainShape cs = new ChainShape();
		CircleShape circleShape = new CircleShape();
		MapLayer layer = tiledMap.getLayers().get("collision");
		for (MapObject mo : layer.getObjects()) {
			if (mo instanceof RectangleMapObject) {
				shape = getRectangle((RectangleMapObject) mo);
				BodyDef bd = new BodyDef();
				bd.type = BodyType.StaticBody;
				Body body2 = world.createBody(bd);
				body2.createFixture(shape, 1).setFriction(0.7f);
				for (int i = 0; i < body2.getFixtureList().size; i++) {
					body2.getFixtureList().get(i).setUserData("wall");
				}

			} else if (mo instanceof PolylineMapObject) {
				cs = getPolyline((PolylineMapObject) mo);
				BodyDef bd = new BodyDef();
				bd.type = BodyType.StaticBody;
				Body body2 = world.createBody(bd);
				for (int i = 0; i < body2.getFixtureList().size; i++) {
					body2.getFixtureList().get(i).setUserData("wall");
				}
				body2.createFixture(cs, 1).setFriction(0.7f);
			} else if (mo instanceof PolygonMapObject) {
				shape = getPolygon((PolygonMapObject) mo);
				BodyDef bd = new BodyDef();
				bd.type = BodyType.StaticBody;
				Body body2 = world.createBody(bd);
				body2.createFixture(shape, 1).setFriction(0.7f);
				body2.getFixtureList().get(0).getShape();
				for (int i = 0; i < body2.getFixtureList().size; i++) {
					body2.getFixtureList().get(i).setUserData("wall");
				}
			}
			// else if (mo instanceof EllipseMapObject) {
			// circleShape = getEllipse((PolygonMapObject) mo);
			// BodyDef bd = new BodyDef();
			// bd.type = BodyType.StaticBody;
			// Body body2 = world.createBody(bd);
			// body2.createFixture(circleShape, 1).setFriction(0.7f);
			// }
		}
		shape.dispose();
		cs.dispose();
		circleShape.dispose();

	}

	private PolygonShape getPolygon(PolygonMapObject mo) {
		PolygonShape polygon = new PolygonShape();
		float[] vertices = mo.getPolygon().getTransformedVertices();

		float[] worldVertices = new float[vertices.length];

		for (int i = 0; i < vertices.length; ++i) {
			// System.out.println(vertices[i]);
			worldVertices[i] = vertices[i] / PPM;
		}

		polygon.set(worldVertices);
		return polygon;
	}

	private static ChainShape getPolyline(PolylineMapObject polylineObject) {
		float[] vertices = polylineObject.getPolyline()
				.getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];

		for (int i = 0; i < vertices.length / 2; ++i) {
			worldVertices[i] = new Vector2();
			worldVertices[i].x = vertices[i * 2] / PPM;
			worldVertices[i].y = vertices[i * 2 + 1] / PPM;
		}

		ChainShape chain = new ChainShape();
		chain.createChain(worldVertices);
		return chain;
	}

	private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
		Rectangle rectangle = rectangleObject.getRectangle();
		PolygonShape polygon = new PolygonShape();
		Vector2 size = new Vector2(
				(rectangle.x + rectangle.width * 0.5f) / PPM,
				(rectangle.y + rectangle.height * 0.5f) / PPM);
		polygon.setAsBox(rectangle.width * 0.5f / PPM, rectangle.height * 0.5f
				/ PPM, size, 0.0f);
		return polygon;
	}

	@Override
	public void dispose() {

	}

}
