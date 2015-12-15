package com.nukethemoon.tools.opusproto.loader.json;

import com.google.gson.JsonElement;
import com.nukethemoon.tools.opusproto.generator.WorldConfiguration;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreter;
import com.nukethemoon.tools.opusproto.layer.LayerConfig;


public class WorldSave {

	public String version;

	public SamplerConfigEntry[] samplerConfigs;

	public ColorInterpreter[] interpreters;

	public LayerConfig[] layerConfigs;

	public WorldConfiguration worldConfig;


	public static class SamplerConfigEntry {
		public String type;
		public JsonElement data;

		public SamplerConfigEntry(String type, JsonElement configJson) {
			this.type = type;
			this.data = configJson;
		}
	}
}
