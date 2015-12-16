package com.nukethemoon.tools.opusproto.editor.ui.sampler.formular;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.Samplers;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.message.layer.EventLayersChanged;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.AbstractSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.SamplerSelectBoxElement;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;

public class MaskedSamplerForm extends AbstractSamplerForm {

	private final SamplerSelectBoxElement maskSelector;
	private final SamplerSelectBoxElement samplerSelector;

	public MaskedSamplerForm(Skin skin, final AbstractSampler sampler, final Samplers pool) {
		super(skin, sampler, pool);
		top().left();
		defaults().pad(2);

		Label maskLabel = new Label("Mask", skin);
		add(maskLabel).left();
		row();

		Table maskTable = new Table(skin);
		maskTable.defaults().pad(2);
		maskTable.setBackground(Styles.ITEM_BACKGROUND);
		maskSelector = new SamplerSelectBoxElement(skin, Editor.STAGE, pool, true);
		maskSelector.addChangedListener(new ChangedListener() {
			@Override
			public void onChange() {
				boolean canChange = canAddSamplerToSampler(sampler, maskSelector.getSelected());

				if (canChange) {
					notifyChanges();
					Editor.post(new EventLayersChanged());
				} else {
					String samplerReferenceId = ((MaskedSamplerConfig) sampler.getConfig()).samplerItems[0].samplerReferenceId;
					maskSelector.setSelected(pool.getSampler(samplerReferenceId));
				}
			}
		});
		maskTable.add(maskSelector);
		TextButton editMaskButton = new TextButton("edit", skin);
		editMaskButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandOpenSamplerEditor(maskSelector.getSelected().getConfig().id));
			}
		});
		maskTable.add(editMaskButton);
		add(maskTable);
		row();

		Label samplerLabel = new Label("Sampler", skin);
		add(samplerLabel).left();
		row();
		Table samplerTable = new Table(skin);
		samplerTable.defaults().pad(2);
		samplerTable.setBackground(Styles.ITEM_BACKGROUND);
		samplerSelector = new SamplerSelectBoxElement(skin, Editor.STAGE, pool, true);
		samplerSelector.addChangedListener(new ChangedListener() {
			@Override
			public void onChange() {
				boolean canChange = canAddSamplerToSampler(sampler, samplerSelector.getSelected());
				if (canChange) {
					notifyChanges();
					Editor.post(new EventLayersChanged());
				} else {
					String samplerReferenceId = ((MaskedSamplerConfig) sampler.getConfig()).samplerItems[1].samplerReferenceId;
					samplerSelector.setSelected(pool.getSampler(samplerReferenceId));
				}
			}
		});
		samplerTable.add(samplerSelector);
		TextButton editSamplerButton = new TextButton("edit", skin);
		editSamplerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandOpenSamplerEditor(samplerSelector.getSelected().getConfig().id));
			}
		});
		samplerTable.add(editSamplerButton);
		add(samplerTable);
		row();

		loadFromConfig(sampler.getConfig());
		notifyChanges();
	}

	@Override
	public boolean applyToConfig(AbstractSamplerConfiguration config) {
		MaskedSamplerConfig c = (MaskedSamplerConfig) config;
		if (c.samplerItems == null || c.samplerItems.length < 2) {
			c.samplerItems = new ChildSamplerConfig[2];
		}
		c.samplerItems[0] = new ChildSamplerConfig(maskSelector.getSelected().getConfig().id);
		c.samplerItems[1] = new ChildSamplerConfig(samplerSelector.getSelected().getConfig().id);
		return true;
	}

	@Override
	public void loadFromConfig(AbstractSamplerConfiguration config) {
		MaskedSamplerConfig c = (MaskedSamplerConfig) config;
		if (c.samplerItems != null && c.samplerItems.length > 0) {
			maskSelector.setSelected(samplers.getSampler(c.samplerItems[0].samplerReferenceId));
			if (c.samplerItems.length > 1) {
				samplerSelector.setSelected(samplers.getSampler(c.samplerItems[1].samplerReferenceId));
			}
		}
	}
}
