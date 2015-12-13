package com.nukethemoon.tools.opusproto.interpreter;



public class GreyScaleInterpreter extends AbstractInterpreter {


	public GreyScaleInterpreter(String id) {
		super(id);
	}

	@Override
	public int getType(float sampleValue) {
		if (sampleValue > 1f || sampleValue < 0) {
			argb8888(1f, 1f, 0, 0);
		}
		return argb8888(1, sampleValue, sampleValue, sampleValue);
	}
}
