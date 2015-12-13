package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;


public class RenameDialog extends BaseDialog {

	public RenameDialog(Skin skin, String oldName) {
		super("Rename", skin);
		getContentTable().defaults().pad(14);
		final TextField renameTextField = new TextField(oldName, skin);
		renameTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				resultValue = renameTextField.getText();
			}
		});
		getContentTable().add(renameTextField);

		button("OK", true);
		button("cancel", false);
	}
}
