package com.nukethemoon.tools.opusproto.interpreter;



public class GreyScaleInterpreter extends TypeInterpreter {


	public GreyScaleInterpreter(String id) {
		super(id);
	}

	@Override
	public int getType(float sampleValue) {
		return 0;
	}
}
