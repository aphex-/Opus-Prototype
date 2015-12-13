package com.nukethemoon.tools.opusproto.editor.ui.menu.tables;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenSaveAs;
import com.nukethemoon.tools.opusproto.editor.message.CommandSaveProject;

public class FileTable extends AbstractMenuTable {

	public FileTable(Skin skin) {
		super(skin, "file");

		final MenuButton saveButton = new MenuButton("save", skin);
		saveButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandSaveProject());
				handleClose();
			}
		});

		contentTable.add(saveButton).expand().left().fill();
		contentTable.row();

		MenuButton exportButton = new MenuButton("save as ..", skin);
		exportButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenSaveAs());
				handleClose();
			}
		});
		contentTable.add(exportButton).expand().left().fill();
		pack();
	}


}
