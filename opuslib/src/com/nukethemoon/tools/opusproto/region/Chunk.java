package com.nukethemoon.tools.opusproto.region;

import com.nukethemoon.tools.opusproto.gemoetry.AbstractGeometryData;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

	private int width;
	private int height;
	private int offsetX;
	private int offsetY;

	private float[][][] layerData;
	private List<AbstractGeometryData> geometryData;


	public Chunk(int width, int height, int offsetX, int offsetY, int layerCount) {
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		geometryData = new ArrayList<AbstractGeometryData>();
		layerData = new float[layerCount][width][height];
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

	public AbstractGeometryData getGemoetryData(String creatorId, double seed) {
		for (AbstractGeometryData d : geometryData) {
			if (d.isBasedOn(creatorId, seed)) {
				return d;
			}
		}
		return null;
	}

	public float[][][] getLayerData() {
		return layerData;
	}

	public AbstractGeometryData getGemoetryData(int index) {
		return geometryData.get(index);
	}

	public void addGeometryData(AbstractGeometryData data) {
		geometryData.add(data);
	}

	public int getGemoetryDataCount() {
		return geometryData.size();
	}

}
