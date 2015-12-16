package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.editor.Config;

public class NewProjectDialog extends BaseDialog {

	private TextField textField;

	public NewProjectDialog(final Skin skin, final Stage stage) {
		super("create new project", skin);

		getContentTable().defaults().pad(15);
		getContentTable().add(new Label("project name:", skin));
		textField = new TextField("", skin);
		getContentTable().add(textField);
		getContentTable().row();

		TextButton createButton = new TextButton("create", skin);
		createButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (textField.getText() == null || textField.getText().equals("")) {
					ErrorDialog errorDialog = new ErrorDialog("Invalid name.", skin);
					errorDialog.show(stage);
				} else {

					if (Gdx.files.local(Config.PROJECT_PATH + textField.getText()).exists()) {
						ErrorDialog errorDialog = new ErrorDialog("Project name already exists.", skin);
						errorDialog.show(stage);
					} else {
						Gdx.files.local(Config.PROJECT_PATH + textField.getText()).mkdirs();
						FileHandle local = Gdx.files.local(Config.PROJECT_PATH + textField.getText() + Config.SAVE_FILE_NAME);

						listener.onResult(textField.getText());
						hide();
					}
				}
			}
		});

		getContentTable().add(createButton).colspan(2);
	}

}
