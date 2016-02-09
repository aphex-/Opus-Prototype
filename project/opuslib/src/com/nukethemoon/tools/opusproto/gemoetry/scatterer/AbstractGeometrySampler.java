package com.nukethemoon.tools.opusproto.gemoetry.scatterer;

import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.gemoetry.AbstractGeometryData;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public abstract class AbstractGeometrySampler extends AbstractSampler {


	public AbstractGeometrySampler(AbstractSamplerConfiguration config,
								   double worldSeed, Algorithms noisePool,
								   Samplers samplers) {
		super(config, worldSeed, noisePool, samplers);
	}


	public abstract AbstractGeometryData createGeometries(
			float x, float y, float width, float height, double seed);

}
