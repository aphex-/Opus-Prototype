package com.nukethemoon.tools.opusproto.interpreter;

import java.util.ArrayList;
import java.util.List;

public class ColorInterpreter extends TypeInterpreter {

	public List<ColorInterpreterItem> items = new ArrayList<ColorInterpreterItem>();

	public ColorInterpreter(String id) {
		super(id);
		ColorInterpreterItem item = new ColorInterpreterItem();
		items.add(item);
	}

	@Override
	public int getType(float sampleValue) {
		for (ColorInterpreterItem item : items) {
			if (sampleValue >= item.startValue && sampleValue <= item.endValue) {
				float colorScale = (sampleValue - item.startValue) / (item.endValue - item.startValue);
				float resultR = item.starColorR + (Math.abs(item.endColorR - item.starColorR) * colorScale);
				float resultG = item.starColorG + (Math.abs(item.endColorG - item.starColorG) * colorScale);
				float resultB = item.starColorB + (Math.abs(item.endColorB - item.starColorB) * colorScale);
				// returns the rgb888 color value
				return ((int) (resultR * 255) << 16) | ((int) (resultG * 255) << 8) | (int) (resultB * 255);

			}
		}
		return 0;
	}

	/**
	 * Returns the rgba8888 color value.
	 * @param rgb888 The rgb888 color value.
	 * @param alpha The alpha (0 - 255)
	 * @return the rgba8888 color value.
	 */
	public static int toRGBA888(int rgb888, int alpha) {
		return ((rgb888) << 8) + alpha;
	}


}
