package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class YesNoDialog extends BaseDialog {

	public YesNoDialog(String text, Skin skin) {
		super(text, skin);

		key(Input.Keys.ENTER, true);
		key(Input.Keys.ESCAPE, false);
		button("Yes", true);
		button("No", false);
	}
}
