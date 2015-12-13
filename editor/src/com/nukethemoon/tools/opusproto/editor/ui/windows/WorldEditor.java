package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandGenerateWorld;
import com.nukethemoon.tools.opusproto.editor.message.CommandRenameElement;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.BaseDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.NewLayerDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.RenameDialog;
import com.nukethemoon.tools.opusproto.editor.ui.world.WorldLayerEntry;
import com.nukethemoon.tools.opusproto.generator.WorldGenerator;
import com.nukethemoon.tools.opusproto.layer.Layer;

import java.util.List;

public class WorldEditor extends ClosableWindow implements WorldLayerEntry.LayerRankListener {

	private final Table layers;
	private final Label worldName;
	private Table layersContainer;
	private WorldGenerator generator;
	private Skin skin;


	public WorldEditor(final Skin skin, WorldGenerator generator, final Stage stage, final String projectName) {
		super("WORLD", skin);
		defaults().pad(2);
		this.generator = generator;
		this.skin = skin;


		worldName = new Label(projectName, skin);
		worldName.setColor(Styles.PROJECT_COLOR);
		add(worldName).left();
		TextButton renameButton = new TextButton("rename", skin);
		renameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenameDialog renameDialog = new RenameDialog(skin, projectName);
				renameDialog.setResultListener(new BaseDialog.ResultListener() {
					@Override
					public void onResult(Object result) {
						Editor.post(new CommandRenameElement(projectName,
								(String) result, CommandRenameElement.ElementType.World));
					}
				});
				renameDialog.show(stage);
			}
		});
		add(renameButton).right();
		row();


		layersContainer = new Table(skin);
		layersContainer.defaults().pad(3);
		layersContainer.setBackground(Styles.INNER_BACKGROUND);
		Label layerTitle = new Label("Layers", skin);
		layersContainer.add(layerTitle).left();

		TextButton createNewButton = new TextButton("create", skin);
		createNewButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NewLayerDialog newLayerDialog = new NewLayerDialog(skin);
				newLayerDialog.show(stage);

			}
		});
		layersContainer.add(createNewButton).right();
		layersContainer.row();
		layers = new Table(skin);
		generateLayerList();

		layersContainer.add(layers).colspan(2);
		add(layersContainer).colspan(2);
		pack();
	}

	private void generateLayerList() {
		layers.clear();
		for (int i = 0; i < generator.getLayers().size(); i++) {
			Layer l = generator.getLayers().get(i);
			WorldLayerEntry worldLayerEntry = new WorldLayerEntry(skin, l, this);
			layers.add(worldLayerEntry).colspan(2).fill();
			layers.row();
		}
		pack();
	}

	public void reload() {
		generateLayerList();
	}

	@Override
	public void onMoveUp(String layerId) {
		moveLayer(layerId, -1);
	}

	private void moveLayer(String layerId, int offset) {
		List<Layer> layers = generator.getLayers();
		int addIndex = -1;
		Layer la = null;
		for (Layer l : layers) {
			if (l.getConfig().id.equals(layerId)) {
				la = l;
				int i = layers.indexOf(l);
				if (i + offset < layers.size() && i + offset >= 0) {
					addIndex = i + offset;
				}
			}
		}
		if (addIndex != -1) {
			layers.remove(la);
			layers.add(addIndex, la);
		}
		generateLayerList();
		Editor.post(new CommandGenerateWorld());
	}

	@Override
	public void onMoveDown(String layerId) {
		moveLayer(layerId, +1);
	}

	public void setWorldName(String name) {
		worldName.setText(name);
	}
}
