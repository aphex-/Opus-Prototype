package com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.noise;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.AbstractSamplerForm;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.sampler.noise.NoiseConfig;


public class NoiseSamplerForm extends AbstractSamplerForm {

	private final SelectBox algorithmSelect;
	private Skin skin;

	public NoiseSamplerForm(Skin skin, AbstractSampler sampler, Samplers pool) {
		super(skin, sampler, pool);
		this.skin = skin;
		defaults().center().pad(5);
		setBackground(Styles.INNER_BACKGROUND);


		Label algorithmLabel = new Label("algorithm", skin);
		add(algorithmLabel);

		algorithmSelect = new SelectBox(skin);
		String[] algorithmNames = new String[Editor.KNOWN_ALGORITHMS.keySet().size()];
		Editor.KNOWN_ALGORITHMS.keySet().toArray(algorithmNames);
		algorithmSelect.setItems(algorithmNames);
		algorithmSelect.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				notifyChanges();
			}
		});

		add(algorithmSelect);


		loadFromConfig(sampler.getConfig());
		notifyChanges();
	}

	@Override
	public boolean applyToConfig(AbstractSamplerConfiguration config) {
		NoiseConfig c = (NoiseConfig) config;
		c.noiseAlgorithmName = (String) algorithmSelect.getSelected();
		return true;
	}


	@Override
	public void loadFromConfig(AbstractSamplerConfiguration config) {
		NoiseConfig c = (NoiseConfig) config;
		algorithmSelect.setSelected(c.noiseAlgorithmName);

	}



}
