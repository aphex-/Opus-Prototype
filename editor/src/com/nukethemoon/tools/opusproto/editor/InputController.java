package com.nukethemoon.tools.opusproto.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandDrawMap;
import com.nukethemoon.tools.opusproto.editor.message.CommandDrawRectangle;
import com.nukethemoon.tools.opusproto.editor.message.CommandGenerateWorld;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshot;
import com.nukethemoon.tools.opusproto.editor.ui.UI;
import com.nukethemoon.tools.opusproto.generator.WorldGenerator;

import java.util.ArrayList;
import java.util.List;

public class InputController implements InputProcessor {

	private List<Integer> keysDown = new ArrayList<Integer>();
	private Vector3 tmpVector = new Vector3();
	private Stage stage;
	private OrthographicCamera camera;
	private UI ui;
	private WorldGenerator worldGenerator;
	public Vector3 mouseWorldPosition = new Vector3();
	private int windowWidth;
	private int windowHeight;

	private Vector3 lastTouchPosition = new Vector3();
	private Vector3 firstTouchPosition = new Vector3();

	private int mouseButtonDown = -1;

	private CommandDrawRectangle commandRect = new CommandDrawRectangle(0,0,0,0);


	public InputController(Stage stage, OrthographicCamera camera,
						   UI ui, WorldGenerator worldGenerator,
						   LwjglApplicationConfiguration cfg) {
		this.stage = stage;
		this.camera = camera;
		this.ui = ui;
		this.worldGenerator = worldGenerator;
		windowWidth = cfg.width;
		windowHeight = cfg.height;
	}

	@Override
	public boolean keyDown(int keycode) {
		//Gdx.app.log("KeyDown ", keycode + "");

		if (keycode == 66) {
			Editor.post(new CommandGenerateWorld(true));
		}

		if (keycode == 47) {
			Editor.post(new CommandSnapshot());
		}

		if (!keysDown.contains(keycode)) {
			keysDown.add(keycode);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keysDown.contains(keycode)) {
			keysDown.remove(new Integer(keycode));
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		lastTouchPosition.set(screenX, screenY, 0);
		firstTouchPosition = new Vector3(screenX, screenY, 0);
		mouseButtonDown = button;
		stage.setScrollFocus(null);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		stage.setKeyboardFocus(null);
		if (mouseButtonDown == 0 && firstTouchPosition != null) {
			Editor.post(new CommandDrawMap(firstTouchPosition.x, firstTouchPosition.y, screenX, screenY));
		}
		firstTouchPosition = null;
		mouseButtonDown = -1;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (mouseButtonDown == 1) {
			moveCamera(screenX, screenY);
		}
		if (mouseButtonDown == 0 && firstTouchPosition != null) {
			commandRect.x1 = firstTouchPosition.x;
			commandRect.y1 = firstTouchPosition.y;
			commandRect.x2 = screenX;
			commandRect.y2 = screenY;
			com.nukethemoon.tools.opusproto.editor.app.Editor.post(commandRect);
		}
		return false;
	}


	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (ui != null) {
					tmpVector.set(screenX, screenY, 0);
					camera.unproject(tmpVector, 0, 0, windowWidth, windowHeight);
					mouseWorldPosition.set(tmpVector.x, tmpVector.y, 0);
					int x = (int) tmpVector.x;
					int y = (int) tmpVector.y;
					//ui.getTopMenu().getInfoTable().setHoverValue(x, y, worldGenerator.getSampleAt(x, y, 0));
				}
			}
		});
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		camera.zoom += (amount * camera.zoom) / 10f;
		return false;
	}

	public Vector3 getMouseWorldPosition() {
		return mouseWorldPosition;
	}

	public List<Integer> getKeysDown() {
		return keysDown;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	private void moveCamera( int touch_x, int touch_y ) {
		float xOffset = (lastTouchPosition.x - touch_x) * camera.zoom;
		float yOffset = (lastTouchPosition.y - touch_y) * camera.zoom;

		camera.position.set(camera.position.x + xOffset, camera.position.y - yOffset, 0);
		camera.update();

		lastTouchPosition.set(touch_x, touch_y, 0);
	}


}
