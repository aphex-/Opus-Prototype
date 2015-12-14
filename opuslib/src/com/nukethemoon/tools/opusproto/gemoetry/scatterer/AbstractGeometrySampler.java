package com.nukethemoon.tools.opusproto.gemoetry.scatterer;

import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.gemoetry.AbstractGeometryData;
import com.nukethemoon.tools.opusproto.noise.NoiseAlgorithmPool;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public abstract class AbstractGeometrySampler extends AbstractSampler {


	public AbstractGeometrySampler(AbstractSamplerConfiguration config,
								   double worldSeed, NoiseAlgorithmPool noisePool,
								   SamplerLoader samplerLoader) {
		super(config, worldSeed, noisePool, samplerLoader);
	}


	public abstract AbstractGeometryData createGeometries(
			float x, float y, float width, float height, double seed);

}
