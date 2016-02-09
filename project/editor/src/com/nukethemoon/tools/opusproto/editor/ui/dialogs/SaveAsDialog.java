package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SaveAsDialog extends BaseDialog {

	public SaveAsDialog(Skin skin) {
		super("save as..", skin);
		getContentTable().pad(15);

		Label nameLabel = new Label("Name:", skin);
		getContentTable().add(nameLabel);

		final TextField textField = new TextField("", skin);
		textField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				resultValue = textField.getText();
			}
		});
		getContentTable().add(textField);

		button("save", true);
		button("cancel", false);
		pack();
	}
}
