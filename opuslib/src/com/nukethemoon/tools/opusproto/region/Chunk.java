package com.nukethemoon.tools.opusproto.region;

public class Chunk {

	private int width;
	private int height;

	private int originalWidth; // width without resolution
	private int originalHeight; // height without resolution

	private int offsetX;
	private int offsetY;

	private float[][][] layerData;
	private float resolution;



	private int overlap;

	public Chunk(int width, int height, int offsetX, int offsetY, int layerCount, float resolution, int overlap) {
		this.resolution = resolution;
		this.originalHeight = height;
		this.originalWidth = width;
		this.overlap = overlap;
		this.width = Math.round((width ) / resolution);
		this.height = Math.round((height) / resolution);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		layerData = new float[layerCount][this.width][this.height];
	}

	public float getAbsolute(int pX, int pY, int layerIndex) {
		return getRelative(pX - offsetX, pY - offsetY, layerIndex);
	}

	public float getRelative(int pX, int pY, int layerIndex) {
		return layerData[layerIndex][pX][pY];
	}

	public void setAbsolute(int pX, int pY, int layerIndex, float value) {
		setRelative(pX - offsetX, pY - offsetY, layerIndex, value);
	}

	public void setRelative(int pX, int pY, int layerIndex, float value) {
		layerData[layerIndex][pX][pY] = value;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getChunkX() {
		return getOffsetX() / originalWidth;
	}

	public int getChunkY() {
		return getOffsetY() / originalHeight;
	}

	public float[][][] getLayerData() {
		return layerData;
	}

	public float getResolution() {
		return resolution;
	}

	public int getOriginalWidth() {
		return originalWidth;
	}

	public int getOriginalHeight() {
		return originalHeight;
	}

	public int getOverlap() {
		return overlap;
	}
}
