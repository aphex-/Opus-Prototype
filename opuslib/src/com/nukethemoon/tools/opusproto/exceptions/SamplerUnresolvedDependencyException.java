package com.nukethemoon.tools.opusproto.exceptions;

import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerContainerConfig;

import java.util.List;

public class SamplerUnresolvedDependencyException extends Exception {

	public SamplerUnresolvedDependencyException(List<AbstractSamplerContainerConfig> notLoadable) {
		super(createMessage(notLoadable));
	}

	private static String createMessage(List<AbstractSamplerContainerConfig> notLoadable) {
		String message = "Can not load dependencies for the samplers: ";
		for (AbstractSamplerContainerConfig c : notLoadable) {
			message += c.id + " ";
		}
		message += ".";
		return message;
	}

}
