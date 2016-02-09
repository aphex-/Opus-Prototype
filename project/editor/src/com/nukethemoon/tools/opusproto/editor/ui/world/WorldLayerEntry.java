package com.nukethemoon.tools.opusproto.editor.ui.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandDeleteLayer;
import com.nukethemoon.tools.opusproto.editor.message.CommandGenerateWorld;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandOpenLayerEditor;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.layer.Layer;

public class WorldLayerEntry extends Table {


	private final Skin skin;
	private final Layer layer;
	private LayerRankListener rankListener;

	public WorldLayerEntry(Skin skin, final Layer layer, final LayerRankListener listener) {
		this.skin = skin;
		this.layer = layer;
		this.rankListener = listener;
		defaults().pad(2);
		setBackground(Styles.ITEM_BACKGROUND);

		TextButton upButton = new TextButton("up", skin);
		upButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				listener.onMoveUp(layer.getConfig().id);
			}
		});
		add(upButton);

		TextButton downButton = new TextButton("down", skin);
		downButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				listener.onMoveDown(layer.getConfig().id);
			}
		});
		add(downButton);

		Label layerName = new Label(layer.getConfig().id, skin);
		layerName.setColor(Styles.LAYER_COLOR);
		add(layerName).padLeft(10).padRight(10).expand().left();

		TextButton editButton = new TextButton("edit", skin);
		editButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandOpenLayerEditor(layer.getConfig().id));
			}
		});
		add(editButton);

		TextButton removeButton = new TextButton("x", skin);
		removeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandDeleteLayer(layer.getConfig().id));
			}
		});
		add(removeButton);

		final CheckBox activeBox = new CheckBox("", skin);
		activeBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				layer.active = activeBox.isChecked();
				Editor.post(new CommandGenerateWorld());
			}
		});
		activeBox.setChecked(true);
		add(activeBox);

		pack();
	}


	public interface LayerRankListener {
		void onMoveUp(String layerId);
		void onMoveDown(String layerId);
	}
}
