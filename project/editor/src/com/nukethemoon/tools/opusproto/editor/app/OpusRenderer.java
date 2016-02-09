package com.nukethemoon.tools.opusproto.editor.app;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.opusproto.editor.InputController;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.previews.SamplerPreviewImage;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;

import java.util.*;
import java.util.List;

public class OpusRenderer {

	private SpriteBatch batch;
	private ShapeRenderer worldShapeRenderer;
	private ShapeRenderer screenShapeRenderer;
	private OrthographicCamera camera;

	private Vector3 tmpVector0 = new Vector3();
	private Vector3 tmpVector1 = new Vector3();
	private Vector3 tmpVector2 = new Vector3();
	private Vector3 tmpVector3 = new Vector3();
	private Vector3 tmpVector4 = new Vector3();

	private Map<Layer, List<Sprite>> layerToSprites = new HashMap<Layer, List<Sprite>>();

	private float blinkProgress = 0f;
	private Color blinkColor = new Color();
	private boolean chunkRendererEnabled = false;
	private Sprite samplerSnapshot = null;
	private boolean samplerSnapshotDrawBehind = true;
	private Color clearColor = Color.GRAY;
	private Stage stage;
	private Ani ani;
	private InputController inputController;

	private int windowWidth;
	private int windowHeight;

	public OpusRenderer(int windowWidth, int windowHeight, Stage STAGE) {
		Gdx.graphics.setDisplayMode(windowWidth, windowHeight, false);
		this.windowHeight = windowHeight;
		this.windowWidth = windowWidth;
		stage = STAGE;
		ani = new Ani();
		camera = new OrthographicCamera(windowWidth, windowHeight);
		camera.position.set(0, 0, 10);
		batch = new SpriteBatch();

		worldShapeRenderer = new ShapeRenderer();
		worldShapeRenderer.setAutoShapeType(true);
		screenShapeRenderer = new ShapeRenderer();
		screenShapeRenderer.setAutoShapeType(true);
	}


	public void setChunkRendering(boolean enabled) {
		chunkRendererEnabled = enabled;
	}

	public void setClearColor(Color color) {
		clearColor = color;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void putLayerSprite(Layer l, List<Sprite> sprites) {
		layerToSprites.put(l, sprites);
	}

	public void addSprite(Layer l, Sprite sprite) {
		layerToSprites.get(l).add(sprite);
	}

	public void initLayerSprite(List<Layer> layers) {
		for (Layer l : layers) {
			if (layerToSprites.get(l) == null) {
				layerToSprites.put(l, new ArrayList<Sprite>());
			}
		}
	}

	public void dispose() {
		for (List<Sprite> list : layerToSprites.values()) {
			for (Sprite sprite : list) {
				sprite.getTexture().dispose();
			}
		}
		layerToSprites.clear();
	}

	public void clearSnapshot() {
		samplerSnapshot = null;
	}


	public List<int[]> getChunkPositionsInRect(float px1, float py1, float px2, float py2,
											   int mapSize, float performanceFactor) {
		tmpVector4.set(px1, py1, 0);
		camera.unproject(tmpVector4);
		int x1 = worldToMap(tmpVector4.x, mapSize);
		int y1 = worldToMap(tmpVector4.y, mapSize);
		tmpVector4.set(px2, py2, 0);
		camera.unproject(tmpVector4);
		int x2 = worldToMap(tmpVector4.x, mapSize);
		int y2 = worldToMap(tmpVector4.y, mapSize);

		int lowX = x1 < x2 ? x1 : x2;
		int highX = x1 > x2 ? x1 : x2;
		int lowY = y1 < y2 ? y1 : y2;
		int highY = y1 > y2 ? y1 : y2;


		if ((highX - lowX) * (highY - lowY) > mapSize * (50 * performanceFactor)) {
			return null;
		}

		List<int[]> positions = new ArrayList<int[]>();
		for (int x = lowX; x <= lowX + (highX - lowX); x++) {
			for (int y = lowY; y <= lowY + (highY - lowY); y++) {
				positions.add(new int[]{x, y});
			}
		}
		return positions;
	}

	public void createSnapshot(boolean drawBehind, AbstractSampler sampler, float opacity) {
		float zoom = camera.zoom;
		if (camera.zoom > 2.5) {
			zoom = 2.5f;
		}
		samplerSnapshotDrawBehind = drawBehind;
		int w = (int) (windowWidth * (zoom));
		int h = (int) (windowHeight * (zoom));
		int offsetX = (int) ((-w / 2) + camera.position.x);
		int offsetY = (int) ((-h / 2) + camera.position.y);
		Pixmap pixmap = SamplerPreviewImage.create(sampler, w, h, 1, offsetX, offsetY);
		samplerSnapshot = new Sprite(new Texture(pixmap));
		samplerSnapshot.setAlpha(opacity);
		samplerSnapshot.setPosition(offsetX, offsetY);
	}

	public void setSnapshotAlpha(float snapshotAlpha) {
		if (samplerSnapshot != null) {
			samplerSnapshot.setAlpha(snapshotAlpha);
		}
	}

	public void setSnapshotBehind(boolean behind) {
		samplerSnapshotDrawBehind = behind;
	}


	public void render(int mapSize, List<Layer> layers) {
		handleCameraMovement();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		worldShapeRenderer.setProjectionMatrix(camera.combined);
		screenShapeRenderer.setProjectionMatrix(stage.getCamera().combined);

		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (chunkRendererEnabled) {
			blinkProgress = (blinkProgress + 0.04f) % 1f;
			drawGrid(mapSize);
			batch.begin();
			if (samplerSnapshot != null && samplerSnapshotDrawBehind) {
				samplerSnapshot.draw(batch);
			}
			for (Layer layer : layers) {
				for (Sprite sprite : layerToSprites.get(layer)) {
					sprite.draw(batch);
				}
			}


			if (samplerSnapshot != null && !samplerSnapshotDrawBehind) {
				samplerSnapshot.draw(batch);
			}
			batch.end();


			drawOrigin();
			drawTileSelection();
			drawCameraCenter();


			drawPoints();

		}
		ani.update();

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}


	private void drawPoints() {

		/*screenShapeRenderer.begin();

		for (int index = 0; index < points.length; index = index + 2) {
			float x = points[index];
			float y = points[index + 1];
			tmpVector1.set(x, y, 0);
			camera.project(tmpVector1);

			screenShapeRenderer.setColor(Color.BLUE);
			screenShapeRenderer.rect(tmpVector1.x - 1, tmpVector1.y - 1, 1, 1);

		}
		screenShapeRenderer.end();*/
	}

	public void drawSelectionRect(float px1, float py1, float px2, float py2) {
			screenShapeRenderer.begin();
			float y1 = windowHeight - py1;
			float x1 = px1;
			float height = (windowHeight - py2) - y1;
			float width = px2 - px1;
			screenShapeRenderer.rect(x1, y1, width, height);
			screenShapeRenderer.end();
	}

	private void drawOrigin() {
		tmpVector2.set(0, 0, 0);
		camera.project(tmpVector2, 0, 0, windowWidth, windowHeight);
		screenShapeRenderer.begin();
		screenShapeRenderer.setColor(Color.RED); // x
		screenShapeRenderer.line(tmpVector2.x, tmpVector2.y, 0, tmpVector2.x + 80, tmpVector2.y, 0);
		screenShapeRenderer.line(tmpVector2.x, tmpVector2.y + 1, 0, tmpVector2.x + 80, tmpVector2.y + 1, 0);
		screenShapeRenderer.setColor(Color.YELLOW); // y
		screenShapeRenderer.line(tmpVector2.x, tmpVector2.y, 0, tmpVector2.x, tmpVector2.y + 80, 0);
		screenShapeRenderer.line(tmpVector2.x + 1, tmpVector2.y, 0, tmpVector2.x + 1, tmpVector2.y + 80, 0);
		screenShapeRenderer.end();
	}

	private void drawTileSelection() {
		int x = Math.round(inputController.getMouseWorldPosition().x - 0.5f);
		int y = Math.round(inputController.getMouseWorldPosition().y - 0.5f);
		tmpVector3.set(x, y, 0);
		camera.project(tmpVector3);
		screenShapeRenderer.begin();
		blinkColor.set(blinkProgress, blinkProgress, blinkProgress, 1f);
		screenShapeRenderer.setColor(blinkColor); // x
		screenShapeRenderer.rect(tmpVector3.x, tmpVector3.y, 1 / camera.zoom, 1 / camera.zoom);
		screenShapeRenderer.end();
	}

	private void drawGrid(int mapSize) {
		screenShapeRenderer.begin();
		tmpVector1.set(camera.position.x - ((windowWidth / 2) * camera.zoom),
				camera.position.y + ((windowHeight / 2) * camera.zoom), 0);
		camera.project(tmpVector1);
		float screenLeft = tmpVector1.x;
		float screenTop = tmpVector1.y;
		tmpVector1.set(camera.position.x + ((windowWidth / 2) * camera.zoom),
				camera.position.y - ((windowHeight / 2) * camera.zoom), 0);
		camera.project(tmpVector1);
		screenShapeRenderer.setColor(Styles.GRID_COLOR_DARK);
		float screenRight = tmpVector1.x;
		float screenBottom = tmpVector1.y;
		float screenWidth = Math.abs(screenRight - screenLeft);
		float screenHeight = Math.abs(screenTop + screenBottom);
		int linesCountX = (int) ((screenWidth * camera.zoom) / mapSize) + 4;
		int linesCountY = (int) ((screenHeight * camera.zoom) / mapSize) + 4;
		int linesOffsetX = (int) (camera.position.x / mapSize);
		int linesOffsetY = (int) (camera.position.y / mapSize);
		if (linesCountX < 100 && linesCountY < 100) {
			for (int x = -linesCountX / 2; x < linesCountX / 2; x++) {
				for (int y = -linesCountY / 2; y < linesCountY / 2; y++) {
					int offsetX = x + linesOffsetX;
					int offsetY = y + linesOffsetY;
					// horizontal lines
					tmpVector0.set(camera.position.x - screenWidth * camera.zoom, mapSize * offsetY, 0);
					tmpVector1.set(camera.position.x + screenWidth * camera.zoom, mapSize * offsetY, 0);
					camera.project(tmpVector1);
					camera.project(tmpVector0);
					screenShapeRenderer.line(tmpVector0.x, tmpVector0.y, tmpVector1.x, tmpVector1.y);
					// vertical lines
					tmpVector0.set(mapSize * offsetX, camera.position.y - screenHeight * camera.zoom, 0);
					tmpVector1.set(mapSize * offsetX, camera.position.y + screenHeight * camera.zoom, 0);
					camera.project(tmpVector1);
					camera.project(tmpVector0);
					screenShapeRenderer.line(tmpVector0.x, tmpVector0.y, tmpVector1.x, tmpVector1.y);
				}
			}
		}
		screenShapeRenderer.end();
	}

	private void drawCameraCenter() {
		screenShapeRenderer.begin();
		blinkColor.set(1f - blinkProgress, 1f - blinkProgress, 1f - blinkProgress, 1f);
		screenShapeRenderer.setColor(blinkColor); // x
		float screenCenterX = windowWidth / 2;
		float screenCenterY = windowHeight / 2;
		screenShapeRenderer.line(screenCenterX - 10, screenCenterY, screenCenterX + 10, screenCenterY);
		screenShapeRenderer.line(screenCenterX, screenCenterY - 10, screenCenterX, screenCenterY + 10);
		screenShapeRenderer.end();

	}

	private void handleCameraMovement() {
		if (inputController == null) {
			return;
		}

		for (Integer keycode : inputController.getKeysDown()) {
			float movement = camera.zoom * 4;
			if (keycode == 19) { // up
				camera.position.add(0, movement, 0);
			}
			if (keycode == 20) { // down
				camera.position.add(0, -movement, 0);
			}
			if (keycode == 21) { // left
				camera.position.add(-movement, 0, 0);
			}
			if (keycode == 22) { // right
				camera.position.add(movement, 0, 0);
			}
		}
	}

	private int worldToMap(float coordinate, int mapSize) {
		int x = (int) (coordinate / mapSize);
		if (coordinate < 0) {
			x = x-1;
		}
		return x;
	}

	public Ani getAni() {
		return ani;
	}

	public void setInputController(InputController inputController) {
		this.inputController = inputController;
	}


	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}
}
