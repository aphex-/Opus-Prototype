package com.nukethemoon.tools.opusproto.editor.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.*;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.Config;
import com.nukethemoon.tools.opusproto.editor.InputController;
import com.nukethemoon.tools.opusproto.editor.Settings;
import com.nukethemoon.tools.opusproto.editor.message.CommandDrawMap;
import com.nukethemoon.tools.opusproto.editor.message.CommandDrawRectangle;
import com.nukethemoon.tools.opusproto.editor.message.CommandGenerateWorld;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenSaveAs;
import com.nukethemoon.tools.opusproto.editor.message.CommandRefreshLayout;
import com.nukethemoon.tools.opusproto.editor.message.CommandRenameElement;
import com.nukethemoon.tools.opusproto.editor.message.CommandSaveProject;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshot;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotBySampler;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotChangeSettings;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotDelete;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandCreateInterpreter;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandDeleteInterpreter;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.EventInterpreterPoolChanged;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandCreateLayer;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandDeleteLayer;
import com.nukethemoon.tools.opusproto.editor.message.layer.EventLayersChanged;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandCreateSampler;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandDeleteSampler;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.EventSamplerPoolChanged;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.UI;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.AbstractSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.AContinentForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.CombinedSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.FlatSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.MaskedSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.noise.NoiseSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.windows.SamplerEditor;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.exceptions.SamplerRecursionException;
import com.nukethemoon.tools.opusproto.generator.ChunkListener;
import com.nukethemoon.tools.opusproto.generator.OpusConfiguration;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.interpreter.TypeInterpreter;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreter;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.layer.LayerConfig;
import com.nukethemoon.tools.opusproto.loader.json.OpusLoaderJson;
import com.nukethemoon.tools.opusproto.noise.AbstractNoiseAlgorithm;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.noise.algorithms.CellNoise;
import com.nukethemoon.tools.opusproto.noise.algorithms.DiamondSquare;
import com.nukethemoon.tools.opusproto.noise.algorithms.SimplexNoise;
import com.nukethemoon.tools.opusproto.region.Chunk;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.acontinent.AContinent;
import com.nukethemoon.tools.opusproto.sampler.combined.Combined;
import com.nukethemoon.tools.opusproto.sampler.flat.FlatSampler;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSampler;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.noise.NoiseConfig;
import com.nukethemoon.tools.opusproto.sampler.noise.NoiseSampler;
import com.nukethemoon.tools.opusproto.log.Log;
import com.nukethemoon.tools.simpletask.SimpleTaskExecutor;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Editor implements ApplicationListener, ChunkListener {

	public static Map<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerForm>> KNOWN_EDITORS;
	public static Map<String, Class<? extends AbstractNoiseAlgorithm>> KNOWN_ALGORITHMS = new HashMap<String, Class<? extends AbstractNoiseAlgorithm>>();

	private static LwjglApplicationConfiguration cfg;
	private UI ui;

	private Algorithms algorithms;
	private Samplers samplers;
	private Opus opus;

	private static Bus bus = new Bus(ThreadEnforcer.ANY);

	public static final String DEFAULT_SAMPLER_NAME = 		"EditorDefaultSampler";
	public static final String DEFAULT_MASK_SAMPLER_NAME = 	"EditorDefaultMask";
	public static final String DEFAULT_INTERPRETER_NAME = 	"EditorDefaultInterpreter";
	public static final String DEFAULT_LAYER_NAME = 		"EditorDefaultLayer";

	public static Stage STAGE;

	private OpusLoaderJson fileOperation;
	private InputMultiplexer inputMultiplexer;
	private InputController inputController;
	private float performanceFactor = 1;

	private static Settings settings;
	private Image splashScreen;
	private OpusRenderer renderer;

	private CommandDrawRectangle commandDrawRectangle;

	public static void main(String [] args)	{
		cfg = new LwjglApplicationConfiguration();
		cfg.title = Config.NAME;
		cfg.width = 1300;
		cfg.height = 800;
		cfg.samples = 0;
		new EditorApplication(new Editor(), cfg);
	}

	public static void post(Object o) {
		bus.post(o);
	}

	@Override
	public void create() {
		bus.register(this);

		Styles.init();
		settings = Settings.load(Config.SETTINGS_FILE);
		cfg.width = settings.screenWidth;
		cfg.height = settings.screenHeight;

		STAGE = new Stage(new ScreenViewport());
		renderer = new OpusRenderer(cfg.width,  cfg.height, STAGE);

		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(STAGE);
		Gdx.input.setInputProcessor(inputMultiplexer);


		KNOWN_EDITORS = new HashMap<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerForm>>();
		KNOWN_EDITORS.put(NoiseSampler.class, 		NoiseSamplerForm.class);
		KNOWN_EDITORS.put(FlatSampler.class, 		FlatSamplerForm.class);
		KNOWN_EDITORS.put(Combined.class, 			CombinedSamplerForm.class);
		KNOWN_EDITORS.put(MaskedSampler.class, 		MaskedSamplerForm.class);
		KNOWN_EDITORS.put(AContinent.class, 		AContinentForm.class);

		KNOWN_ALGORITHMS.put(SimplexNoise.class.getSimpleName(), 	SimplexNoise.class);
		KNOWN_ALGORITHMS.put(DiamondSquare.class.getSimpleName(), 	DiamondSquare.class);
		KNOWN_ALGORITHMS.put(CellNoise.class.getSimpleName(), 		CellNoise.class);

		Log.out = new Log.Out() {
			@Override
			public void logError(String tag, String message) {
				Gdx.app.error(tag, message);
				showErrorDialog(message);
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

		setExitDialog();

		if (!Config.DEBUG) {
			showSplashScreenAnimation(new AnimationFinishedListener() {
				@Override
				public void onAnimationFinished(BaseAnimation baseAnimation) {
					openProjectDialog();
				}
			});
		} else {
			openProjectDialog();
		}

	}

	private void showSplashScreenAnimation(AnimationFinishedListener listener) {
		splashScreen = new Image(new Texture(Config.IMAGE_PATH_SPLASH));
		splashScreen.setPosition(cfg.width / 2 - splashScreen.getWidth() / 2,
				cfg.height / 2 - splashScreen.getHeight() / 2);
		STAGE.addActor(splashScreen);
		renderer.getAni().add(new BaseAnimation(1500, listener) {
			@Override
			protected void onProgress(float v) {
				Color imageColor = splashScreen.getColor();
				imageColor.a = v;
				splashScreen.setColor(imageColor);
			}
		});
	}

	private void openProjectDialog() {
		if (!Gdx.files.local(Config.PROJECT_PATH + settings.openProject).exists()) {
			ProjectDialog dialog = new ProjectDialog(Styles.UI_SKIN, STAGE);
			dialog.show(STAGE);
			dialog.setResultListener(new BaseDialog.ResultListener() {
				@Override
				public void onResult(Object result) {
					init((String) result);
				}
			});
		} else {
			init(settings.openProject);
		}
	}


	public static void openHelp(String filename) {
		File htmlFile = new File(filename);
		try {
			Desktop.getDesktop().browse(htmlFile.toURI());
		} catch (IOException e) {
			showException(e, "Error opening help file " + filename);
		}
	}

	public static void openNTMPage() {
		try {
			URL url = new URL(Config.NTM_URL);
			Desktop.getDesktop().browse(url.toURI());
		} catch (Exception e) {
			showException(e, "Error opening file " + Config.NTM_URL);
		}
	}

	private void setExitDialog() {
		if (settings.showExitDialog && !Config.DEBUG) {
			Dialog exitDialog = new Dialog("Confirm Exit", Styles.UI_SKIN)	{
				@Override
				protected void result(Object object)
				{
					if (object.equals(true))
					{
						((ExitDialogInterface) Gdx.app).exitConfirmed();
					} else
					{
						((ExitDialogInterface) Gdx.app).exitCanceled();
					}
				}
			};
			exitDialog.button("Yes", true);
			exitDialog.button("Cancel", false);
			((ExitDialogInterface) Gdx.app).setExitDialog(exitDialog, STAGE);
		}
	}

	private void init(String projectName) {

		if (splashScreen != null) {
			splashScreen.remove();
		}

		fileOperation = new OpusLoaderJson();
		algorithms = new Algorithms();
		samplers = new Samplers();

		samplers.addInterpreter(new ColorInterpreter(DEFAULT_INTERPRETER_NAME));

		try {
			String projectFile = Config.PROJECT_PATH + projectName + Config.SAVE_FILE_NAME;
			if (Gdx.files.local(projectFile).exists()) {

				byte[] bytes = fileOperation.readFile(projectFile);
				opus = fileOperation.load(samplers, algorithms, bytes);
				createEditorDefaultSampler(samplers, algorithms, opus.getConfig().seed);
			} else {
				// initialize a new project
				OpusConfiguration newOpusConfig = new OpusConfiguration();

				createEditorDefaultSampler(samplers, algorithms, newOpusConfig.seed);

				newOpusConfig.name = projectName;
				newOpusConfig.layerIds = new String[]{DEFAULT_LAYER_NAME};
				LayerConfig baseLayerConfig = new LayerConfig(DEFAULT_LAYER_NAME);
				baseLayerConfig.interpreterId = DEFAULT_INTERPRETER_NAME;
				baseLayerConfig.samplerItems = new ChildSamplerConfig[] {
					new ChildSamplerConfig(DEFAULT_MASK_SAMPLER_NAME)
				};
				Layer baseLayer = new Layer(baseLayerConfig, newOpusConfig.seed, samplers);
				opus = new Opus(newOpusConfig, new Layer[]{baseLayer});
			}
		} catch (Exception e) {
			showException(e, "Error initializing project.");
			return;
		}


		opus.addChunkListener(this);
		ui = new UI(STAGE, cfg, opus, algorithms, samplers, bus, Styles.UI_SKIN, settings);
		bus.register(ui);
		requestFirstChunks(opus);
		renderer.setChunkRendering(true);
		inputController = new InputController(STAGE, renderer.getCamera(), ui, opus, cfg);
		inputMultiplexer.addProcessor(inputController);
		renderer.setInputController(inputController);
		renderer.setClearColor(Color.BLACK);
	}

	private void createEditorDefaultSampler(Samplers sam, Algorithms alg, double opusSeed) {
		try {
			sam.addSampler(
					new NoiseSampler(new NoiseConfig(DEFAULT_SAMPLER_NAME),
							opusSeed, alg, sam));

			MaskedSamplerConfig maskedSamplerConfig = new MaskedSamplerConfig(DEFAULT_MASK_SAMPLER_NAME);
			maskedSamplerConfig.samplerItems = new ChildSamplerConfig[2];
			maskedSamplerConfig.samplerItems[0] = new ChildSamplerConfig(DEFAULT_SAMPLER_NAME);
			maskedSamplerConfig.samplerItems[1] = new ChildSamplerConfig(DEFAULT_SAMPLER_NAME);
			sam.addSampler(
					new MaskedSampler(maskedSamplerConfig, opusSeed, alg, sam)
			);
		} catch (SamplerInvalidConfigException e) {
			showException(e, "Error loading editor standard sampler.");
			return;
		}
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void save(CommandSaveProject command) {
		try {
			fileOperation.save(samplers, opus, Config.PROJECT_PATH + opus.getConfig().name + Config.SAVE_FILE_NAME);
		} catch (Exception e) {
			showException(e, "Error saving project ");
		}
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void saveAs(CommandOpenSaveAs command) {
		SaveAsDialog saveAsDialog = new SaveAsDialog(Styles.UI_SKIN);
		saveAsDialog.setResultListener(new BaseDialog.ResultListener() {
			@Override
			public void onResult(Object result) {
				String name = (String) result;
				if (isValidName(name)) {
					if (!Gdx.files.local(Config.PROJECT_PATH  + name).exists()) {
						String saveAsFile = Config.PROJECT_PATH  + name + Config.SAVE_FILE_NAME;
						String originName = opus.getConfig().name;
						opus.getConfig().name = name;
						try {
							Gdx.files.local(Config.PROJECT_PATH  + name).mkdirs();
							fileOperation.save(samplers, opus, saveAsFile);
						} catch (IOException e) {
							showException(e);
							opus.getConfig().name = originName;
							return;
						}
						showSuccessDialog("Successfully saved as " + saveAsFile);
					} else {
						showErrorDialog("Can not save as " + name + ". A project with this name exists already.");
					}
				} else {
					showErrorDialog("Can not save as " + name + ". Invalid name.");
				}
			}
		});
		saveAsDialog.show(STAGE);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void createNewLayer(CommandCreateLayer command) {

		try {
			LayerConfig config = new LayerConfig(command.layerId);
			config.interpreterId = DEFAULT_INTERPRETER_NAME;
			Layer layer = new Layer(config, opus.getConfig().seed, samplers);
			renderer.putLayerSprite(layer, new ArrayList<Sprite>());
			opus.getLayers().add(layer);
			Editor.post(new EventLayersChanged());

		} catch (SamplerInvalidConfigException e) {
			showException(e, "Error creating layer " + command.layerId);
			return;
		}

		/*if (command.layerId == null || command.layerId.length() == 0) {
			ErrorDialog errorDialog = new ErrorDialog("Can not add layer. Invalid layer name.", UI_SKIN);
			errorDialog.show(STAGE);
			return;
		}

		for (int i = 0; i < worldGenerator.getLayers().size(); i++) {
			Layer layer = worldGenerator.getLayers().get(i);
			if (layer.getConfig().id.equals(command.layerId)) {
				ErrorDialog errorDialog = new ErrorDialog("Can not add layer. Name '" + command.layerId + "' is already used.", UI_SKIN);
				errorDialog.show(STAGE);
				return;
			}
		}

		Layer.LayerConfig config = new Layer.LayerConfig(command.layerId);
		config.samplerItems = new Layer.SamplerItemConfig[0];
		config.maskItems = new Layer.SamplerItemConfig[0];
		Layer l = new Layer(config, worldConfiguration.seed, samplerLoader);
		worldGenerator.getLayers().add(l);
		ui.getWorldEditor().reload();
		Editor.post(new CommandOpenLayerEditor(l.getConfig().id));*/
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void update(CommandGenerateWorld command) {
		if (command.force || settings.autoRefresh) {
			opus.clear();
			renderer.dispose();
			requestFirstChunks(opus);
		}
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void deleteLayer(CommandDeleteLayer command) {
		Layer layerToDelete = null;
		for (Layer l : opus.getLayers()) {
			if (l.getConfig().id.equals(command.layerId)) {
				layerToDelete = l;
			}
		}
		opus.getLayers().remove(layerToDelete);
		Editor.post(new EventLayersChanged());
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void clearSnapShot(CommandSnapshotDelete command) {
		renderer.clearSnapshot();
	}


	@Subscribe
	@SuppressWarnings("unused")
	public void takeSnapshot(CommandSnapshotBySampler command) {
		AbstractSampler sampler = samplers.getSampler(command.id);
		takeSnapshot(sampler, command.drawBehind, command.opacity);
	}



	@Subscribe
	@SuppressWarnings("unused")
	public void drawRect(CommandDrawRectangle command) {
		commandDrawRectangle = command;
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void endRect(CommandDrawMap command) {
		List<int[]> positions = renderer.getChunkPositionsInRect(command.x1, command.y1,
				command.x2, command.y2,
				opus.getConfig().chunkSize, performanceFactor);
		requestChunks(positions);

		commandDrawRectangle = null;
	}

	/**
	 * Requests the chunks with assigned coordinates.
	 * @param coordinates A list of integer arrays with x and y
	 */
	private void requestChunks(List<int[]> coordinates) {
		int[] regionX = new int[coordinates.size()];
		int[] regionY = new int[coordinates.size()];
		for (int i = 0; i < coordinates.size(); i++) {
			regionX[i] = coordinates.get(i)[0];
			regionY[i] = coordinates.get(i)[1];
		}
		requestChunks(regionX, regionY);
	}

	private void takeSnapshot(AbstractSampler sampler, boolean drawBehind, float opacity) {
		if (opacity == 0) {
			renderer.clearSnapshot();
			return;
		}
		ui.getSnapshotTable().setVisible(true);
		renderer.createSnapshot(drawBehind, sampler, opacity);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void changeSnapshotOrder(CommandSnapshotChangeSettings command) {
		renderer.setSnapshotBehind(command.drawBehind);
		renderer.setSnapshotAlpha(command.opacity);
	}

	private void requestFirstChunks(Opus op) {
		int startSize = 4;
		renderer.dispose();
		renderer.initLayerSprite(op.getLayers());
		List<int[]> positions = new ArrayList<int[]>();
		for (int mx = -startSize; mx < startSize; mx ++) {
			for (int my = -startSize; my < startSize; my ++) {
				double distance = Math.sqrt(Math.pow(0 - mx, 2) + Math.pow(0 - my, 2));
				if (distance <= startSize) {
					positions.add(new int[]{mx, my});
				}
			}
		}
		requestChunks(positions);
	}

	private void requestChunks(int x[], int y[]) {
		try {
			opus.requestChunks(x, y);
		} catch (Exception e) {
			showException(e, "Error requesting chunk.");
		}
	}

	@Override
	public void onChunkCreated(int regionX, int regionY, Chunk chunk) {
		addSprite(chunk);
	}

	private void addSprite(final Chunk chunk) {
		if (chunk == null) {
			return;
		}

		SimpleTaskExecutor<Pixmap> executor = new SimpleTaskExecutor<Pixmap>();

		for (int layerIndex = 0; layerIndex < opus.getLayers().size(); layerIndex++) {
			final int finalLayerIndex = layerIndex;
			executor.addTask(new Callable<Pixmap>() {
				@Override
				public Pixmap call() throws Exception {
					return createPixmap(chunk.getLayerData()[finalLayerIndex], finalLayerIndex, chunk.getResolution());
				}
			}, new SimpleTaskExecutor.ResultListener<Pixmap>() {
				@Override
				public void onResult(Pixmap result) {
					Sprite sprite = new Sprite(new Texture(result));
					result.dispose();
					sprite.setPosition(chunk.getOffsetX(), chunk.getOffsetY());
					Layer layer = opus.getLayers().get(finalLayerIndex);
					renderer.addSprite(layer, sprite);
				}
			});
		}

		try {
			executor.execute();
		} catch (Exception e) {
			showException(e, "Error creating chunks.");
		}
	}



	public Pixmap createPixmap(float data[][], int layerIndex, float resolution) {
		TypeInterpreter interpreter = opus.getLayers().get(layerIndex).getInterpreter();
		Pixmap pixmap = new Pixmap(
				(opus.getConfig().chunkSize),
				(opus.getConfig().chunkSize),
				Pixmap.Format.RGBA8888);

		pixmap.setColor(Color.BLACK);
		for (int x = 0; x < opus.getConfig().chunkSize; x++) {
			for (int y = 0; y < opus.getConfig().chunkSize; y++) {
				float noise = data[x][(opus.getConfig().chunkSize - 1) - y];
				int rgb888 = interpreter.getType(noise);
				if (rgb888 > 0) {
					pixmap.drawPixel(
							(x), (y), ColorInterpreter.toRGBA888(rgb888, 255));
				}
			}
		}
		return pixmap;
	}

	@Override
	public void resize(int width, int height) {
		renderer.setWindowWidth(width);
		renderer.setWindowHeight(height);
		if (inputController != null) {
			inputController.setWindowWidth(width);
			inputController.setWindowHeight(height);
		}
		updateWindowSize(null);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void updateWindowSize(CommandRefreshLayout c) {
		STAGE.getViewport().update(renderer.getWindowWidth(), renderer.getWindowHeight(), true);
		if (ui != null) {
			ui.updatePosition(renderer.getWindowWidth(), renderer.getWindowHeight());
		}
		renderer.getCamera().viewportWidth = renderer.getWindowWidth();
		renderer.getCamera().viewportHeight = renderer.getWindowHeight();
		renderer.getCamera().update();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void deleteSampler(CommandDeleteSampler command) {
		if (command.samplerId.equals(DEFAULT_MASK_SAMPLER_NAME) || command.samplerId.equals(DEFAULT_SAMPLER_NAME)) {
			showErrorDialog("You can not delete this sampler. It is a default layer for this editor.");
			return;
		}

		try {
			List<String> dependencies = new ArrayList<String>();
			if (samplers.doesASamplerDependOn(samplers.getSampler(command.samplerId), dependencies)) {
				showDependencyError(dependencies, "Sampler");
				return;
			}
		} catch (SamplerRecursionException e) {
			showException(e, "Error deleting sampler " + command.samplerId);
			return;
		}

		try {
			List<String> dependencies = new ArrayList<String>();
			if (samplers.doesASamplerDependOn(samplers.getSampler(command.samplerId),
					toAbstract(opus.getLayers()), dependencies, 0)) {
				showDependencyError(dependencies, "Layer");
				return;
			}

			samplers.removeSampler(command.samplerId);
			Editor.post(new EventSamplerPoolChanged());

		} catch (SamplerRecursionException e) {
			showException(e, "Error deleting sampler " + command.samplerId);
			return;
		}
	}

	private static void showDependencyError(List<String> dependencies, String type) {
		String errorMessage = "Can not delete this sampler. A " + type + " depends on it.\n";
		for (String dependency : dependencies) {
			errorMessage += dependency + "\n";
		}
		ErrorDialog errorDialog = new ErrorDialog(errorMessage, Styles.UI_SKIN);
		errorDialog.show(STAGE);
	}

	public static List<AbstractSampler> toAbstract(List<Layer> layer) {
		List<AbstractSampler> list = new ArrayList<AbstractSampler>(layer.size());
		for (Layer l : layer) {
			list.add(l);
		}
		return list;
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void deleteInterpreter(CommandDeleteInterpreter command) {
		if (command.id.equals(DEFAULT_INTERPRETER_NAME)) {
			showErrorDialog("You can not delete the default interpreter.");
		}

		if (samplers.doesALayerDependOn(samplers.getInterpreter(command.id), opus.getLayers())) {
			showErrorDialog("You can not delete this interpreter. It is used in a layer.");
			return;
		}
		samplers.removeInterpreter(command.id);
		Editor.post(new EventInterpreterPoolChanged());
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void takeSnapshot(CommandSnapshot command) {
		if (ui != null) {
			List<Window> windows = ui.getWindows();
			for (int i = 0; i < windows.size(); i++) {
				Window window = windows.get(i);
				if (window instanceof SamplerEditor) {
					if (window.isVisible()) {
						SamplerEditor e = ((SamplerEditor) window);
						String selectedSamplerId = e.getSelectedSamplerId();
						if (selectedSamplerId != null) {
							Editor.post(new CommandSnapshotBySampler(selectedSamplerId, 1, false));
						}
					}
				}
			}
		}

	}

	@Subscribe
	@SuppressWarnings("unused")
	public void createNewSampler(CommandCreateSampler command) {

		AbstractSampler s = samplers.getSampler(command.name);
		if (s != null) {
			ErrorDialog errorDialog = new ErrorDialog("Can not create a sampler with the id " + command.name + ". This id is already used.", Styles.UI_SKIN);
			errorDialog.show(STAGE);
			return;
		}

		Class<? extends AbstractSamplerConfiguration> configClass = null;
		for (java.util.Map.Entry<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerConfiguration>> entry : Samplers.SAMPLER_TO_CONFIG.entrySet()) {
			if (entry.getKey().getSimpleName().toLowerCase().equals(command.type.toLowerCase())) {
				configClass = entry.getValue();
			}
		}

		if (configClass != null) {
			try {
				AbstractSamplerConfiguration o = (AbstractSamplerConfiguration) configClass.getConstructors()[0].newInstance(command.name);
				AbstractSampler sampler = Samplers.create(o, opus.getConfig().seed, algorithms, samplers);
				samplers.addSampler(sampler);
				Editor.post(new EventSamplerPoolChanged());
				Editor.post(new CommandOpenSamplerEditor(sampler.getConfig().id));
			} catch (Exception e) {
				showException(e, "Error creating sampler.");
				return;
			}
		} else {
			showErrorDialog( "Can not find the config class for the sampler " + command.type);
		}
	}

	@Override
	public void render() {
		int mapSize = -1;
		List<Layer> layers = null;
		if (opus != null) {
			mapSize = opus.getConfig().chunkSize;
			layers = opus.getLayers();
		}

		if (renderer != null) {
			renderer.render(mapSize, layers);
			if (commandDrawRectangle != null) {
				renderer.drawSelectionRect(commandDrawRectangle.x1,
						commandDrawRectangle.y1, commandDrawRectangle.x2,
						commandDrawRectangle.y2);
			}
		}
	}


	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		Settings settings = new Settings();
		settings.screenHeight = renderer.getWindowHeight();
		settings.screenWidth = renderer.getWindowWidth();
		if (ui != null) {
			settings.windows = ui.createWindowSettings();
		}
		if (fileOperation != null) {
			Settings.save(settings, Config.SETTINGS_FILE);
		}
		renderer.dispose();
	}


	public static boolean isValidName(String name) {
		return (name != null && name.length() > 0 && name.length() < 20);
	}

	public static void showErrorDialog(String text) {
		ErrorDialog errorDialog = new ErrorDialog(text, Styles.UI_SKIN);
		errorDialog.show(STAGE);
	}

	public static void showSuccessDialog(String text) {
		SuccessDialog errorDialog = new SuccessDialog(text, Styles.UI_SKIN);
		errorDialog.show(STAGE);
	}

	private static void showException(Exception e) {
		showException(e, "");
	}

	private static void showException(Exception e, String extraText) {
		Log.e(Editor.class, e.getMessage());
		showErrorDialog(extraText + " " + e.getClass().getSimpleName()
				+ ": " + e.getMessage());
		e.printStackTrace();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void renameElement(CommandRenameElement command) {
		if (!isValidName(command.newName)) {
			showErrorDialog("The name is not valid.");
			return;
		}
		if (command.type == CommandRenameElement.ElementType.Sampler) {
			if (samplers.getSampler(command.newName) != null) {
				showErrorDialog("A sampler with the name " + command.newName + " exists already.");
				return;
			}
			if (command.oldName.equals(DEFAULT_MASK_SAMPLER_NAME) || command.oldName.equals(DEFAULT_SAMPLER_NAME)) {
				showErrorDialog("You can not rename this sampler. It is a default layer for this editor.");
				return;
			}
			samplers.changeId(command.oldName, command.newName);
			for (Layer layer : opus.getLayers()) {
				samplers.changeId(layer, command.oldName, command.newName);
				try {
					layer.init();
				} catch (SamplerInvalidConfigException e) {
					showException(e, "Error renaming layer child.");
					return;
				}
			}
			Editor.post(new EventSamplerPoolChanged());
		}

		if (command.type == CommandRenameElement.ElementType.Layer) {
			if (opus.getLayer(command.newName) != null) {
				showErrorDialog("A layer with the name " + command.newName + " exists already.");
				return;
			}
			Layer layer = opus.getLayer(command.oldName);
			layer.getConfig().id = command.newName;
			Editor.post(new EventLayersChanged());
		}

		if (command.type == CommandRenameElement.ElementType.Interpreter) {
			if (command.oldName.equals(DEFAULT_INTERPRETER_NAME)) {
				showErrorDialog("You can not rename the default interpreter.");
				return;
			}
			samplers.changeInterpreterId(command.oldName, command.newName, opus.getLayers());
			Editor.post(new EventInterpreterPoolChanged());
		}
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void createInterpreter(CommandCreateInterpreter command) {
		if (samplers.getInterpreter(command.id) != null) {
			showErrorDialog("Can not create interpreter. The id '" + command.id + "' is already used.");
		} else {
			if (isValidName(command.id)) {
				samplers.addInterpreter(new ColorInterpreter(command.id));
				Editor.post(new EventInterpreterPoolChanged());
			} else {
				showErrorDialog("Invalid name '" + command.id + "'");
			}
		}
	}
}
