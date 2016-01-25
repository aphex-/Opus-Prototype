package com.nukethemoon.tools.opusproto.noise;

public abstract class AbstractNoiseAlgorithm {

	public abstract float[][] createData(float x, float y, int size, double seed, float scale, float resolution);
}
