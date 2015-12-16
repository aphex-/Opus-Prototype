package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.tools.opusproto.editor.Config;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.loader.json.JsonLoader;

public class ProjectDialog extends BaseDialog {

	private final List<String> projectsList;
	private Stage stage;

	public ProjectDialog(Skin skin, Stage stage) {
		super("Project", skin);
		this.stage = stage;


		Table table = getContentTable();
		table.padTop(10);
		table.padBottom(10);

		table.add(new Label("Select the project to open from '" + Config.PROJECT_PATH + "'", skin));
		table.row();

		Table projectListTable = new Table(skin);


		projectListTable.defaults().pad(2);
		projectListTable.setBackground(Styles.INNER_BACKGROUND);
		table.add(projectListTable).colspan(2);
		projectListTable.row();

		FileHandle projectsDir = Gdx.files.local(Config.PROJECT_PATH);

		if (!projectsDir.exists()) {
			projectsDir.mkdirs();
		}

		projectsList = new List<String>(skin);
		FileHandle[] list = projectsDir.list(JsonLoader.DIRECTORY_FILTER);
		String[] projectNames = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			projectNames[i] = list[i].name();
		}
		projectsList.setItems(projectNames);

		projectListTable.add(projectsList);
		row();

		table.row();

		button("open", true);
		button("create new", false);
		pack();
	}


	@Override
	protected void result(Object object) {
		boolean resultOpen = (Boolean) object;
		if (resultOpen) {
			listener.onResult(projectsList.getSelected());
		} else {
			NewProjectDialog newProjectDialog = new NewProjectDialog(getSkin(), stage);
			newProjectDialog.show(stage);
			newProjectDialog.setResultListener(listener);
		}
	}
}
