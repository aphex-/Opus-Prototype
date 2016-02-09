package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandCreateInterpreter;

public class NewInterpreterDialog extends NewLayerDialog {

	public NewInterpreterDialog(Skin skin) {
		super(skin);
	}

	@Override
	protected void result(Object object) {
		if ((Boolean) object) {
			Editor.post(new CommandCreateInterpreter(nameTextField.getText()));
		}
	}
}
