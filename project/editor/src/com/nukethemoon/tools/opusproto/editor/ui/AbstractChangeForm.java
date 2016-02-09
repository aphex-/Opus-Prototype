package com.nukethemoon.tools.opusproto.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.Notifyable;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractChangeForm extends Table implements Notifyable {
	private List<ChangedListener> listeners = new ArrayList<ChangedListener>();

	public AbstractChangeForm(Skin skin) {
		super(skin);
	}

	public void addChangedListener(ChangedListener listener) {
		listeners.add(listener);
	}

	public void notifyChanges() {
		for (ChangedListener listener : listeners) {
			listener.onChange();
		}
	}

	public interface ChangedListener {
		void onChange();
	}

	public static boolean containsValidFloat(TextField textField) {
		try {
			Float.parseFloat(textField.getText());
			textField.setStyle(Styles.TEXT_FIELD_STYLE);
			return true;
		} catch (NumberFormatException e) {
			textField.setStyle(Styles.TEXT_FIELD_STYLE_FAIL);
			return false;
		}
	}

	public static boolean containsValidDouble(TextField textField) {
		try {
			Double.parseDouble(textField.getText());
			textField.setStyle(Styles.TEXT_FIELD_STYLE);
			return true;
		} catch (NumberFormatException e) {
			textField.setStyle(Styles.TEXT_FIELD_STYLE_FAIL);
			return false;
		}
	}

	public static boolean containsValidInteger(TextField textField) {
		boolean valid = NumberUtils.isDigits(textField.getText());
		if (valid) {
			textField.setStyle(Styles.TEXT_FIELD_STYLE);
			return true;
		} else {
			textField.setStyle(Styles.TEXT_FIELD_STYLE_FAIL);
			return false;
		}
	}

	public static boolean containsValidHexColor(TextField textField) {
		if (parseColor(textField.getText()) == null) {
			textField.setStyle(Styles.TEXT_FIELD_STYLE_FAIL);
			return false;
		} else {
			textField.setStyle(Styles.TEXT_FIELD_STYLE);
			return true;
		}
	}

	public static Color parseColor(String hexString) {
		if (hexString != null) {
			if (hexString.length() == 7) {
				if (hexString.substring(0, 1).equals("#")) {
					try {
						return Color.valueOf(hexString.substring(1, 7));
					} catch (NumberFormatException e) {
						return null;
					}
				}
			}
		}
		return null;
	}


}
