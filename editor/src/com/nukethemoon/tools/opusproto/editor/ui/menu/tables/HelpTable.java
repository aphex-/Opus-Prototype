package com.nukethemoon.tools.opusproto.editor.ui.menu.tables;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenAboutWindow;

public class HelpTable extends AbstractMenuTable {

	public HelpTable(Skin skin) {
		super(skin, "help");

		final MenuButton docuButton = new MenuButton("docu", skin);
		docuButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.openHelp("data/docu/index.html");
			}
		});

		contentTable.add(docuButton).expand().left().fill();
		contentTable.row();

		MenuButton aboutButton = new MenuButton("about", skin);
		aboutButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenAboutWindow());
			}
		});
		contentTable.add(aboutButton).expand().left().fill();
		pack();
	}
}
