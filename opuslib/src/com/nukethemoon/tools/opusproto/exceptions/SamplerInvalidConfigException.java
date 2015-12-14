package com.nukethemoon.tools.opusproto.exceptions;

import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;

public class SamplerInvalidConfigException extends Exception {

	private AbstractSampler sampler;

	public SamplerInvalidConfigException(String message, AbstractSampler sampler) {
		super(message);
		this.sampler = sampler;
	}
}
