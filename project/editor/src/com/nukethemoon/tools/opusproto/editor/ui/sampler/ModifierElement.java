package com.nukethemoon.tools.opusproto.editor.ui.sampler;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.UpDownElement;
import com.nukethemoon.tools.opusproto.sampler.SamplerModifier;

public class ModifierElement extends AbstractChangeForm {


	private final TextField textFieldValue;
	private final SelectBox<String> modifierSelectBox;
	private final UpDownElement upDownElement;
	private Skin skin;

	public ModifierElement(Skin skin) {
		super(skin);
		this.skin = skin;
		padRight(5); // hack for scrollbar
		padLeft(5);
		setBackground(Styles.ITEM_BACKGROUND);
		defaults().pad(2);

		upDownElement = new UpDownElement(skin);
		add(upDownElement);

		modifierSelectBox = new SelectBox<String>(skin);
		String[] modifierTypes = new String[SamplerModifier.Type.values().length];
		for (int i = 0; i < modifierTypes.length; i++) {
			modifierTypes[i] = SamplerModifier.Type.values()[i].name();
		}
		modifierSelectBox.setItems(modifierTypes);
		modifierSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				notifyChanges();
			}
		});
		add(modifierSelectBox);

		textFieldValue = new CursorTextField("", skin, this, 0.1f);
		textFieldValue.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				notifyChanges();
			}
		});
		add(textFieldValue).width(50);
	}

	public void setUpDownListener(UpDownElement.ButtonClickListener listener) {
		upDownElement.setListener(listener);
	}

	public void setType(SamplerModifier.Type type) {
		modifierSelectBox.setSelected(type.name());
	}

	public void setValue(float value) {
		textFieldValue.setText(value + "");
	}



	public TextField getTextField() {
		return textFieldValue;
	}

	public SamplerModifier.Type getSelectedType() {
		for (SamplerModifier.Type t : SamplerModifier.Type.values()) {
			if (t.name().equals(modifierSelectBox.getSelected())) {
				return t;
			}
		}
		return null;
	}

	public void addRemoveButton(ChangeListener listener) {
		TextButton removeButton = new TextButton("X", skin);
		removeButton.addListener(listener);
		add(removeButton);
		pack();
	}

}
