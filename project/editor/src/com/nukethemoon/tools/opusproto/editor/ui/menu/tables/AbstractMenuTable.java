package com.nukethemoon.tools.opusproto.editor.ui.menu.tables;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;

public class AbstractMenuTable extends Table {

	protected CloseListener listener;
	protected Table contentTable;

	public AbstractMenuTable(Skin skin, String title) {
		super(skin);

		contentTable = new Table(skin);
		contentTable.setVisible(false);
		contentTable.padTop(4);
		contentTable.defaults().pad(2);
		contentTable.setBackground(Styles.INNER_BACKGROUND);

		Table titleTable = new Table(skin);
		MenuButton textButton = new MenuButton(title, skin);
		textButton.setClickListener(new MenuButton.MenuButtonClickListener() {
			@Override
			public void onClick() {
				contentTable.setVisible(!contentTable.isVisible());
				pack();
			}
		});

		textButton.setMouseListener(new MenuButton.MenuButtonMouseListener() {
			@Override
			public void onHover() {
				//contentTable.setVisible(true);
			}

			@Override
			public void onLeave() {

			}
		});

		titleTable.setBackground(Styles.STANDARD_BACKGROUND);
		titleTable.add(textButton).expand().left().fill();
		add(titleTable).expand().left().fill();
		row();

		add(contentTable);
	}

	protected void handleClose() {
		contentTable.setVisible(false);
		if (listener != null) {
			listener.onClose();
		}
	}

	public void setCloseListener(CloseListener listener) {
		this.listener = listener;
	}

	public interface CloseListener {
		void onClose();
	}
}
