package com.nukethemoon.tools.opusproto.sampler.flat;

import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public class FlatSampler extends AbstractSampler {

	private FlatSamplerConfig flatSamplerConfig;

	public FlatSampler(AbstractSamplerConfiguration config, double seed, Algorithms pool, Samplers samplers) throws SamplerInvalidConfigException {
		super(config, seed, pool, samplers);
		flatSamplerConfig = (FlatSamplerConfig) config;
		init();
	}

	@Override
	public float[][] bufferedCreateValues(float x, float y, int size, float scaleFactor,
										  float resolution,
										  double seedModifier, ChunkRequestBuffer buffer) {

		int resSize = (int) (size / resolution);
		float[][] data = new float[resSize][resSize];
		for (int xTmp = 0; xTmp < data.length; xTmp++) {
			for (int yTmp = 0; yTmp < data[xTmp].length; yTmp++) {
				data[xTmp][yTmp] = flatSamplerConfig.value;
			}
		}
		return data;
	}

	@Override
	public void loadConfig() {

	}

	@Override
	protected void compute(float[][] data) {

	}


	@Override
	public float getMaxSample() {
		return flatSamplerConfig.value;
	}

	@Override
	public float getMinSample() {
		return flatSamplerConfig.value;
	}

}
