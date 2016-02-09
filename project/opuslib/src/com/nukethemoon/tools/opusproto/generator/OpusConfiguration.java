package com.nukethemoon.tools.opusproto.generator;

public class OpusConfiguration {

	public OpusConfiguration() {
		layerIds = new String[0];
	}

	public String seedString;

	public int chunkSize = 30;
	public int chunkOverlap = 0;

	public String name;
	public String[] layerIds;

	public boolean bufferChunks = true;
	public boolean bufferLayers = true;

	public transient double seed;

}
