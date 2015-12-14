package com.nukethemoon.tools.opusproto.sampler;

public class SamplerModifier {

	public final Type type;
	public final float value;

	public enum Type {
		Multiply,
		Add,
		Max,
		Min,
		Step,
		Limit,
		Sharpen,
		Sin,
		Pow,
		HigherThan,
		LowerThan,
		Invert

	}

	public SamplerModifier(Type type, float value) {
		this.type = type;
		this.value = value;
	}
}
