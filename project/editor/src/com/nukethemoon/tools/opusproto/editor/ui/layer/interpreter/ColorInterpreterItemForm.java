package com.nukethemoon.tools.opusproto.editor.ui.layer.interpreter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.CursorTextField;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreterItem;

public class ColorInterpreterItemForm extends AbstractChangeForm {

	private final TextField startColorTextField;
	private final TextField endColorTextField;

	private final CursorTextField endValueTextField;
	private final CursorTextField startValueTextField;

	private ColorInterpreterItemForm.ColorItemListener listener;

	public ColorInterpreterItemForm(Skin skin, ColorInterpreterItem item) {
		super(skin);
		top().left();
		defaults().pad(2).top().left();
		setBackground(Styles.ITEM_BACKGROUND);

		add(new Label("value", skin));
		startValueTextField = new CursorTextField(item.startValue + "", skin, null, 0.1f);
		startValueTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (listener != null) {
					listener.onChange(ColorInterpreterItemForm.this);
				}
			}
		});
		add(startValueTextField).width(50);

		add(new Label("-", skin));
		endValueTextField = new CursorTextField(item.endValue + "", skin, null, 0.1f);
		endValueTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (listener != null) {
					listener.onChange(ColorInterpreterItemForm.this);
				}
			}
		});
		add(endValueTextField).width(50);


		add(new Label("to color", skin));

		Image startColorImage = new Image();
		Color startColor = new Color(item.starColorR, item.starColorG, item.starColorB, 1);
		fillImage(startColorImage, startColor);
		add(startColorImage);
		String startColorHex = startColor.toString().toUpperCase().substring(0, 6);
		startColorTextField = new TextField("#" + startColorHex, skin);
		startColorTextField.setMaxLength(7);
		startColorTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (containsValidHexColor(startColorTextField)) {
					if (listener != null) {
						listener.onChange(ColorInterpreterItemForm.this);
					}
				}
			}
		});
		add(startColorTextField).width(80);

		add(new Label("-", skin));
		Image endColorImage = new Image();
		Color endColor = new Color(item.endColorR, item.endColorG, item.endColorB, 1);
		fillImage(endColorImage, endColor);
		add(endColorImage);
		String endColorHex = endColor.toString().toUpperCase().substring(0, 6);
		endColorTextField = new TextField("#" + endColorHex, skin);
		endColorTextField.setMaxLength(7);
		endColorTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (containsValidHexColor(endColorTextField)) {
					if (listener != null) {
						listener.onChange(ColorInterpreterItemForm.this);
					}
				}
			}
		});
		add(endColorTextField).width(80);

		TextButton removeItemButton = new TextButton("x", skin);
		removeItemButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (listener != null) {
					listener.onRemove(ColorInterpreterItemForm.this);
				}
			}
		});
		add(removeItemButton);

		pack();
	}

	public CursorTextField getEndValueTextField() {
		return endValueTextField;
	}

	public CursorTextField getStartValueTextField() {
		return startValueTextField;
	}

	private void fillImage(Image image, Color color) {
		Pixmap pixmap = new Pixmap(30, 30, Pixmap.Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				pixmap.drawPixel(x, y, Color.argb8888(color.r, color.g, color.b, color.a));
			}
		}
		image.setDrawable(new SpriteDrawable(new Sprite(new Texture(pixmap))));
	}

	public TextField getStartColorTextField() {
		return startColorTextField;
	}

	public TextField getEndColorTextField() {
		return endColorTextField;
	}

	public void setColorItemListener(ColorItemListener listener) {
		this.listener = listener;
	}

	public interface ColorItemListener {
		void onRemove(ColorInterpreterItemForm form);
		void onChange(ColorInterpreterItemForm form);
	}
}
