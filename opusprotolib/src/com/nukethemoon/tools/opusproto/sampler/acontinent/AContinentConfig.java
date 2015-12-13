package com.nukethemoon.tools.opusproto.sampler.acontinent;

import com.nukethemoon.tools.opusproto.noise.NoiseAlgorithmPool;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public class AContinentConfig extends AbstractSamplerConfiguration {

	public float size = 0.5f;
	public float edge = 0.05f;

	public int iterations = 4;
	public float growth = 2f;

	public String noiseAlgorithmName;

	public boolean smoothEdge = false;

	public AContinentConfig(String id) {
		super(id);
		this.scale = 5;
		this.noiseAlgorithmName = NoiseAlgorithmPool.NAME_SIMPLEX;
		this.worldSeedModifier = 0;
	}
}
