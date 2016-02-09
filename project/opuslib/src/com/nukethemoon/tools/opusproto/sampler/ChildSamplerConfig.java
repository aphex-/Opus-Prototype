package com.nukethemoon.tools.opusproto.sampler;

/**
 * Configuration for a combined sampler item.
 */
public class ChildSamplerConfig {
	public String samplerReferenceId;
	public Operator operator = Operator.Plus;
	public boolean invert = false;
	public boolean active = true; // for debug

	public float scaleModifier = 1.0f;
	public double seedModifier = 0f;
	public float multiply = 1f;

	public ChildSamplerConfig(String samplerReferenceId) {
		this.samplerReferenceId = samplerReferenceId;
	}

	public enum Operator {
		Plus,
		Minus,
		Mix,
		And,
		Or,
		Highest,
		Lowest

	}
}
