package com.nukethemoon.tools.opusproto.tools.geometries;

import com.nukethemoon.tools.opusproto.noise.AbstractNoiseAlgorithm;
import com.nukethemoon.tools.opusproto.noise.algorithms.SimplexNoise;

public class RandomizedGrid {

	private double seedAngle;
	private double seedRadius;

	private float cellSize;
	private float maxDisplacement;
	private AbstractNoiseAlgorithm noiseAlgorithm;


	public RandomizedGrid(float cellSize, long seed) {
		noiseAlgorithm = new SimplexNoise();
		int seedLength = Long.toString(seed).length();
		double convertedSeed = seed / Math.pow(10, seedLength);
		this.seedAngle = convertedSeed;
		this.seedRadius = convertedSeed / 2d;

		this.cellSize = cellSize;
		this.maxDisplacement = cellSize * 0.49f;
	}

	/**
	 * p1  _____  p2
	 *    |     |
	 *    |     |
	 * p0  -----  p3
	 *
	 * @return
	 */

	public float[] generatePolygon(int x, int y) {

		/*double noiseAnglePoint00 = noiseAlgorithm.noise(x, 		y, seedAngle) 		* Math.PI;
		double noiseAnglePoint01 = noiseAlgorithm.noise(x, 		y + 1, seedAngle) 	* Math.PI;
		double noiseAnglePoint02 = noiseAlgorithm.noise(x + 1, 	y + 1, seedAngle) 	* Math.PI;
		double noiseAnglePoint03 = noiseAlgorithm.noise(x + 1, 	y, seedAngle) 		* Math.PI;

		double noiseRadiusPoint00 = shiftUp(noiseAlgorithm.noise(x, 		y, seedAngle)) 		* maxDisplacement;
		double noiseRadiusPoint01 = shiftUp(noiseAlgorithm.noise(x, 		y + 1, seedAngle)) 	* maxDisplacement;
		double noiseRadiusPoint02 = shiftUp(noiseAlgorithm.noise(x + 1, 	y + 1, seedAngle)) 	* maxDisplacement;
		double noiseRadiusPoint03 = shiftUp(noiseAlgorithm.noise(x + 1, 	y, seedRadius)) 	* maxDisplacement;

		float p0X = (x * cellSize) 			+ (float) (Math.cos(noiseAnglePoint00) * noiseRadiusPoint00);
		float p0Y = (y * cellSize) 			+ (float) (Math.sin(noiseAnglePoint00) * noiseRadiusPoint00);

		float p1X = (x * cellSize) 			+ (float) (Math.cos(noiseAnglePoint01) * noiseRadiusPoint01);
		float p1Y = ((y + 1) * cellSize) 	+ (float) (Math.sin(noiseAnglePoint01) * noiseRadiusPoint01);

		float p2X = ((x + 1) * cellSize) 	+ (float) (Math.cos(noiseAnglePoint02) * noiseRadiusPoint02);
		float p2Y = ((y + 1) * cellSize) 	+ (float) (Math.sin(noiseAnglePoint02) * noiseRadiusPoint02);

		float p3X = ((x + 1) * cellSize) 	+ (float) (Math.cos(noiseAnglePoint03) * noiseRadiusPoint03);
		float p3Y = (y * cellSize) 			+ (float) (Math.sin(noiseAnglePoint03) * noiseRadiusPoint03);

		return new float[] {
			p0X, p0Y,
			p1X, p1Y,
			p2X, p2Y,
			p3X, p3Y
		};*/

		return null;
	}

	private double shiftUp(double pValue) {
		return (pValue * 0.5d) + 0.5d;
	}
}
