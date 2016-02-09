package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public abstract class ClosableWindow extends Window {

	private WindowCloseListener closeListener;

	public ClosableWindow(String title, Skin skin) {
		super(title, skin);
		final TextButton closeButton = new TextButton("x", skin);
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (closeListener != null) {
					closeListener.onClose();
				}
				setVisible(false);
			}
		});
		getTitleTable().add(closeButton).height(18);
	}


	public void setCloseListener(WindowCloseListener listener) {
		this.closeListener = listener;
	}

	public interface WindowCloseListener {
		void onClose();
	}
}
