package com.nukethemoon.tools.opusproto.sampler.noise;

import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

/**
 * The configuration for this sampler.
 */
public class NoiseConfig extends AbstractSamplerConfiguration {

	public String noiseAlgorithmName;

	public NoiseConfig(String id) {
		super(id);
		this.noiseAlgorithmName = Algorithms.NAME_SIMPLEX;
	}
}
