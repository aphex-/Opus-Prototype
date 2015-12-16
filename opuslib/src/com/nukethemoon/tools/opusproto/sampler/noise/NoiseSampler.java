package com.nukethemoon.tools.opusproto.sampler.noise;

import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.noise.AbstractNoiseAlgorithm;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.Samplers;

public class NoiseSampler extends AbstractSampler {

	private AbstractNoiseAlgorithm noiseAlgorithm;
	private NoiseConfig noiseConfig;

	public NoiseSampler(AbstractSamplerConfiguration config, double seed, Algorithms pool, Samplers samplers) throws SamplerInvalidConfigException {
		super(config, seed, pool, samplers);
		noiseConfig = (NoiseConfig) config;
		init();
	}

	@Override
	public void loadConfig() {
		this.noiseAlgorithm = noisePool.getAlgorithm(noiseConfig.noiseAlgorithmName);
	}

	@Override
	public float[][] bufferedCreateValues(float x, float y,
										  int size, float scaleFactor, double seedModifier, ChunkRequestBuffer buffer) {


		return noiseAlgorithm.createData(x, y, size, getModifiedSeed(getSamplerSeed(), seedModifier), config.scale * scaleFactor);
	}

	@Override
	protected void compute(float[][] data) {

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
