package com.nukethemoon.tools.opusproto.generator;

public class WorldConfiguration {

	public WorldConfiguration() {
		layerIds = new String[0];
	}

	public String seedString;
	public int mapSize = 30;
	public int mapsPerRegion = 3;
	public String name;
	public String[] layerIds;

	public transient double seed;

}
