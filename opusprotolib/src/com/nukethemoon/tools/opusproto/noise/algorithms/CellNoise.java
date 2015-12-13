package com.nukethemoon.tools.opusproto.noise.algorithms;

import com.nukethemoon.tools.opusproto.gemoetry.PointList;
import com.nukethemoon.tools.opusproto.gemoetry.scatterer.massspring.SimplePositionConfig;
import com.nukethemoon.tools.opusproto.gemoetry.scatterer.massspring.SimplePositionScattering;
import com.nukethemoon.tools.opusproto.noise.AbstractNoiseAlgorithm;
import com.nukethemoon.tools.opusproto.noise.NoiseAlgorithmPool;

/**
 * Own implementation based on
 *
 * https://code.google.com/p/fractalterraingeneration/wiki/Cell_Noise
 */

public class CellNoise extends AbstractNoiseAlgorithm {

	private SimplePositionScattering scattering;

	public CellNoise(NoiseAlgorithmPool pool) {
		SimplePositionConfig positionConfig = new SimplePositionConfig("internal");
		scattering = new SimplePositionScattering(positionConfig, 2323, pool, null);
	}


	@Override
	public float[][] createData(float x, float y, int size, double seed, float scale) {

		float[][] data = new float[size][size];

		float pointGridSize = size * scale;
		((SimplePositionConfig) scattering.getConfig()).gridSize = pointGridSize;
		((SimplePositionConfig) scattering.getConfig()).worldSeedModifier = seed;
		((SimplePositionConfig) scattering.getConfig()).maximumDistance = (pointGridSize / 2) - 1;

		PointList pointList = (PointList) scattering.createGeometries(
				x - (pointGridSize),
				y - (pointGridSize),
				x + (pointGridSize * 3),
				y + (pointGridSize * 3),
				seed);

		for (int tmpX = 0; tmpX < data.length; tmpX++) {
			for (int tmpY = 0; tmpY < data[tmpX].length; tmpY++) {

				float maxDistance = ((SimplePositionConfig) scattering.getConfig()).gridSize;

				float distanceF1 = maxDistance;
				float[] points = pointList.getPoints();
				if (points.length > 0) {
					for (int pointIndex = 0; pointIndex <= ((points.length - 1) / 2); pointIndex++) {
						float pX = points[pointIndex * 2];
						float pY = points[(pointIndex * 2) + 1];
						float tmpDistance = euclideanDistance(tmpX + x, tmpY + y, pX, pY);
						if (tmpDistance < distanceF1) {
							distanceF1 = tmpDistance;
						}
					}
				}


				float normDistance = (distanceF1 / maxDistance);
				if (normDistance > 1f) {
					data[tmpX][tmpY] = 0;
				} else {
					data[tmpX][tmpY] = normDistance;
				}
			}
		}
		return data;
	}



	public static float euclideanDistance(float p0X, float p0Y, float p1X, float p1Y) {
		float differenceX = p0X - p1X;
		float differenceY = p0Y - p1Y;
		return (float) Math.sqrt(differenceX * differenceX + differenceY * differenceY);
	}

	public static float manhattanDistance(float p0X, float p0Y, float p1X, float p1Y) {
		float differenceX = p0X - p1X;
		float differenceY = p0Y - p1Y;
		return Math.abs(differenceX) + Math.abs(differenceY);
	}
}
