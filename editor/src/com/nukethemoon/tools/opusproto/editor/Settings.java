package com.nukethemoon.tools.opusproto.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Settings {

	public List<WindowSetting> windows = new ArrayList<WindowSetting>();

	public int screenWidth = 800;
	public int screenHeight = 800;

	public boolean autoRefresh = false;

	public boolean showExitDialog = true;
	public String openProject = null;

	public static class WindowSetting {
		public int x;
		public int y;
		public boolean visible = true;
		public String name;
	}


	public static void save(Settings settings, String path) {
		Gson gson = new Gson();
		String settingsJson = gson.toJson(settings);
		byte[] layerBytes = settingsJson.getBytes(StandardCharsets.UTF_8);
		FileHandle layerFile = Gdx.files.local(path);
		layerFile.writeBytes(layerBytes, false);
	}

	public static Settings load(String path) {
		Gson gson = new Gson();
		FileHandle settingsFile = Gdx.files.local(path);
		if (settingsFile.exists()) {
			String str = new String(settingsFile.readBytes(), StandardCharsets.UTF_8);
			Settings config = gson.fromJson(str, Settings.class);
			return config;
		}
		return new Settings();
	}
}
