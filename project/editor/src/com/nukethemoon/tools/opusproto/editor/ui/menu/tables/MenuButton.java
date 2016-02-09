package com.nukethemoon.tools.opusproto.editor.ui.menu.tables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;

public class MenuButton extends Table {

	private MenuButtonClickListener clickListener;
	private MenuButtonMouseListener mouseListener;

	public MenuButton(String text, Skin skin) {

		Label textLabel = new Label(text, skin);
		add(textLabel).expand().expand().fill().left();

		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (clickListener != null) {
					clickListener.onClick();
				}
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				setBackground(Styles.SELECTED_BACKGROUND);
				if (mouseListener != null) {
					mouseListener.onHover();
				}
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				setBackground(Styles.TRANSPARENT_BACKGROUND);
				if (mouseListener != null) {
					mouseListener.onLeave();
				}
			}
		});
		pack();
	}

	public void setClickListener(MenuButtonClickListener listener) {
		this.clickListener = listener;
	}

	public void setMouseListener(MenuButtonMouseListener listener) {
		this.mouseListener = listener;
	}


	public interface MenuButtonClickListener {
		void onClick();
	}

	public interface MenuButtonMouseListener {
		void onHover();
		void onLeave();
	}
}
