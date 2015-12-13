package com.nukethemoon.tools.opusproto.editor.ui.sampler;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class CursorTextField extends TextField {

	public CursorTextField(String text, Skin skin, final Notifyable n, final float offset) {
		super(text, skin);

		addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				Float f = parseFloat(getText());
				if (f != null) {
					f = (float) Math.round(f * 100f) / 100f;
					if (Input.Keys.UP == keycode) {
						f += offset;
						setProgrammaticChangeEvents(true);
						setText(f + "");
						setProgrammaticChangeEvents(false);
						if (n != null) {
							n.notifyChanges();
						}
					}
					if (Input.Keys.DOWN == keycode) {
						f -= offset;
						setProgrammaticChangeEvents(true);
						setText(f + "");
						setProgrammaticChangeEvents(false);
						if (n != null) {
							n.notifyChanges();

						}
					}
				}
				return super.keyDown(event, keycode);
			}
		});
	}

	private Float parseFloat(String string) {
		try {
			float v = Float.parseFloat(string);
			return v;
		} catch(NumberFormatException e) {
			return null;
		}
	}
}
