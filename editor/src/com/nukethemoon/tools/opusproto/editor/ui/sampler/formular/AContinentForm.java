package com.nukethemoon.tools.opusproto.editor.ui.sampler.formular;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.AbstractSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.InputTable;
import com.nukethemoon.tools.opusproto.sampler.acontinent.AContinentConfig;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public class AContinentForm extends AbstractSamplerForm {

	private final InputTable inputTable;

	private final String NAME_ITERATIONS = "iterations";
	private final String NAME_GROWTH = "itr. growth";
	private final String NAME_SIZE = "size";
	private final String NAME_EDGE = "edge";
	private final CheckBox cbSmoothEdge;

	public AContinentForm(Skin skin, AbstractSampler sampler, Samplers pool) {
		super(skin, sampler, pool);

		inputTable = new InputTable(skin);
		inputTable.setBackground(Styles.INNER_BACKGROUND);
		inputTable.addEntry(NAME_ITERATIONS, 60);
		inputTable.addEntry(NAME_GROWTH, 60);
		inputTable.addEntry(NAME_SIZE, 60);
		inputTable.addEntry(NAME_EDGE, 60);

		inputTable.setEntryValueListener(new InputTable.EntryValueListener() {
			@Override
			public void onChange(String entryName, String entryValue) {
				notifyChanges();
			}
		});

		add(inputTable);

		row();
		cbSmoothEdge = new CheckBox("Smooth edge", skin);
		cbSmoothEdge.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				notifyChanges();
			}
		});
		add(cbSmoothEdge);
	}

	@Override
	public boolean applyToConfig(AbstractSamplerConfiguration config) {
		AContinentConfig c = ((AContinentConfig) config);

		if (containsValidInteger(inputTable.getTextField(NAME_ITERATIONS))) {
			if (containsValidFloat(inputTable.getTextField(NAME_GROWTH))) {
				if (containsValidFloat(inputTable.getTextField(NAME_SIZE))) {
					if (containsValidFloat(inputTable.getTextField(NAME_EDGE))) {
						c.iterations = Integer.parseInt(inputTable.getTextField(NAME_ITERATIONS).getText());
						c.growth = Float.parseFloat(inputTable.getTextField(NAME_GROWTH).getText());
						c.size = Float.parseFloat(inputTable.getTextField(NAME_SIZE).getText());
						c.edge = Float.parseFloat(inputTable.getTextField(NAME_EDGE).getText());
						c.smoothEdge = cbSmoothEdge.isChecked();
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void loadFromConfig(AbstractSamplerConfiguration config) {
		AContinentConfig c = ((AContinentConfig) config);
		inputTable.getTextField(NAME_ITERATIONS).setText(c.iterations + "");
		inputTable.getTextField(NAME_GROWTH).setText(c.growth + "");
		inputTable.getTextField(NAME_SIZE).setText(c.size + "");
		inputTable.getTextField(NAME_EDGE).setText(c.edge + "");
		cbSmoothEdge.setChecked(c.smoothEdge);
	}
}
