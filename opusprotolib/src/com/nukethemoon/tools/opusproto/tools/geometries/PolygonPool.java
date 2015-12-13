package com.nukethemoon.tools.opusproto.tools.geometries;

import java.util.ArrayList;
import java.util.List;

public class PolygonPool {

	private List<float[]> polygons = new ArrayList<float[]>();

	public PolygonPool() {
		polygons.add(new float[] {
				-20, 	-20,
				-30, 	-10,
				-35,	10,
				-20, 	30,
				0,		30,
				20,		10,
				20,		-10,
				10,		-10,
				5,		-30
		});
	}

	public float[] getPolygon(int index) {
		return polygons.get(index);
	}

	public int getPolygonCount() {
		return polygons.size();
	}
}
