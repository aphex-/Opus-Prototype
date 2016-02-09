package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.tools.opusproto.editor.Config;

import java.io.File;
import java.io.FileFilter;

public class LoadDialog extends BaseDialog {

	private List<String> uiList = null;
	private String path;

	public LoadDialog(String path, Skin skin) {
		super("Load from " + Config.PROJECT_PATH + path, skin);
		this.path = path;
		FileHandle internal = Gdx.files.local(Config.PROJECT_PATH + path);

		String errorText = null;
		if (!internal.exists()) {
			errorText = "Directory not found '" + internal.path() + "'";
		}

		FileHandle[] list = internal.list(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});

		if (list.length == 0) {
			errorText = "No files found in '" + internal.path() + "'";
		}
		if (errorText != null) {
			text(errorText);
			button("OK", false);
			key(Input.Keys.ENTER, false);
			key(Input.Keys.ESCAPE, false);
			return;
		}

		uiList = new List<String>(skin);

		String[] fileNames = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			FileHandle file = list[i];
			fileNames[i] = file.name();
		}
		uiList.setItems(fileNames);

		ScrollPane pane = new ScrollPane(uiList, skin);
		pane.setFadeScrollBars(false);
		pane.setSize(300, 300);

		getContentTable().add(pane).top();
		getContentTable().defaults().pad(2);



		button("load", true);
		button("cancel", false);
		key(Input.Keys.ENTER, true);
		key(Input.Keys.ESCAPE, false);
	}


	@Override
	protected void result(Object object) {
		resultValue = Config.PROJECT_PATH + path + "/" + uiList.getSelected();
		super.result(object);
	}
}
