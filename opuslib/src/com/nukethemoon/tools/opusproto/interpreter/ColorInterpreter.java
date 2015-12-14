package com.nukethemoon.tools.opusproto.interpreter;

import java.util.ArrayList;
import java.util.List;

public class ColorInterpreter extends AbstractInterpreter {

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
				return argb8888(resultR, resultG, resultB, 1);
			}
		}
		return argb8888(0,0,0,0);
	}


}
