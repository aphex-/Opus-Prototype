package com.nukethemoon.tools.opusproto.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class UpDownElement extends Table {

	private ButtonClickListener listener;

	public UpDownElement(Skin skin) {
		super(skin);
		left().top();
		ImageButton upButton = new ImageButton(Styles.ICON_UP);
		upButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (listener != null) {
					listener.onUp();
				}
			}
		});
		add(upButton).size(25, 14).top();
		row();

		ImageButton downButton = new ImageButton(Styles.ICON_DOWN);
		downButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (listener != null) {
					listener.onDown();
				}
			}
		});
		add(downButton).size(25, 14).bottom();
		pack();
	}

	public void setListener(ButtonClickListener listener) {
		this.listener = listener;
	}

	public interface ButtonClickListener {
		void onDown();
		void onUp();
	}
}
