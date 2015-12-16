package com.nukethemoon.tools.opusproto.editor.ui.menu.tables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.tools.opusproto.Samplers;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotChangeSettings;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotDelete;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;

public class SnapshotTable extends Table {

	private Samplers samplers;

	public SnapshotTable(Skin skin, Samplers samplers) {
		this.samplers = samplers;
		top().left();
		setBackground(Styles.ITEM_BACKGROUND);

		final Label overlayLabel = new Label("snapshot", skin);
		add(overlayLabel);
		final Slider slider = new Slider(0f, 1f, 0.05f, false, skin);
		slider.setValue(0.75f);
		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandSnapshotChangeSettings(false, slider.getValue()));
			}
		});

		add(slider).width(80).padLeft(4).padRight(4);

		TextButton closeButton = new TextButton("x", skin);
		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Editor.post(new CommandSnapshotDelete());
				SnapshotTable.this.setVisible(false);
			}
		});
		add(closeButton).left().top();

		pack();
	}


}
