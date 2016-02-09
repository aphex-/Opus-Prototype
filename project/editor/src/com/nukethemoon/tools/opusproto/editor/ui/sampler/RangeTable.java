package com.nukethemoon.tools.opusproto.editor.ui.sampler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;

public class RangeTable extends Table {

	private Label lowValue;
	private Label highLabel;

	public RangeTable(Skin skin) {
		super(skin);
		setBackground(Styles.INNER_BACKGROUND);

		Label titleLabel = new Label("range:", skin);
		add(titleLabel).left();

		lowValue = new Label("", skin);
		add(lowValue);

		Label toLabel = new Label("to", skin);
		add(toLabel);

		highLabel = new Label("", skin);
		add(highLabel);
	}

	public void setRange(float low, float high) {
		lowValue.setText(String.format("%.3f", low));
		setValueColor(lowValue, low);
		highLabel.setText(String.format("%.3f", high));
		setValueColor(highLabel, high);
	}

	private void setValueColor(Label label, float value) {
		if (value < 0) {
			label.setColor(Color.GREEN);
			return;
		}
		if (value > 1) {
			label.setColor(Color.RED);
			return;
		}
		if (value == 1 || value == 0) {
			label.setColor(Color.WHITE);
			return;
		}
		label.setColor(Color.LIGHT_GRAY);
	}
}
