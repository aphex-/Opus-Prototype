package com.nukethemoon.tools.opusproto.gemoetry.scatterer.massspring;

import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public class SimplePositionConfig extends AbstractSamplerConfiguration {

	public float maximumDistance = 49f;
	public float gridSize = 100;


	public SimplePositionConfig(String id) {
		super(id);
	}
}
