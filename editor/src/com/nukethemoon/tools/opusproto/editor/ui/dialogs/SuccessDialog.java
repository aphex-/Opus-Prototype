package com.nukethemoon.tools.opusproto.editor.ui.dialogs;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SuccessDialog extends BaseDialog{

	public SuccessDialog(String text, Skin skin) {
		super("Success", skin);
		getContentTable().defaults().pad(15);
		getContentTable().add(new Label(text, skin));
		button("ok", true);
	}
}
