package com.nukethemoon.tools.opusproto.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Util {






	public static TextField.TextFieldFilter FLOAT_FILTER = new TextField.TextFieldFilter() {
		@Override
		public boolean acceptChar(TextField textField, char c) {
			return isFloatString(textField.getText(), c);
		}
	};

	public static boolean isFloatString(String string, char additionalChar) {
		String testString = string + additionalChar;
		try {
			Float.parseFloat(testString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}


	public static class SamplerNameAndConfig {
		private FileHandle file;
		private AbstractSamplerConfiguration config;

		public SamplerNameAndConfig(FileHandle file, AbstractSamplerConfiguration config) {
			this.file = file;
			this.config = config;
		}

		public AbstractSamplerConfiguration getConfig() {
			return config;
		}

		@Override
		public String toString() {
			String secondString = "null";
			if (config != null) {
				secondString = config.getClass().getSimpleName();
			}
			if (file != null) {
				secondString += " [" +file.name() + "]";
			}
			return secondString;
		}
	}

	public static String createIntervalInfo(float min, float max) {
		return "interval [" + min + " , " + max + "]";
	}

	public static AbstractSampler[] filter(AbstractSampler[] list, Class<? extends AbstractSampler> samplerClass) {
		if (samplerClass == null) {
			return list;
		}
		AbstractSampler[] samplerList;
		List<AbstractSampler> filteredList = new ArrayList<AbstractSampler>();
		for (AbstractSampler s : list) {
			if (s.getClass() == samplerClass) {
				filteredList.add(s);
			}
		}
		samplerList = new AbstractSampler[filteredList.size()];
		return filteredList.toArray(samplerList);
	}

	public static void applyStandardSettings(ScrollPane pane) {
		pane.setFadeScrollBars(false);
		pane.setOverscroll(false, false);
		pane.setScrollingDisabled(true, false);
		pane.setScrollbarsOnTop(true);
	}

}
