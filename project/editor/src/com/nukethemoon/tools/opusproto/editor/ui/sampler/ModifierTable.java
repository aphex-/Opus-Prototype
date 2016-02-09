package com.nukethemoon.tools.opusproto.editor.ui.sampler;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.editor.Util;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.UpDownElement;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.sampler.SamplerModifier;

import java.util.ArrayList;
import java.util.List;

public class ModifierTable extends AbstractChangeForm {

	private final ScrollPane pane;
	private List<ModifierElement> modifierList = new ArrayList<ModifierElement>();
	private Table itemContainer;
	private AbstractSampler sampler;

	public ModifierTable(Skin skin, AbstractSampler sampler) {
		super(skin);
		this.sampler = sampler;
		top().left();
		defaults().pad(2);
		add(new Label("modifier", skin)).left();

		TextButton helpButton = new TextButton("?", skin);
		helpButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.openHelp("data/docu/modifier.html");
			}
		});
		add(helpButton).left();

		final TextButton addModifierButton = new TextButton("add", skin);
		addModifierButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				addModifier(SamplerModifier.Type.Add, 0);
				itemContainer.pack();
				notifyChanges();
			}
		});
		add(addModifierButton).right();
		row();

		itemContainer = new Table(skin);
		itemContainer.left().top();
		pane = new ScrollPane(itemContainer, skin);
		pane.setVisible(false);
		Util.applyStandardSettings(pane);

		add(pane).colspan(3).width(228).fill().expand();
	}

	public void update(AbstractSampler sampler) {
		this.sampler = sampler;
		for (int i = 0; i < sampler.getConfig().modifiers.size(); i++) {
			SamplerModifier samplerModifier = sampler.getConfig().modifiers.get(i);
			addModifier(samplerModifier.type, samplerModifier.value);
		}
	}

	public void clearModifier() {
		itemContainer.clear();
		modifierList.clear();
		pane.setVisible(false);
	}

	public void addModifier(SamplerModifier.Type type, float value) {
		SamplerModifier samplerModifier = new SamplerModifier(type, value);

		final ModifierElement modifierElement = new ModifierElement(getSkin());
		modifierElement.addRemoveButton(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				removeModifier(modifierElement);
			}
		});
		modifierElement.setUpDownListener(new UpDownElement.ButtonClickListener() {
			@Override
			public void onDown() {
				moveModifier(false, modifierElement);
			}

			@Override
			public void onUp() {
				moveModifier(true, modifierElement);
			}
		});

		modifierElement.setType(samplerModifier.type);
		modifierElement.setValue(samplerModifier.value);
		modifierElement.addChangedListener(new ChangedListener() {
			@Override
			public void onChange() {
				notifyChanges();
			}
		});
		itemContainer.add(modifierElement);
		modifierList.add(modifierElement);
		pane.setVisible(true);
		itemContainer.row();
	}

	private void moveModifier(boolean up, ModifierElement element) {
		int i = modifierList.indexOf(element);
		if (up && i > 0) {
			modifierList.remove(element);
			modifierList.add(i - 1, element);
		}
		if (!up && i < modifierList.size() - 1) {
			modifierList.remove(element);
			modifierList.add(i + 1, element);
		}

		itemContainer.clear();
		for (ModifierElement e : modifierList) {
			itemContainer.add(e);
			itemContainer.row();
		}
		itemContainer.pack();

		notifyChanges();
	}

	private void swap(ModifierElement a, ModifierElement b) {
		Cell<ModifierElement> cellA = itemContainer.getCell(a);
		Cell<ModifierElement> cellB = itemContainer.getCell(b);
		cellA.setActor(b);
		cellB.setActor(a);
	}

	private void removeModifier(ModifierElement ui) {
		itemContainer.removeActor(ui);
		modifierList.remove(ui);
		if (modifierList.size() == 0) {
			pane.setVisible(false);
		}
		pack();
		notifyChanges();
	}

	public static boolean containsValidModifiers(List<ModifierElement> list) {
		for (ModifierElement modifierElement : list) {
			if (!containsValidFloat(modifierElement.getTextField())) {
				return false;
			}
		}
		return true;
	}


	public boolean containsValidModifiers() {
		return containsValidModifiers(modifierList);
	}

	public void applyModifiers(AbstractSamplerConfiguration config) {
		if (containsValidModifiers(modifierList)) {

			if (config.modifiers == null) {
				config.modifiers = new ArrayList<SamplerModifier>();
			}

			config.modifiers.clear();
			for (ModifierElement modifierElement : modifierList) {
				config.modifiers.add(new SamplerModifier(modifierElement.getSelectedType(),
						Float.parseFloat(modifierElement.getTextField().getText())));
			}

		}
	}
}
