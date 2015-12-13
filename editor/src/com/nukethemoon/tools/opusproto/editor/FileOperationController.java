package com.nukethemoon.tools.opusproto.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.exceptions.SamplerRecursionException;
import com.nukethemoon.tools.opusproto.exceptions.SamplerUnresolvedDependencyException;
import com.nukethemoon.tools.opusproto.generator.WorldConfiguration;
import com.nukethemoon.tools.opusproto.generator.WorldGenerator;
import com.nukethemoon.tools.opusproto.interpreter.AbstractInterpreter;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreter;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.layer.LayerConfig;
import com.nukethemoon.tools.opusproto.noise.NoiseAlgorithmPool;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;

public class FileOperationController {

	private Gson gson;
	private String projectName;


	public FileOperationController(String projectName) {
		gson = new GsonBuilder().setPrettyPrinting().create();
		this.projectName = projectName;
	}

	private String getSamplerPath() {
		return Config.PROJECT_PATH + projectName + "/" + Config.SUB_FOLDER_SAMPLER;
	}

	private String getLayerPath() {
		return Config.PROJECT_PATH + projectName + "/" + Config.SUB_FOLDER_LAYER;
	}

	private String getSaveFilePath() {
		return Config.PROJECT_PATH + projectName + "/" + "save" + ".json";
	}




	public WorldGenerator load(SamplerLoader loader, NoiseAlgorithmPool noisePool) throws SamplerInvalidConfigException, SamplerUnresolvedDependencyException, SamplerRecursionException {


		FileHandle saveFile = Gdx.files.local(getSaveFilePath());
		if (saveFile.exists()) {
			String str = new String(saveFile.readBytes(), StandardCharsets.UTF_8);
			Save save = gson.fromJson(str, Save.class);

			WorldConfiguration worldConfiguration = save.worldConfig;
			worldConfiguration.seed = Double.parseDouble(worldConfiguration.seedString);

			AbstractSamplerConfiguration[] samplerConfigList = new AbstractSamplerConfiguration[save.samplerConfigs.length];
			for (int i = 0; i < save.samplerConfigs.length; i++) {
				Save.SamplerConfigEntry entry = save.samplerConfigs[i];
				samplerConfigList[i] = gson.fromJson(entry.data, SamplerLoader.getConfigClassByName(entry.type));
			}

			loader.loadSamplers(samplerConfigList, worldConfiguration.seed, noisePool);

			for (int i = 0; i < save.interpreters.length; i++) {
				loader.addInterpreter(save.interpreters[i]);
			}

			Layer[] layerList = new Layer[save.layerConfigs.length];
			for (int i = 0; i < save.layerConfigs.length; i++) {
				layerList[i] = new Layer(save.layerConfigs[i], worldConfiguration.seed, loader);
			}

			return new WorldGenerator(worldConfiguration, layerList);
		}
		return new WorldGenerator(new WorldConfiguration(), new Layer[0]);

	}


	public void save(SamplerLoader samplerLoader, WorldGenerator worldGenerator) {

		if (worldGenerator.getConfig().name == null) {
			worldGenerator.getConfig().name = projectName;
		}

		if (!worldGenerator.getConfig().name.equals(projectName)) {
			// project was renamed
			FileHandle local = Gdx.files.local(Config.PROJECT_PATH + projectName);
			File file = new File(Config.PROJECT_PATH + worldGenerator.getConfig().name);
			local.file().renameTo(file);
			projectName = worldGenerator.getConfig().name;
		}

		Save save = new Save();
		AbstractInterpreter[] interpreterList = samplerLoader.createInterpreterList();
		save.interpreters = new ColorInterpreter[interpreterList.length];
		for (int i = 0; i < interpreterList.length; i++) {
			save.interpreters[i] = (ColorInterpreter) interpreterList[i];
		}


		AbstractSampler[] samplerList = samplerLoader.createSamplerList();
		save.samplerConfigs = new Save.SamplerConfigEntry[samplerList.length];
		for (int i = 0; i < samplerList.length; i++) {
			save.samplerConfigs[i] = new Save.SamplerConfigEntry(
					samplerList[i].getConfig().getClass().getSimpleName(),
					gson.toJsonTree(samplerList[i].getConfig()));
		}


		Layer[] layerList = worldGenerator.getLayers().toArray(new Layer[worldGenerator.getLayers().size()]);
		save.layerConfigs = new LayerConfig[layerList.length];
		for (int i = 0; i < layerList.length; i++) {
			save.layerConfigs[i] = (LayerConfig) layerList[i].getConfig();
		}


		// save world config
		WorldConfiguration worldConfig = worldGenerator.getConfig();
		String[] layerIds = new String[worldGenerator.getLayers().size()];
		for (int i = 0; i < layerIds.length; i++) {
			layerIds[i] = worldGenerator.getLayers().get(i).getConfig().id;
		}
		worldConfig.seedString = worldConfig.seed + "";
		worldConfig.layerIds = layerIds;
		save.worldConfig = worldConfig;
		save.version = Config.VERSION;

		String saveJson = gson.toJson(save, Save.class);
		byte[] saveBytes = saveJson.getBytes(StandardCharsets.UTF_8);
		FileHandle saveFile = Gdx.files.local(getSaveFilePath());
		saveFile.writeBytes(saveBytes, false);

		//deleteUnused(samplerLoader, worldGenerator);
	}

	private void deleteUnused(SamplerLoader samplerLoader, WorldGenerator worldGenerator) {
		FileHandle layerDir = Gdx.files.local(getLayerPath());
		FileHandle[] list = layerDir.list(JSON_FILE_FILTER);

		for (FileHandle file : list) {
			Layer layer = worldGenerator.getLayer(file.nameWithoutExtension());
			if (layer == null) {
				file.delete();
			}
		}

		FileHandle samplerDir = Gdx.files.local(getSamplerPath());
		FileHandle[] dirs = samplerDir.list(DIRECTORY_FILTER);

		for (FileHandle dir : dirs) {
			for (FileHandle file : dir.list(JSON_FILE_FILTER)) {
				AbstractSampler sampler = samplerLoader.getSampler(file.nameWithoutExtension());
				if (sampler == null) {
					file.delete();
				}
			}
		}
	}

	public static FileFilter JSON_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isFile() && "json".equals(getFileExtension(pathname.getName()));
		}
	};

	public static FileFilter DIRECTORY_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	public static String getFileExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i+1);
		}
		return "";
	}

	public void saveAs(SamplerLoader samplerLoader, WorldGenerator worldGenerator, String name) {
		projectName = name;
		worldGenerator.getConfig().name = name;
		save(samplerLoader, worldGenerator);
	}

	public static boolean projectExists(String name) {
		if (name == null || name.length() == 0) {
			return false;
		}
		return Gdx.files.local(Config.PROJECT_PATH + name).exists();
	}

	public void saveSettings(Settings settings) {
		String settingsJson = gson.toJson(settings);
		byte[] layerBytes = settingsJson.getBytes(StandardCharsets.UTF_8);
		String fullPath = getSettingsPath();
		FileHandle layerFile = Gdx.files.local(fullPath);
		layerFile.writeBytes(layerBytes, false);
	}

	public static String getSettingsPath() {
		return Config.PROJECT_PATH + "editor_settings.json";
	}

	public static Settings loadSettings() {
		Gson gson = new Gson();
		FileHandle settingsFile = Gdx.files.local(getSettingsPath());
		if (settingsFile.exists()) {
			String str = new String(settingsFile.readBytes(), StandardCharsets.UTF_8);
			Settings config = gson.fromJson(str, Settings.class);
			return config;
		}
		return new Settings();
	}
}
