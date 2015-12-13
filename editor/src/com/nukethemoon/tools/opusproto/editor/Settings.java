package com.nukethemoon.tools.opusproto.editor;

import java.util.ArrayList;
import java.util.List;

public class Settings {

	public List<WindowSetting> windows = new ArrayList<WindowSetting>();

	public int screenWidth = 800;
	public int screenHeight = 800;

	public boolean autoRefresh = false;

	public boolean showExitDialog = true;
	//public String openProject = "newworld";
	public String openProject = null;


	public static class WindowSetting {
		public int x;
		public int y;
		public boolean visible = true;
		public String name;
	}
}
