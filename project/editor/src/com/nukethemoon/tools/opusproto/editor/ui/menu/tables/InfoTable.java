package com.nukethemoon.tools.opusproto.editor.ui.menu.tables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;

public class InfoTable extends Table {

	private final Label positionLabelX;
	private final Label positionLabelY;
	private final Label tileValueLabel;

	public InfoTable(Skin skin) {
		super(skin);
		top().left();
		setBackground(Styles.INNER_BACKGROUND);

		add(new Label("x", skin));
		positionLabelX = new Label("", skin);
		positionLabelX.setColor(Color.LIGHT_GRAY);
		add(positionLabelX).left().width(45);

		add(new Label("y", skin));
		positionLabelY = new Label("", skin);
		positionLabelY.setColor(Color.LIGHT_GRAY);
		add(positionLabelY).left().width(45);

		add(new Label("v", skin));
		tileValueLabel = new Label("", skin);
		tileValueLabel.setColor(Color.LIGHT_GRAY);
		add(tileValueLabel).left().width(60);
		pack();
	}

	public void setHoverValue(int x, int y, float value) {
		positionLabelX.setText(x + "");
		positionLabelY.setText(y + "");
		tileValueLabel.setText(String.format("%.3f", value));
		pack();
	}
}
