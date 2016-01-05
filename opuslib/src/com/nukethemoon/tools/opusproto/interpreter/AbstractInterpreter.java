package com.nukethemoon.tools.opusproto.interpreter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInterpreter {

	public String id;
	public List<? extends InterpreterItem> it;

	public AbstractInterpreter(String id) {
		this.id = id;
		it = new ArrayList<>();
	}

	public int getType(float sampleValue) {
		for (int i = 0; i < it.size(); i++) {
			InterpreterItem item = it.get(i);
			if (sampleValue >= item.startValue && sampleValue <= item.endValue) {
				return i;
			}
		}
		return -1;
	}

	public int getTypeCount() {
		return it.size();
	}

	@Override
	public String toString() {
		return id;
	}

	public static class InterpreterItem {
		public float startValue = 0;
		public float endValue = 1;
	}
}
