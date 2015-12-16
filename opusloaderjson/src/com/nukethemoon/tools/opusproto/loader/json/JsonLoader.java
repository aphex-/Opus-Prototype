package com.nukethemoon.tools.opusproto.loader.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nukethemoon.tools.opusproto.Samplers;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.exceptions.SamplerRecursionException;
import com.nukethemoon.tools.opusproto.exceptions.SamplerUnresolvedDependencyException;
import com.nukethemoon.tools.opusproto.generator.WorldConfiguration;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class initializes Opus by a json file.
 */
public class JsonLoader {

	private Gson gson;
	private static Charset CHARSET = StandardCharsets.UTF_8;

	/**
	 * Creates a new instance.
	 */
	public JsonLoader() {
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	/**
	 * Loads Opus by a json file located
	 * at the assigned relative path.
	 * @param filePath The relative path to the json file.
	 * @return The initialized WorldGenerator.
	 * @throws SamplerInvalidConfigException
	 * @throws SamplerUnresolvedDependencyException
	 * @throws SamplerRecursionException
	 * @throws IOException
	 */
	public Opus load(String filePath) throws SamplerInvalidConfigException, SamplerUnresolvedDependencyException, SamplerRecursionException, IOException {
		return load(new Samplers(), new Algorithms(), filePath);
	}

	/**
	 * Loads Opus by a json file located
	 * at the assigned relative path. An instance of SamplerLoader
	 * and NoiseAlgorithmPool can be passed if they
	 * are initialized before.
	 * @param samplers The Samplers to use.
	 * @param algorithms The Algorithms to use.
	 * @param filePath The relative path to the json file.
	 * @return The initialized WorldGenerator.
	 * @throws SamplerInvalidConfigException
	 * @throws SamplerUnresolvedDependencyException
	 * @throws SamplerRecursionException
	 * @throws IOException
	 */
	public Opus load(Samplers samplers, Algorithms algorithms, String filePath)
			throws SamplerInvalidConfigException, SamplerUnresolvedDependencyException,
			SamplerRecursionException, IOException {

		samplers = samplers == null ? new Samplers() : samplers;
		algorithms = algorithms == null ? new Algorithms() : algorithms;
		byte[] bytes = Files.readAllBytes(Paths.get(filePath));
		WorldSave save = gson.fromJson(new String(bytes, CHARSET), WorldSave.class);
		WorldConfiguration worldConfiguration = save.worldConfig;
		worldConfiguration.seed = Double.parseDouble(worldConfiguration.seedString);
		AbstractSamplerConfiguration[] samplerConfigList = new AbstractSamplerConfiguration[save.samplerConfigs.length];
		for (int i = 0; i < save.samplerConfigs.length; i++) {
			WorldSave.SamplerConfigEntry entry = save.samplerConfigs[i];
			samplerConfigList[i] = gson.fromJson(entry.data, Samplers.getConfigClassByName(entry.type));
		}
		samplers.loadSamplers(samplerConfigList, worldConfiguration.seed, algorithms);
		for (int i = 0; i < save.interpreters.length; i++) {
			samplers.addInterpreter(save.interpreters[i]);
		}
		Layer[] layerList = new Layer[save.layerConfigs.length];
		for (int i = 0; i < save.layerConfigs.length; i++) {
			layerList[i] = new Layer(save.layerConfigs[i], worldConfiguration.seed, samplers);
		}
		return new Opus(worldConfiguration, layerList);
	}

	public void save(Samplers samplers, Opus opus) {

		/*if (worldGenerator.getConfig().name == null) {
			worldGenerator.getConfig().name = saveFilePath;
		}

		if (!worldGenerator.getConfig().name.equals(saveFilePath)) {
			// project was renamed
			FileHandle local = Gdx.files.local(Config.PROJECT_PATH + saveFilePath);
			File file = new File(Config.PROJECT_PATH + worldGenerator.getConfig().name);
			local.file().renameTo(file);
			saveFilePath = worldGenerator.getConfig().name;
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

		//deleteUnused(samplerLoader, worldGenerator);*/
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

	public void saveAs(Samplers samplers, Opus opus, String name) {
		/*saveFilePath = name;
		worldGenerator.getConfig().name = name;
		save(samplerLoader, worldGenerator);*/
	}





}
