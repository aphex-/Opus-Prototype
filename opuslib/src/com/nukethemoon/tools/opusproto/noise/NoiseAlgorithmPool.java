package com.nukethemoon.tools.opusproto.noise;

import com.nukethemoon.tools.opusproto.noise.algorithms.CellNoise;
import com.nukethemoon.tools.opusproto.noise.algorithms.DiamondSquare;
import com.nukethemoon.tools.opusproto.noise.algorithms.SimplexNoise;

import java.util.HashMap;
import java.util.Map;

public class NoiseAlgorithmPool {

	private Map<String, AbstractNoiseAlgorithm> noiseAlgorithms;

	public static final String NAME_SIMPLEX = SimplexNoise.class.getSimpleName();
	public static final String NAME_DIAMOND_SQUARE = DiamondSquare.class.getSimpleName();
	public static final String NAME_CELL_NOISE = CellNoise.class.getSimpleName();

	public NoiseAlgorithmPool() {
		noiseAlgorithms = new HashMap<String, AbstractNoiseAlgorithm>();
		noiseAlgorithms.put(NAME_SIMPLEX, new SimplexNoise());
		noiseAlgorithms.put(NAME_DIAMOND_SQUARE, new DiamondSquare());
		noiseAlgorithms.put(NAME_CELL_NOISE, new CellNoise(this));
	}

	public AbstractNoiseAlgorithm getAlgorithm(String name) {
		return noiseAlgorithms.get(name);
	}

	public void addAlgorithm(String name, AbstractNoiseAlgorithm algorithm) {
		noiseAlgorithms.put(name, algorithm);
	}

}
