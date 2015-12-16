package com.nukethemoon.tools.opusproto.editor.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.previews.SamplerPreviewImage;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.noise.algorithms.CellNoise;
import com.nukethemoon.tools.opusproto.sampler.acontinent.AContinent;
import com.nukethemoon.tools.opusproto.sampler.acontinent.AContinentConfig;
import com.nukethemoon.tools.opusproto.log.Log;

import java.util.ArrayList;
import java.util.List;


public class MapGenTest implements ApplicationListener  {


	private static LwjglApplicationConfiguration cfg;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private List<Sprite> sprites = new ArrayList<Sprite>();
	private Drawable drawable;

	public static void main(String [] args)	{
		cfg = new LwjglApplicationConfiguration();
		cfg.title = "MapGen";
		cfg.width = 1300;
		cfg.height = 800;
		cfg.samples = 1;
		new EditorApplication(new MapGenTest(), cfg);
	}

	int size = 800;

	@Override
	public void create() {
		Log.out = new Log.Out() {
			@Override
			public void logError(String tag, String message) {
				Gdx.app.error(tag, message);
			}

			@Override
			public void logInfo(String tag, String message) {
				Gdx.app.log(tag, message);
			}

			@Override
			public void logDebug(String tag, String message) {
				Gdx.app.debug(tag, message);
			}
		};

		camera = new OrthographicCamera(cfg.width, cfg.height);
		camera.position.set(0, 0, 10);
		batch = new SpriteBatch();
		/*try {
			initSampler();
		} catch (SamplerInvalidConfigException e) {
			e.printStackTrace();
		}*/
		initDiamondSquare();

	}

	private void initSampler() throws SamplerInvalidConfigException {
		Algorithms noisePool = new Algorithms();
		AContinent m = new AContinent(new AContinentConfig("sd"), 234d, noisePool, null);
		SamplerPreviewImage previewImage = new SamplerPreviewImage();
		previewImage.applySampler(m, size, false);
		drawable = previewImage.getDrawable();

	}

	private void initDiamondSquare() {
		//DiamondSquare ds = new DiamondSquare();
		//int[][] map = ds.makeHeightMap(9, 128, 128);

		int size = 300;
		sprites.add(createDiamondSprite(0, 0, size));
		sprites.add(createDiamondSprite(size, 0, size));
		sprites.add(createDiamondSprite(size, size, size));
		sprites.add(createDiamondSprite(0, size, size));
	}


	private Sprite createDiamondSprite(int xIn, int yIn, int size) {
		//float[][] map = MidpointDisplacement.createDataMap(xIn, yIn, size, size, 7, 0);

		float[][] map = new CellNoise(new Algorithms()).createData(xIn, yIn, size, 2344, 1);

		Pixmap pixmap = new Pixmap(map.length, map[0].length, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				float value = map[x][map[x].length - y - 1];
				pixmap.drawPixel(x, y, Color.argb8888(value, value, value, 1));
			}
		}

		Sprite sprite = new Sprite(new Texture(pixmap));
		sprite.setPosition(xIn, yIn);
		return sprite;
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		camera.update();
		batch.begin();

		for (Sprite s : sprites) {
			s.draw(batch);
		}

		if (drawable != null) {
			drawable.draw(batch, 0, 0, size, size);
		}
		batch.end();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
}
