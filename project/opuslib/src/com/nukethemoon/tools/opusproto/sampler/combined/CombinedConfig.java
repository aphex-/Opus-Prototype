package com.nukethemoon.tools.opusproto.sampler.combined;

import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerContainerConfig;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;

public class CombinedConfig extends AbstractSamplerContainerConfig {

	public CombinedConfig(String samplerId) {
		super(samplerId);
		samplerItems = new ChildSamplerConfig[0];
	}
}
