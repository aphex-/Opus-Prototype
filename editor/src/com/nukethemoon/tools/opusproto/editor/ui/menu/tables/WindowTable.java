package com.nukethemoon.tools.opusproto.editor.ui.menu.tables;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandOpenInterpreterList;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandOpenLayerEditor;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenProjectWindow;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerList;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenSettingsWindow;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenWorldEditor;

public class WindowTable extends AbstractMenuTable {

	public WindowTable(Skin skin) {
		super(skin, "window");

		MenuButton samplerButton = new MenuButton("sampler editor", skin);
		samplerButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenSamplerEditor(null));
				handleClose();
			}
		});
		contentTable.add(samplerButton).left().fill();
		contentTable.row();

		MenuButton samplerOverviewButton = new MenuButton("sampler overview", skin);
		samplerOverviewButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenSamplerList());
				handleClose();
			}
		});
		contentTable.add(samplerOverviewButton).left().fill();
		contentTable.row();


		MenuButton layerButton = new MenuButton("layer", skin);
		layerButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenLayerEditor(null));
				handleClose();
			}
		});
		contentTable.add(layerButton).left().fill();
		contentTable.row();

		MenuButton worldButton = new MenuButton("world", skin);
		worldButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenWorldEditor());
				handleClose();
			}
		});
		contentTable.add(worldButton).left().fill();
		contentTable.row();


		MenuButton projectButton = new MenuButton("project", skin);
		projectButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenProjectWindow());
				handleClose();
			}
		});
		contentTable.add(projectButton).left().fill();
		contentTable.row();

		MenuButton interpreterButton = new MenuButton("interpreter", skin);
		interpreterButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenInterpreterList());
				handleClose();
			}
		});
		contentTable.add(interpreterButton).left().fill();
		contentTable.row();


		MenuButton settingsButton = new MenuButton("settings", skin);
		settingsButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				Editor.post(new CommandOpenSettingsWindow());
				handleClose();
			}
		});
		contentTable.add(settingsButton).left().fill();
		contentTable.row();

	}
}
