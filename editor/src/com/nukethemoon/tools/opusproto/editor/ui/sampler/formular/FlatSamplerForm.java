package com.nukethemoon.tools.opusproto.editor.ui.sampler.formular;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.AbstractSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.CursorTextField;
import com.nukethemoon.tools.opusproto.sampler.flat.FlatSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public class FlatSamplerForm extends AbstractSamplerForm {

	private final TextField valueTextField;

	public FlatSamplerForm(Skin skin, AbstractSampler sampler, SamplerLoader pool) {
		super(skin, sampler, pool);
		defaults().center().pad(5);
		setBackground(Styles.INNER_BACKGROUND);


		Table valueTable = new Table(skin);
		valueTable.defaults().pad(2);
		Label label = new Label("value", skin);
		valueTable.add(label);

		valueTextField = new CursorTextField("", skin, this, 0.1f);
		valueTable.add(valueTextField).width(50);

		valueTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				notifyChanges();
			}
		});
		add(valueTable);
		row();

		loadFromConfig(sampler.getConfig());
		notifyChanges();
	}

	@Override
	public boolean applyToConfig(AbstractSamplerConfiguration config) {
		FlatSamplerConfig c = (FlatSamplerConfig) sampler.getConfig();

		boolean validRes = containsValidFloat(valueTextField);
		if (validRes) {
			c.value = Float.parseFloat(valueTextField.getText());
			return true;
		}
		return false;
	}

	@Override
	public void loadFromConfig(AbstractSamplerConfiguration config) {
		valueTextField.setText(((FlatSamplerConfig) config).value + "");
	}
}
