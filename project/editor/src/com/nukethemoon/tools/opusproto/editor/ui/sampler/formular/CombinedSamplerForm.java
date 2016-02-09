package com.nukethemoon.tools.opusproto.editor.ui.sampler.formular;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.layer.EventLayersChanged;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.UpDownElement;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.AbstractSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.RangeTable;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.combined.SamplerChildForm;
import com.nukethemoon.tools.opusproto.sampler.combined.CombinedConfig;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class CombinedSamplerForm extends AbstractSamplerForm {

	private final RangeTable rangeTable;
	private final TextButton addButton;

	//private AbstractSampler[] subSamplers;
	private CombinedConfig config;

	private List<SamplerChildForm> samplerForms = new ArrayList<SamplerChildForm>();

	private Skin skin;
	private Samplers pool;

	private Table subSamplerContainer;

	public CombinedSamplerForm(Skin skin, AbstractSampler pSampler, final Samplers pool) {
		super(skin, null, pool);
		this.pool = pool;
		this.skin = skin;
		defaults().pad(2);

		setBackground(Styles.INNER_BACKGROUND);

		Table titleTable = new Table(skin);
		Label label =  new Label("Sampler", skin);
		titleTable.add(label).expand().left().fill();
		addButton = new TextButton("add", skin);
		addButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				addSampler(samplers.getSampler(Editor.DEFAULT_SAMPLER_NAME));
			}
		});
		titleTable.add(addButton).right();

		add(titleTable).fill().pad(4);
		row();

		subSamplerContainer = new Table(skin);
		add(subSamplerContainer);
		row();

		rangeTable = new RangeTable(skin);
		add(rangeTable).left();

		loadFromConfig(pSampler.getConfig());
		generateSamplerList();
		updateRangeInfo();

		pack();
	}

	private void addSampler(AbstractSampler sampler) {
		//subSamplers = ArrayUtils.add(subSamplers, sampler);
		config.samplerItems = ArrayUtils.add(config.samplerItems, new ChildSamplerConfig(sampler.getConfig().id));
		generateSamplerList();
		Editor.post(new EventLayersChanged());
	}

	private void removeSampler(int index) {
		//subSamplers = ArrayUtils.remove(subSamplers, index);
		config.samplerItems = ArrayUtils.remove(config.samplerItems, index);
		generateSamplerList();
		Editor.post(new EventLayersChanged());
	}

	private void generateSamplerList() {
		clearSamplerEntries();
		for (int i = 0; i < config.samplerItems.length; i++) {
			createSamplerEntry(config.samplerItems[i], i);
		}
		notifyChanges();
		updateRangeInfo();
	}

	private void createSamplerEntry(ChildSamplerConfig itemConfig, final int index) {

		final SamplerChildForm samplerChildForm = new SamplerChildForm(skin, samplers.getSampler(itemConfig.samplerReferenceId),
				Editor.STAGE, pool, itemConfig);

		samplerChildForm.setSamplerModListener(new SamplerChildForm.SamplerModListener() {
			@Override
			public void onScaleModChange(TextField textField) {
				if (containsValidFloat(textField)) {
					config.samplerItems[index].scaleModifier = Float.parseFloat(textField.getText());
					notifyChanges();
				}
			}

			@Override
			public void onSeedModChange(TextField textField) {
				if (containsValidDouble(textField)) {
					config.samplerItems[index].seedModifier = Double.parseDouble(textField.getText());
					notifyChanges();
				}
			}

			@Override
			public void onInvertChange(boolean invert) {
				config.samplerItems[index].invert = invert;
				notifyChanges();
			}

			@Override
			public void onMultiplyChange(TextField textField) {
				if (containsValidFloat(textField)) {
					config.samplerItems[index].multiply = Float.parseFloat(textField.getText());
					notifyChanges();
				}
			}
		});


		// remove a sampler
		samplerChildForm.addRemoveListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				removeSampler(index);
			}
		});

		// change a sampler
		samplerChildForm.addSamplerSelectionListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean canChange = canAddSamplerToSampler(pool.getSampler(config.id), samplerChildForm.getSelected());
				if (canChange) {
					config.samplerItems[index].samplerReferenceId = samplerChildForm.getSelected().getConfig().id;
					notifyChanges();
					updateRangeInfo();
					Editor.post(new EventLayersChanged());
				} else {
					// change back
					samplerChildForm.setSelected(pool.getSampler(config.samplerItems[index].samplerReferenceId));
				}
			}
		});

		samplerChildForm.addActiveListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				config.samplerItems[index].active = samplerChildForm.isActivated();
				notifyChanges();
				updateRangeInfo();
			}
		});

		samplerChildForm.addOperationListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				config.samplerItems[index].operator = samplerChildForm.getOperator();
				notifyChanges();
				updateRangeInfo();
			}
		});

		samplerChildForm.getUpDownElement().setListener(new UpDownElement.ButtonClickListener() {
			@Override
			public void onDown() {
				if (index < config.samplerItems.length - 1) {
					ChildSamplerConfig onIndexItem = config.samplerItems[index];
					ChildSamplerConfig overIndexItem = config.samplerItems[index + 1];
					config.samplerItems[index] = overIndexItem;
					config.samplerItems[index + 1] = onIndexItem;
					generateSamplerList();
				}
			}

			@Override
			public void onUp() {
				if (index > 0) {
					ChildSamplerConfig onIndexItem = config.samplerItems[index];
					ChildSamplerConfig overIndexItem = config.samplerItems[index - 1];
					config.samplerItems[index] = overIndexItem;
					config.samplerItems[index - 1] = onIndexItem;
					generateSamplerList();
				}
			}
		});

		samplerForms.add(samplerChildForm);
		subSamplerContainer.add(samplerChildForm);
		subSamplerContainer.row();
	}

	private void clearSamplerEntries() {
		for (SamplerChildForm form : samplerForms) {
			form.remove();
		}
		samplerForms.clear();
		pack();
	}

	public void updateRangeInfo() {
		ChildSamplerConfig[] items = config.samplerItems;
		AbstractSampler s = pool.getSampler(config.id);

		if (items != null && items.length > 0) {
			float maxValue = s.getMaxSample();
			float minValue = s.getMinSample();
			rangeTable.setVisible(true);
			rangeTable.setRange(minValue, maxValue);
		} else {
			rangeTable.setVisible(false);
		}
	}

	@Override
	public boolean applyToConfig(AbstractSamplerConfiguration pConfig) {
		((CombinedConfig) pConfig).samplerItems = config.samplerItems;
		return true;
	}

	@Override
	public void loadFromConfig(AbstractSamplerConfiguration pConfig) {
		config = (CombinedConfig) pConfig;
		generateSamplerList();
	}

}
