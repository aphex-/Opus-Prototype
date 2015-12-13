package com.nukethemoon.tools.opusproto.interpreter;

public abstract class AbstractInterpreter {

	public String id;

	public AbstractInterpreter(String id) {
		this.id = id;
	}

	public abstract int getType(float sampleValue);

	public static int argb8888 (float a, float r, float g, float b) {
		return ((int)(a * 255) << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
	}

	public static int valueOf (String hex) {
		hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;
		int r = Integer.valueOf(hex.substring(0, 2), 16);
		int g = Integer.valueOf(hex.substring(2, 4), 16);
		int b = Integer.valueOf(hex.substring(4, 6), 16);
		int a = hex.length() != 8 ? 255 : Integer.valueOf(hex.substring(6, 8), 16);
		return argb8888(a / 255f, r / 255f, g / 255f, b / 255f);
	}

	@Override
	public String toString() {
		return id;
	}
}
