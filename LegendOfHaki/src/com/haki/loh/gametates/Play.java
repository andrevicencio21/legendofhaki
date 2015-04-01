package com.haki.loh.gametates;

import static com.haki.loh.handlers.B2DVariables.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.LibGdxDrawer;
import com.brashmonkey.spriter.LibGdxLoader;
import com.brashmonkey.spriter.SCMLReader;
import com.brashmonkey.spriter.Spriter;
import com.haki.loh.entities.Entity;
import com.haki.loh.entities.Tanuki;
import com.haki.loh.handlers.GameStateManager;
import com.haki.loh.handlers.MyContactListener;
import com.haki.loh.handlers.MyInput;
import com.haki.loh.main.Game;

public class Play extends GameState {

	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera b2DCam;
	private TiledMap tiledMap;
	private TiledMapRenderer tmr;
	private Tanuki tanuki;

	public Tanuki getTanuki() {
		return tanuki;
	}

	public ShapeRenderer renderer;

	// Debug Stuff /////////////////////////////////////
	private boolean debugMode = true;
	private boolean renderArt = true;
	private BitmapFont debugText;
	public static FileHandle scmlHandle;
	public static SCMLReader reader;
	public static Data data;
	public static LibGdxLoader loader;
	public static LibGdxDrawer drawer;
	public String file = "images/haki/Haki.scml";

	public Play(GameStateManager gsm) {
		super(gsm);
		debugRenderer = new Box2DDebugRenderer();
		entityArray = new Array<Entity>();
		debugText = new BitmapFont();
		debugText.setColor(Color.WHITE);

		createWorld();
		createLevel();

		renderer = new ShapeRenderer();
		scmlHandle = Gdx.files.internal("images/haki/Haki.scml");
		reader = new SCMLReader(scmlHandle.read());
		data = reader.getData();
		loader = new LibGdxLoader(data);
		loader.load(scmlHandle.file());
		drawer = new LibGdxDrawer(loader, batch, renderer);
		Spriter.setDrawerDependencies(batch, renderer);
		Spriter.init(LibGdxLoader.class, LibGdxDrawer.class);
		Spriter.load(Gdx.files.internal(file).read(), "images/haki/Haki.scml");

		createPlayer();
		// set up box 2d camera
		b2DCam = new OrthographicCamera();
		b2DCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

		// set up tiledMapRenderer
		tmr = new OrthogonalTiledMapRenderer(tiledMap);
	}

	@Override
	public void handleInput() {
		// player.handleInput();

		// Debug Input
		if (MyInput.isPressed(MyInput.ESCAPE)) {
			System.exit(0);
		}
		if (MyInput.isPressed(MyInput.num1)) {
			debugMode = !debugMode;
		}
		if (MyInput.isPressed(MyInput.num2)) {
			MapLayer layer = tiledMap.getLayers().get("background");
			layer.setVisible(!layer.isVisible());
		}
		if (MyInput.isPressed(MyInput.num3)) {
			renderArt = !renderArt;
		}

		MyInput.update();

	}

	@Override
	public void update(float dt) {
		world.step(dt, 6, 2);
		tanuki.update(dt);
		Spriter.update();
		for (int i = 0; i < entityArray.size; i++) {
			entityArray.get(i).update(dt);
			if (entityArray.get(i).isRemovable()) {
				world.destroyBody(entityArray.get(i).getBody());
				entityArray.removeValue(entityArray.get(i), true);
			}
		}
		handleInput();

	}

	@Override
	public void render() {

		// Clear Screan
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.position.set(tanuki.getBody().getPosition().x * PPM + 30, tanuki
				.getBody().getPosition().y * PPM + 30, 0);
		camera.update();
		tmr.setView(camera);
		tmr.render();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		if (renderArt) {
			renderer.setProjectionMatrix(camera.combined);
			tanuki.render();
			Spriter.draw();

		}
		if (debugMode) {
			batch.setProjectionMatrix(hudCamera.combined);
			debugText.setScale(0.7f);
			debugText.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
					0, 300);
			debugText.draw(batch, "Vel X: "
					+ tanuki.getBody().getLinearVelocity().x, 0, 290);
			debugText.draw(batch, "Vel Y: "
					+ tanuki.getBody().getLinearVelocity().y, 0, 280);
			debugText.draw(batch, "isGrounded: " + tanuki.isGrounded(), 0, 270);
		}
		batch.end();

		if (debugMode) {
			b2DCam.position.set(tanuki.getBody().getPosition().x + 30 / PPM,
					tanuki.getBody().getPosition().y + 30 / PPM, 0);
			b2DCam.update();
			debugRenderer.render(world, b2DCam.combined);
		}
	}

	public void createPlayer() {
		tanuki = new Tanuki(this);
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
				body2.createFixture(cs, 1).setFriction(1f);
				for (int i = 0; i < body2.getFixtureList().size; i++) {
					body2.getFixtureList().get(i).setUserData("wall");
				}
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
	public LibGdxDrawer getDrawer(){
		return drawer;
	}

	@Override
	public void dispose() {

	}

}
