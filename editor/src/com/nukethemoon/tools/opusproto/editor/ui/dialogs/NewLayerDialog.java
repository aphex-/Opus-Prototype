package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandCreateLayer;

public class NewLayerDialog extends BaseDialog {

	protected final TextField nameTextField;

	public NewLayerDialog(Skin skin) {
		super("Create", skin);
		getContentTable().defaults().pad(15);

		Label nameLabel = new Label("Name:", skin);
		getContentTable().add(nameLabel);

		nameTextField = new TextField("", skin);
		getContentTable().add(nameTextField);

		button("create", true);
		button("cancel", false);
	}

	@Override
	protected void result(Object object) {
		super.result(object);
		if ((Boolean) object) {
			Editor.post(new CommandCreateLayer(nameTextField.getText()));
		}
	}
}
