package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandCreateSampler;


public class NewSamplerDialog extends BaseDialog {

	private final TextField nameField;
	private final SelectBox<String> selectBox;

	public NewSamplerDialog(Skin skin) {
		super("Create new sampler", skin);


		Label nameLabel = new Label("Name", skin);
		getContentTable().add(nameLabel);
		nameField = new TextField("default", skin);
		getContentTable().pad(2);
		getContentTable().add(nameField);
		getContentTable().row();

		selectBox = new SelectBox(skin);
		String[] samplerNames = new String[Samplers.SAMPLER_TO_CONFIG.size()];
		int i = 0;
		for (Class c : Samplers.SAMPLER_TO_CONFIG.keySet()) {
			samplerNames[i] = c.getSimpleName();
			i++;
		}
		selectBox.setItems(samplerNames);
		Label typeLabel = new Label("Type", skin);
		getContentTable().add(typeLabel);
		getContentTable().add(selectBox);

		key(Input.Keys.ENTER, true);
		key(Input.Keys.ESCAPE, false);
		button("create", true);
		button("cancel", false);
	}

	@Override
	protected void result(Object object) {
		Boolean success = (Boolean)object;
		if (success) {
			String name = nameField.getText();
			String type = selectBox.getSelected();
			Editor.post(new CommandCreateSampler(name, type));
		}
	}
}
