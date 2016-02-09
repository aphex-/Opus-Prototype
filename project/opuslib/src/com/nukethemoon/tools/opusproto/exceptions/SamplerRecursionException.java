package com.nukethemoon.tools.opusproto.exceptions;

import com.nukethemoon.tools.opusproto.Config;

public class SamplerRecursionException extends Exception {

	public SamplerRecursionException(String samplerId) {
		super("Failed to load sampler '" + samplerId + "'. Max recursion depth "
				+ Config.MAX_SAMPLER_RECURSION_DEPTH + " reached. Check for circular dependencies in your project.");
	}
}
