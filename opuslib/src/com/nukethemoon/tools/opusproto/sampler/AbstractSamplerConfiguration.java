package com.nukethemoon.tools.opusproto.sampler;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSamplerConfiguration {

	public String id;
	public double worldSeedModifier;
	public float scale;
	public List<SamplerModifier> modifiers;

	public AbstractSamplerConfiguration(String id) {
		this.id = id;
		scale = 1;
		worldSeedModifier = 0;
		modifiers = new ArrayList<SamplerModifier>();
	}

}
