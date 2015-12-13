package com.nukethemoon.tools.opusproto.gemoetry;

public class PointList extends AbstractGeometryData {

	private float[] points;

	public PointList(String id, double seed, float[] points) {
		super(id, seed);
		this.points = points;
	}

	public float[] getPoints() {
		return points;
	}
}
