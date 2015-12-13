package com.nukethemoon.tools.opusproto.editor.ui.layer;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.UpDownElement;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.RangeTable;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.combined.SamplerChildForm;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.layer.LayerConfig;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;
import org.apache.commons.lang3.ArrayUtils;

public class LayerSamplerList extends AbstractChangeForm {

	private final RangeTable rangeLabel;
	private final TextButton addButton;

	private Skin skin;
	private Stage stage;
	private SamplerLoader samplerLoader;
	private Layer layer;

	private Table container;

	public LayerSamplerList(Skin skin, final Layer layer,
							Stage stage, SamplerLoader samplerLoader) {
		super(skin);
		this.layer = layer;

		this.stage = stage;
		this.samplerLoader = samplerLoader;
		this.skin = skin;
		defaults().pad(2);

		setBackground(Styles.INNER_BACKGROUND);

		Table titleTable = new Table(skin);
		Label label =  new Label("MaskedSampler", skin);
		titleTable.add(label).expand().left().fill();
		addButton = new TextButton("add", skin);
		addButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				addNewMaskedSampler();
			}
		});
		titleTable.add(addButton).right();

		add(titleTable).fill().pad(4);
		row();

		container = new Table(skin);
		add(container);
		generateSamplerList();
		row();
		rangeLabel = new RangeTable(skin);
		add(rangeLabel).left();
		updateRangeInfo();
		pack();
	}

	private void addNewMaskedSampler() {
		LayerConfig config = (LayerConfig) layer.getConfig();
		ChildSamplerConfig childSamplerConfig
				= new ChildSamplerConfig(Editor.DEFAULT_MASK_SAMPLER_NAME);
		config.samplerItems = ArrayUtils.add(config.samplerItems, childSamplerConfig);
		generateSamplerList();
	}

	private void addMaskedSamplerEntry(AbstractSampler sampler, final int index) {

		LayerConfig config = (LayerConfig) layer.getConfig();

		final SamplerChildForm samplerChildForm = new SamplerChildForm(skin, sampler, stage, samplerLoader, null, MaskedSampler.class);

		samplerChildForm.addChangedListener(new AbstractChangeForm.ChangedListener() {
			@Override
			public void onChange() {
				LayerConfig config = (LayerConfig) layer.getConfig();
				config.samplerItems[index].samplerReferenceId = samplerChildForm.getSelected().getConfig().id;
				notifyChanges();
			}
		});

		samplerChildForm.addRemoveListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				removeSampler(index);
			}
		});

		samplerChildForm.addActiveListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				LayerConfig config = (LayerConfig) layer.getConfig();
				config.samplerItems[index].active = samplerChildForm.isActivated();
				notifyChanges();
			}
		});

		samplerChildForm.addSamplerSelectionListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				/*if (changeSamplerListener != null) {
					SelectBox b = (SelectBox) actor;
					AbstractSampler s = (AbstractSampler) b.getSelected();
					changeSamplerListener.onChange(finalI, s.getConfig().id);
				}*/
				updateRangeInfo();
			}
		});

		samplerChildForm.getUpDownElement().setListener(new UpDownElement.ButtonClickListener() {
			@Override
			public void onDown() {
				moveMaskSampler(false, index);
			}

			@Override
			public void onUp() {
				moveMaskSampler(true, index);
			}
		});
		samplerChildForm.setChecked(config.samplerItems[index].active);

		samplerChildForm.updatePreview();
		container.add(samplerChildForm);
		container.row();
		container.pack();
		pack();
	}


	private void moveMaskSampler(boolean up, int index) {
		LayerConfig config = (LayerConfig) layer.getConfig();

		int offset = 0;

		if (up && index > 0) {
			offset = -1;
		}
		if (!up && index < config.samplerItems.length - 1) {
			offset = + 1;
		}

		ChildSamplerConfig m1 = config.samplerItems[index];
		ChildSamplerConfig m2 = config.samplerItems[index + offset];
		config.samplerItems[index] = m2;
		config.samplerItems[index + offset] = m1;


		generateSamplerList();
	}

	private void generateSamplerList() {
		container.clear();
		LayerConfig config = (LayerConfig) layer.getConfig();
		for (int i = 0; i < config.samplerItems.length; i++) {
			final AbstractSampler sampler = samplerLoader.getSampler(config.samplerItems[i].samplerReferenceId);
			addMaskedSamplerEntry(sampler, i);
		}
		notifyChanges();
	}

	public void updateRangeInfo() {
		rangeLabel.setRange(layer.getMinSample(), layer.getMaxSample());
	}


	private void removeSampler(int i) {
		LayerConfig config = (LayerConfig) layer.getConfig();
		config.samplerItems = ArrayUtils.remove(config.samplerItems, i);
		generateSamplerList();
	}

	public void updatePreviews() {
		for (Actor actor : getChildren()) {
			if (actor instanceof SamplerChildForm) {
				((SamplerChildForm) actor).updatePreview();
			}
		}
	}

	public void reloadSamplers() {
		for (Actor actor : getChildren()) {
			if (actor instanceof SamplerChildForm) {
				((SamplerChildForm) actor).reloadSamplers();
			}
		}
	}

}
