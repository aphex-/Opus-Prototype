package com.nukethemoon.tools.opusproto.sampler;

public abstract class AbstractSamplerContainerConfig extends AbstractSamplerConfiguration {

	public ChildSamplerConfig[] samplerItems;

	public AbstractSamplerContainerConfig(String id) {
		super(id);
		samplerItems = new ChildSamplerConfig[0];
	}


	public ChildSamplerConfig[] getChildSamplerConfigs() {
		return samplerItems;
	}

}
