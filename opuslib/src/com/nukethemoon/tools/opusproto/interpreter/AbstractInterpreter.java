package com.nukethemoon.tools.opusproto.interpreter;

public abstract class AbstractInterpreter {

	public String id;

	public AbstractInterpreter(String id) {
		this.id = id;
	}

	public abstract int getType(float sampleValue);

	@Override
	public String toString() {
		return id;
	}
}
