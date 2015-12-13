package com.nukethemoon.tools.opusproto.generator.region;

public class Mask {

	private final int regionX;
	private final int regionY;
	private float[] polygon;

	// merge priority by distance to world origin

	public Mask(int regionX, int regionY, float[] polygon) {
		this.regionX = regionX;
		this.regionY = regionY;
		this.polygon = polygon;
	}

	public int getRegionX() {
		return regionX;
	}

	public int getRegionY() {
		return regionY;
	}

	public float[] getPolygon() {
		return polygon;
	}
}
