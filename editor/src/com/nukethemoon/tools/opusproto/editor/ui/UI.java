package com.nukethemoon.tools.opusproto.editor.ui;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.nukethemoon.tools.opusproto.Samplers;
import com.nukethemoon.tools.opusproto.editor.Settings;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenAboutWindow;
import com.nukethemoon.tools.opusproto.editor.message.CommandShowDependencyWindow;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandOpenInterpreterEditor;
import com.nukethemoon.tools.opusproto.editor.message.CommandLimitWindowSizes;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandOpenInterpreterList;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenProjectWindow;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerList;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenSettingsWindow;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenWorldEditor;
import com.nukethemoon.tools.opusproto.editor.ui.menu.TopMenu;
import com.nukethemoon.tools.opusproto.editor.ui.menu.tables.SnapshotTable;
import com.nukethemoon.tools.opusproto.editor.ui.windows.AboutWindow;
import com.nukethemoon.tools.opusproto.editor.ui.windows.ColorInterpreterEditor;
import com.nukethemoon.tools.opusproto.editor.ui.windows.ColorInterpreterList;
import com.nukethemoon.tools.opusproto.editor.ui.windows.DependencyWindow;
import com.nukethemoon.tools.opusproto.editor.ui.windows.LayerEditor;
import com.nukethemoon.tools.opusproto.editor.ui.windows.ProjectWindow;
import com.nukethemoon.tools.opusproto.editor.ui.windows.SamplerEditor;
import com.nukethemoon.tools.opusproto.editor.ui.windows.SamplerOverview;
import com.nukethemoon.tools.opusproto.editor.ui.windows.SettingsWindow;
import com.nukethemoon.tools.opusproto.editor.ui.windows.WorldEditor;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class UI {

	private SamplerEditor samplerEditor;

	private LayerEditor layerEditor;
	private TopMenu topMenu;

	private List<Window> windowList = new ArrayList<Window>();


	private static final int PADDING_TOP = 30;

	private Algorithms noisePool;

	private Samplers samplers;
	private Bus bus;
	private WorldEditor worldEditor;
	private Opus opus;
	private Settings settings;

	private ColorInterpreterEditor colorInterpreterEditor;


	private SamplerOverview samplerOverview;
	private LwjglApplicationConfiguration cfg;
	private ProjectWindow projectWindow;
	private SettingsWindow settingsWindow;
	private ColorInterpreterList interpreterList;
	private DependencyWindow dependencyWindow;
	private AboutWindow aboutWindow;

	public UI(final Stage pStage, LwjglApplicationConfiguration cfg,
			  Opus opus, Algorithms noisePool,
			  Samplers samplers, Bus bus, Skin uiSkin,
			  Settings settings) {
		this.cfg = cfg;
		this.opus = opus;
		this.settings = settings;
		this.bus = bus;
		this.noisePool = noisePool;
		this.samplers = samplers;



		initUI(uiSkin, opus, pStage, cfg, settings);
	}


	private void initUI(final Skin uiSkin, Opus generator,
						final Stage pStage, LwjglApplicationConfiguration cfg,
						Settings settings) {

		samplerEditor = new SamplerEditor(uiSkin, pStage, noisePool, samplers);
		samplerEditor.left().top();
		windowList.add(samplerEditor);

		layerEditor = new LayerEditor(uiSkin, generator.getLayers().get(0), pStage, samplers, generator);
		layerEditor.left().top();
		windowList.add(layerEditor);

		worldEditor = new WorldEditor(uiSkin, generator, pStage, generator.getConfig().name);
		worldEditor.left().top();
		windowList.add(worldEditor);


		samplerOverview = new SamplerOverview(uiSkin, pStage, samplers);
		samplerOverview.left().top();
		windowList.add(samplerOverview);

		projectWindow = new ProjectWindow(uiSkin, opus, samplers);
		projectWindow.left().top();
		windowList.add(projectWindow);

		interpreterList = new ColorInterpreterList(uiSkin, samplers);
		interpreterList.left().top();
		windowList.add(interpreterList);

		settingsWindow = new SettingsWindow(uiSkin);
		settingsWindow.left().top();
		windowList.add(settingsWindow);

		aboutWindow = new AboutWindow(uiSkin);
		aboutWindow.left().top();
		aboutWindow.setVisible(false);
		windowList.add(aboutWindow);

		for (Window window : windowList) {
			bus.register(window);
			pStage.addActor(window);
			Settings.WindowSetting windowSetting = getWindowSetting(window.getClass());
			if (windowSetting != null) {
				window.setPosition(windowSetting.x, windowSetting.y);
				window.setVisible(windowSetting.visible);
			}

		}

		// others
		topMenu = new TopMenu(uiSkin, layerEditor, samplerEditor, worldEditor, samplers);
		topMenu.left().top();
		pStage.addActor(topMenu);

		updatePosition(cfg.width, cfg.height);
		limitWindowSizes(null);
		topMenu.toFront();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void showDependencies(CommandShowDependencyWindow command) {
		if (dependencyWindow != null) {
			dependencyWindow.remove();
		}
		dependencyWindow = new DependencyWindow(Styles.UI_SKIN);
		dependencyWindow.showDependencies(command.id, samplers, opus.getLayers());
		Editor.STAGE.addActor(dependencyWindow);
		dependencyWindow.setVisible(true);
		dependencyWindow.toFront();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void showWorldWindow(CommandOpenWorldEditor command) {
		getWorldEditor().setVisible(true);
		getWorldEditor().toFront();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void showSamplerOverview(CommandOpenSamplerList command) {
		getSamplerOverview().setVisible(true);
		getSamplerOverview().toFront();
	}


	@Subscribe
	@SuppressWarnings("unused")
	public void showAboutWindow(CommandOpenAboutWindow command) {
		aboutWindow.setVisible(true);
		aboutWindow.toFront();
	}

	@Subscribe
	public void limitWindowSizes(CommandLimitWindowSizes command) {
		for (Window window : windowList) {
			if (window.getHeight() > cfg.height) {
				window.setHeight(cfg.height - (PADDING_TOP));
			}
		}
		topMenu.toFront();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void openProjectWindow(CommandOpenProjectWindow command) {
		projectWindow.refresh();
		projectWindow.setVisible(true);
		projectWindow.toFront();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void openInterpreterWindow(CommandOpenInterpreterEditor command) {
		if (colorInterpreterEditor != null) {
			colorInterpreterEditor.remove();
		}
		colorInterpreterEditor = new ColorInterpreterEditor(Styles.UI_SKIN, command.interpreter);
		Editor.STAGE.addActor(colorInterpreterEditor);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void openInterpreterList(CommandOpenInterpreterList command) {
		interpreterList.setVisible(true);
		interpreterList.toFront();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void openSettingsWindow(CommandOpenSettingsWindow command) {
		settingsWindow.setVisible(true);
		settingsWindow.toFront();
	}

	public void updatePosition(int width, int height) {
		//samplerEditor.setPosition(width - samplerEditor.getWidth() - PADDING_TOP, height - samplerEditor.getHeight() - DISTANCE_TOP);

		topMenu.setPosition(0, height);
		//layerEditor.setPosition(PADDING_TOP, height - layerEditor.getHeight() - DISTANCE_TOP);

		//infoTable.setPosition((width / 2) - (infoTable.getWidth() / 2), PADDING_TOP);
		//worldEditor.setPosition(PADDING_TOP, PADDING_TOP);

		//snapshotWindow.setPosition(PADDING_TOP + worldEditor.getWidth() + 10, PADDING_TOP);


		//samplerOverview.setPosition(width - samplerOverview.getWidth() - PADDING_TOP, PADDING_TOP);
		//infoTable.setPosition(100,100);
	}


	public WorldEditor getWorldEditor() {
		return worldEditor;
	}

	public LayerEditor getLayerEditor() {
		return layerEditor;
	}

	public SnapshotTable getSnapshotTable() {
		return topMenu.getSnapshotTable();
	}

	public SamplerOverview getSamplerOverview() {
		return samplerOverview;
	}

	private Settings.WindowSetting getWindowSetting(Class windowClass) {
		for (Settings.WindowSetting setting : settings.windows) {
			if (setting.name.equals(windowClass.getSimpleName())) {
				return setting;
			}
		}
		return null;
	}

	public List<Settings.WindowSetting> createWindowSettings() {
		List<Settings.WindowSetting> windowSettings = new ArrayList<Settings.WindowSetting>();
		for (Window window : windowList) {
			Settings.WindowSetting s = new Settings.WindowSetting();
			s.name = window.getClass().getSimpleName();
			s.x = (int) window.getX();
			s.y = (int) window.getY();
			s.visible = window.isVisible();
			windowSettings.add(s);
		}
		return windowSettings;
	}

	public ProjectWindow getProjectWindow() {
		return projectWindow;
	}

	public TopMenu getTopMenu() {
		return topMenu;
	}

	public List<Window> getWindows() {
		return windowList;
	}

}
