package com.nukethemoon.tools.opusproto.region;

public class Chunk {

	private int width;
	private int height;
	private int offsetX;
	private int offsetY;

	private float[][][] layerData;
	private float resolution;


	public Chunk(int width, int height, int offsetX, int offsetY, int layerCount, float resolution) {
		this.resolution = resolution;
		this.width = (int) (width / resolution);
		this.height = (int) (height / resolution);
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
		return getOffsetX() / width;
	}

	public int getChunkY() {
		return getOffsetY() / height;
	}

	public float[][][] getLayerData() {
		return layerData;
	}

	public float getResolution() {
		return resolution;
	}

}
