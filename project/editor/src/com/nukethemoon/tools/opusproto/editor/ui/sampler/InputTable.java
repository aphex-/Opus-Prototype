package com.nukethemoon.tools.opusproto.editor.ui.sampler;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.HashMap;
import java.util.Map;

public class InputTable extends Table {

	public Map<String, TextField> nameToTextField = new HashMap<String, TextField>();
	private InputTable.EntryValueListener listener;

	public InputTable(Skin skin) {
		super(skin);
		defaults().pad(2);
	}

	public void addEntry(String name) {
		addEntry(name, -1);
	}

	public void addEntry(final String name, int width) {
		final TextField textField = new TextField("", getSkin());
		nameToTextField.put(name, textField);
		add(new Label(name, getSkin())).fill().left();
		if (width != -1) {
			add(textField).fill().right().width(width);
		} else {
			add(textField).fill().right();
		}

		textField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (listener != null) {
					listener.onChange(name, textField.getText());
				}
			}
		});

		row();
		pack();
	}

	public TextField getTextField(String name) {
		return nameToTextField.get(name);
	}

	public void setEntryValueListener(EntryValueListener listener) {
		this.listener = listener;
	}

	public interface EntryValueListener {
		void onChange(String entryName, String entryValue);
	}
}
