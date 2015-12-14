package com.nukethemoon.tools.opusproto.sampler.acontinent;

import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.noise.AbstractNoiseAlgorithm;
import com.nukethemoon.tools.opusproto.noise.NoiseAlgorithmPool;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.opusproto.sampler.combined.Combined;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public class AContinent extends AbstractSampler {

	private AContinentConfig c;
	private AbstractNoiseAlgorithm noiseAlgorithm;
	private float levelHeight;

	private double[] seeds;

	public AContinent(AbstractSamplerConfiguration config, double worldSeed, NoiseAlgorithmPool noisePool, SamplerLoader samplerLoader) throws SamplerInvalidConfigException {
		super(config, worldSeed, noisePool, samplerLoader);
		c = (AContinentConfig) config;
		init();
	}

	@Override
	protected float[][] bufferedCreateValues(float x, float y, int size,
											 float scaleFactor, double seedModifier, ChunkRequestBuffer buffer) {

		float[][][] tmpSampleData = new float[c.iterations][][];
		float[][] data = new float[size][size];

		float modifiedScale = scaleFactor * config.scale;
		double modifiedSeed = getModifiedSeed(getSamplerSeed(), seedModifier);

		if (c.iterations <= 0) {
			return new float[size][size];
		}

		for (int i = 0; i < c.iterations; i++) {
			float levelScale = modifiedScale * (float) Math.pow(c.growth, i);

			double tmpSeed = getModifiedSeed(seeds[i], modifiedSeed);

			tmpSampleData[i] = noiseAlgorithm.createData(x, y,
					size, tmpSeed, levelScale);

			multiply(tmpSampleData[i], levelHeight);
			for (int xTmp = 0; xTmp < size; xTmp++) {
				for (int yTmp = 0; yTmp < size; yTmp++) {
					data[xTmp][yTmp] = Combined.or(tmpSampleData[i][xTmp][yTmp], data[xTmp][yTmp]);
				}
			}
		}

		for (int xTmp = 0; xTmp < size; xTmp++) {
			for (int yTmp = 0; yTmp < size; yTmp++) {
				float value = data[xTmp][yTmp];
				value = value * c.iterations; // normalize to range 0 - 1
				value = Math.min(value, c.size + c.edge); // cut top
				value = Math.max(value, c.size); // cut bottom
				value = value - c.size; // translate to 0
				value = value * (1 / c.edge); // normalize to range 0 - 1

				if (c.smoothEdge) {
					value = smoothEdge(value);
				}
				data[xTmp][yTmp] = limit(value);
			}
		}
		return data;
	}



	@Override
	protected void loadConfig() throws SamplerInvalidConfigException {
		if (c.iterations <= 0) {
			return;
		}

		this.noiseAlgorithm = noisePool.getAlgorithm(c.noiseAlgorithmName);
		this.levelHeight = 1f / (float) c.iterations;

		seeds = new double[c.iterations];
		seeds[0] = getModifiedSeed(worldSeed, c.worldSeedModifier);
		for (int i = 1; i < seeds.length; i++) {
			seeds[i] = getModifiedSeed(seeds[i - 1], c.worldSeedModifier);
		}
	}

	@Override
	protected void compute(float[][] data) {

	}


	private float smoothEdge(float value) {
		float returnValue = value;
		if (value > 0.55) {
			float smoothed = (value - ((float) Math.pow(value, 3)) * 0.3f) + 0.05f;
			returnValue = smoothed;
		}
		return returnValue * 1.333333f;
	}


	@Override
	public float getMaxSample() {
		return 1;
	}

	@Override
	public float getMinSample() {
		return 0;
	}


}
