package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;

public class SettingsWindow extends ClosableWindow {

	public SettingsWindow(Skin skin) {
		super("SETTINGS", skin);
		top().left();
		defaults().pad(5);

		int width = 300;

		Table shortcutsTable = new Table(skin);
		shortcutsTable.defaults().pad(4);
		shortcutsTable.top().left();
		shortcutsTable.setBackground(Styles.INNER_BACKGROUND);
		Label shortcutsLabel = new Label("Shortcuts", skin);
		shortcutsTable.add(shortcutsLabel).colspan(2).left().top();
		shortcutsTable.row();

		Label refreshShortcutLabel = new Label("refresh world", skin);
		shortcutsTable.add(refreshShortcutLabel).width(width).left().top().fill();
		shortcutsTable.add(new Label("[ENTER]", skin)).left().top();
		shortcutsTable.row();

		Label snapshotShortcutLabel = new Label("snapshot", skin);
		shortcutsTable.add(snapshotShortcutLabel).width(width).left().top().fill();
		shortcutsTable.add(new Label("[s]", skin)).left().top();
		shortcutsTable.row();

		Label moveCameraLabel = new Label("move camera", skin);
		shortcutsTable.add(moveCameraLabel).width(width).left().top().fill();
		shortcutsTable.add(new Label("[Mouse Right] + Drag", skin)).left().top();
		shortcutsTable.row();

		Label generateTiles = new Label("generate tiles", skin);
		shortcutsTable.add(generateTiles).width(width).left().top().fill();
		shortcutsTable.add(new Label("[Mouse Left] + Drag", skin)).left().top();
		shortcutsTable.row();

		Label increaseLabel = new Label("increase value in a text field", skin);
		shortcutsTable.add(increaseLabel).width(width).left().top().fill();
		shortcutsTable.add(new Label("[UP]", skin)).left().top();
		shortcutsTable.row();

		Label decreaseLabel = new Label("decrease value in a text field", skin);
		shortcutsTable.add(decreaseLabel).width(width).left().top().fill();
		shortcutsTable.add(new Label("[DOWN]", skin)).left().top();
		shortcutsTable.row();

		add(shortcutsTable);

		pack();
	}
}
