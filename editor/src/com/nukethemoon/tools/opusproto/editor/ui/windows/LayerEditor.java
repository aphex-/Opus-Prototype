package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandDeleteLayer;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandOpenInterpreterEditor;
import com.nukethemoon.tools.opusproto.editor.message.CommandGenerateWorld;
import com.nukethemoon.tools.opusproto.editor.message.CommandLimitWindowSizes;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandOpenLayerEditor;
import com.nukethemoon.tools.opusproto.editor.message.CommandRenameElement;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.EventInterpreterPoolChanged;
import com.nukethemoon.tools.opusproto.editor.message.layer.EventLayersChanged;
import com.nukethemoon.tools.opusproto.editor.message.sampler.EventSamplerPoolChanged;
import com.nukethemoon.tools.opusproto.editor.message.sampler.EventSamplerUpdated;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.BaseDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.RenameDialog;
import com.nukethemoon.tools.opusproto.editor.ui.layer.LayerSamplerList;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.generator.WorldGenerator;
import com.nukethemoon.tools.opusproto.interpreter.AbstractInterpreter;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreter;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.layer.LayerConfig;
import com.nukethemoon.tools.opusproto.tools.Log;
import com.squareup.otto.Subscribe;

import java.util.List;

public class LayerEditor extends ClosableWindow implements AbstractChangeForm.ChangedListener {

	private final Label layerName;
	private final SelectBox<AbstractInterpreter> interpreterSelect;

	private LayerSamplerList samplerList;
	private Layer layer;

	private Table samplerContainer;
	private Skin skin;
	private final Stage stage;
	private final SamplerLoader pool;
	private WorldGenerator generator;

	public LayerEditor(final Skin skin, Layer layer, final Stage stage, SamplerLoader pool, WorldGenerator generator) {
		super("LAYER", skin);
		this.generator = generator;
		this.layer = layer;
		this.skin = skin;
		this.stage = stage;
		this.pool = pool;

		defaults().pad(2);

		layerName = new Label(layer.getConfig().id, skin);
		layerName.setColor(Styles.LAYER_COLOR);
		add(layerName).left().fill().expand();

		TextButton helpButton = new TextButton("?", Styles.UI_SKIN);
		helpButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.openHelp("data/docu/layer.html");
			}
		});
		add(helpButton);

		TextButton renameButton = new TextButton("rename", skin);
		renameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				openRenameLayerDialog();
			}
		});
		add(renameButton).right();
		row();

		samplerContainer = new Table(skin);
		samplerContainer.defaults().pad(2);
		add(samplerContainer).colspan(4).fill();
		row();

		Table interpreterTable = new Table(skin);
		interpreterTable.setBackground(Styles.INNER_BACKGROUND);
		interpreterTable.top();
		interpreterTable.defaults().left().pad(2);
		add(interpreterTable).colspan(4).padTop(8).expand().fill();

		Label interpreterTitle = new Label("Interpreter", skin);
		interpreterTable.add(interpreterTitle).left().expand().fill();

		interpreterSelect = new SelectBox<AbstractInterpreter>(Styles.INTERPRETER_SELECT_BOX_SKIN);
		interpreterSelect.setItems(pool.createInterpreterList());
		interpreterSelect.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				applyInterpreter(interpreterSelect.getSelected().id);
			}
		});
		interpreterTable.add(interpreterSelect);

		TextButton editInterpreter = new TextButton("edit", skin);
		editInterpreter.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandOpenInterpreterEditor((ColorInterpreter) interpreterSelect.getSelected()));
			}
		});
		interpreterTable.add(editInterpreter).right();

		generateLayerUI();
		pack();
	}

	private void openRenameLayerDialog() {
		RenameDialog renameDialog = new RenameDialog(skin, layer.getConfig().id);
		renameDialog.setResultListener(new BaseDialog.ResultListener() {
			@Override
			public void onResult(Object result) {
				Editor.post(new CommandRenameElement(layer.getConfig().id, (String) result,
						CommandRenameElement.ElementType.Layer));
				Editor.post(new EventLayersChanged());
			}
		});
		renameDialog.show(stage);
	}

	private void applyInterpreter(String id) {
		((LayerConfig) layer.getConfig()).interpreterId = id;
		try {
			layer.loadConfig();
		} catch (SamplerInvalidConfigException e) {
			Log.e(LayerEditor.class, "Error selecting interpreter. " + e.getMessage());
		}
	}

	private void generateLayerUI() {
		LayerConfig config = (LayerConfig) layer.getConfig();
		layerName.setText(layer.getConfig().id);

		samplerContainer.clear();
		samplerList = new LayerSamplerList(skin, layer, stage, pool);
		samplerContainer.add(samplerList).expand().fill();
		samplerList.addChangedListener(this);

		interpreterSelect.setItems(pool.createInterpreterList());
		interpreterSelect.setSelected(layer.getInterpreter());

		layerName.setText(config.id);
		pack();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void openLayer(CommandOpenLayerEditor command) {
		if (!this.isVisible()) {
			setVisible(true);
		}
		List<Layer> layers = generator.getLayers();
		if (layers != null) {
			for (Layer l : layers) {
				if (l.getConfig().id.equals(command.layerId)) {
					layer = l;
				}
			}
		}
		generateLayerUI();
		toFront();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void onEvent(EventInterpreterPoolChanged event) {
		interpreterSelect.setItems(pool.createInterpreterList());
		interpreterSelect.setSelected(layer.getInterpreter());
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void updatePreview(EventSamplerUpdated command) {
		samplerList.updatePreviews();
		samplerList.updateRangeInfo();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void reloadSamplers(EventSamplerPoolChanged command) {
		samplerList.reloadSamplers();
		samplerList.updateRangeInfo();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void onDeleteLayer(CommandDeleteLayer command) {
		if (layer.getConfig().id.equals(command.layerId)) {
			setVisible(false);
		}
	}

	@Override
	public void onChange() {
		try {
			layer.init();
		} catch (SamplerInvalidConfigException e) {
			Editor.showErrorDialog("Exception while updating layer. " + e.getMessage());
			e.printStackTrace();
		}
		samplerList.updateRangeInfo();
		samplerList.updatePreviews();
		pack();
		Editor.post(new CommandGenerateWorld());
		Editor.post(new EventLayersChanged());
		Editor.post(new CommandLimitWindowSizes());
	}



}
