package com.nukethemoon.tools.opusproto.layer;

import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerContainerConfig;

/**
 * Configuration for a layer.
 */
public class LayerConfig extends AbstractSamplerContainerConfig {

	public String interpreterId;

	public LayerConfig(String id) {
		super(id);
	}
}
