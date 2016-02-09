package com.nukethemoon.tools.opusproto.noise.algorithms;

// based on:
// http://godsnotwheregodsnot.blogspot.de/2013/11/field-diamond-squared-fractal-terrain.html

import com.nukethemoon.tools.opusproto.noise.AbstractNoiseAlgorithm;

public class DiamondSquare extends AbstractNoiseAlgorithm {

	private static SimplexNoise sn = new SimplexNoise();


	@Override
	public float[][] createData(float x, float y, int size, double seed, float scale, float resolution) {
		return createDataMap((int) x, (int) y, size, 7, seed);
	}

	private static float[][] createData(int x0, int y0, int x1, int y1, int iterations, double seed) {

		if (x1 < x0) { return null; }
		if (y1 < y0) { return null; }

		float[][] data = new float[(x1 - x0)][(y1 - y0)];

		if (iterations == 0) {
			for (int j = 0; j < data.length; j++) {
				for (int k = 0; k < data[j].length; k++) {
					data[j][k] =  displace(iterations, x0 + j, y0 + k, seed);
				}
			}
			return data;
		}
		int ux0 = (int) Math.floor(x0 / 2f) - 1;
		int uy0 = (int) Math.floor(y0 / 2f) - 1;
		int ux1 = (int) Math.ceil(x1 / 2f) + 1;
		int uy1 = (int) Math.ceil(y1 / 2f) + 1;

		float[][] upperMap = createData(ux0, uy0, ux1, uy1, iterations - 1, seed);

		int uw = ux1 - ux0;
		int uh = uy1 - uy0;

		int cx0 = ux0 * 2;
		int cy0 = uy0 * 2;

		int cw = uw * 2 - 1;
		int ch = uh * 2 - 1;

		float[][] currentMap = new float[cw][ch];

		for (int j = 0; j < uw; j++) {
			for (int k = 0; k < uh; k++) {
				currentMap[j * 2][k * 2] = upperMap[j][k];
			}
		}

		int xOff = x0 - cx0;
		int yOff = y0 - cy0;
		for (int j = 1; j < cw-1; j += 2) {
			for (int k = 1; k < ch-1; k += 2) {
				currentMap[j][k] = ((currentMap[j - 1][k - 1] + currentMap[j - 1][k + 1] + currentMap[j + 1][k - 1] + currentMap[j + 1][k + 1]) / 4f)
						+ displace(iterations, cx0 + j, cy0 + k, seed);
			}
		}
		for (int j = 1; j < cw-1; j += 2) {
			for (int k = 2; k < ch-1; k += 2) {
				currentMap[j][k] = ((currentMap[j - 1][k] + currentMap[j + 1][k] + currentMap[j][k - 1] + currentMap[j][k + 1]) / 4f)
						+ displace(iterations, cx0 + j, cy0 + k, seed);
			}
		}
		for (int j = 2; j < cw-1; j += 2) {
			for (int k = 1; k < ch-1; k += 2) {
				currentMap[j][k] = ((currentMap[j - 1][k] + currentMap[j + 1][k] + currentMap[j][k - 1] + currentMap[j][k + 1]) / 4f)
						+ displace(iterations, cx0 + j, cy0 + k, seed);
			}
		}

		for (int j = 0; j < data.length; j++) {
			for (int k = 0; k < data[j].length; k++) {
				data[j][k] = currentMap[j + xOff][k + yOff];
			}
		}

		return data;
	}

	// Random function to offset
	private static float displace(int iterations, int x, int y, double seed) {
		float noise = (float) sn.noise(x, y, iterations + seed);
		return ((( noise - 0.5f) * 2)) / (iterations + 1);
	}

	private static float getMaxDeviation(int iterations) {
		float dev = 0.5f / (iterations + 1);
		if (iterations <= 0) return dev;
		return getMaxDeviation(iterations-1) + dev;
	}


	public static float[][] createDataMap(int x, int y, int size, int iterations, double seed) {
		float[][] map = createData(x, y, x + size, y + size, iterations, seed);
		float maxDeviation = getMaxDeviation(iterations);
		for (int j = 0; j < size; j++) {
			for (int k = 0; k < size; k++) {
				map[j][k] = map[j][k] / maxDeviation;
				map[j][k] = (map[j][k] + 1) / 2f;
				if (map[j][k] > 1f) {
					map[j][k] = 1f;
				}
				if (map[j][k] < 0f) {
					map[j][k] = 0;
				}
			}
		}
		return map;
	}


}
