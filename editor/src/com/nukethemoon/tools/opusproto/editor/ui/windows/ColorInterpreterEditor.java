package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandRenameElement;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.BaseDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.RenameDialog;
import com.nukethemoon.tools.opusproto.editor.ui.layer.interpreter.ColorInterpreterItemForm;
import com.nukethemoon.tools.opusproto.interpreter.AbstractInterpreter;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreter;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreterItem;

public class ColorInterpreterEditor extends ClosableWindow {


	private final Skin skin;
	private final ColorInterpreter interpreter;
	private Table itemContainer;

	public ColorInterpreterEditor(final Skin skin, final ColorInterpreter interpreter) {
		super("COLOR INTERPRETER", skin);
		top().left();
		defaults().pad(4).top().left();
		this.skin = skin;
		this.interpreter = interpreter;

		Label labelName = new Label(interpreter.id, skin);
		labelName.setColor(Styles.INTERPRETER_COLOR);
		add(labelName).left().fill();

		TextButton helpButton = new TextButton("?", Styles.UI_SKIN);
		helpButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.openHelp("data/docu/interpreter.html");
			}
		});
		add(helpButton);

		TextButton textButtonRename = new TextButton("rename", skin);
		textButtonRename.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenameDialog renameDialog = new RenameDialog(skin, interpreter.id);
				renameDialog.setResultListener(new BaseDialog.ResultListener() {
					@Override
					public void onResult(Object result) {
						Editor.post(new CommandRenameElement(interpreter.id, (String) result,
								CommandRenameElement.ElementType.Interpreter));
					}
				});
				renameDialog.show(Editor.STAGE);
			}
		});
		add(textButtonRename).right();

		TextButton textButton = new TextButton("add item", skin);
		textButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				interpreter.items.add(new ColorInterpreterItem());
				generateItemList();
			}
		});
		add(textButton).right();

		row();

		itemContainer = new Table(skin);
		add(itemContainer).colspan(4);
		generateItemList();

	}

	private void generateItemList() {
		itemContainer.clear();
		for (final AbstractInterpreter.InterpreterItem item : interpreter.items) {
			ColorInterpreterItemForm colorInterpreterItemForm = new ColorInterpreterItemForm(skin,
					(ColorInterpreterItem) item);
			itemContainer.add(colorInterpreterItemForm);
			colorInterpreterItemForm.setColorItemListener(new ColorInterpreterItemForm.ColorItemListener() {
				@Override
				public void onRemove(ColorInterpreterItemForm form) {
					form.remove();
					interpreter.items.remove(item);
					pack();
				}

				@Override
				public void onChange(ColorInterpreterItemForm form) {
					if (AbstractChangeForm.containsValidHexColor(form.getStartColorTextField())) {
						Color color = AbstractChangeForm.parseColor(form.getStartColorTextField().getText());
						ColorInterpreterItem cItem = (ColorInterpreterItem) item;
						cItem.starColorR = color.r;
						cItem.starColorG = color.g;
						cItem.starColorB = color.b;
					}

					if (AbstractChangeForm.containsValidHexColor(form.getEndColorTextField())) {
						Color color = AbstractChangeForm.parseColor(form.getEndColorTextField().getText());
						ColorInterpreterItem cItem = (ColorInterpreterItem) item;
						cItem.endColorR = color.r;
						cItem.endColorG = color.g;
						cItem.endColorB = color.b;
					}

					if (AbstractChangeForm.containsValidFloat(form.getStartValueTextField())) {
						item.startValue = Float.parseFloat(form.getStartValueTextField().getText());
					}

					if (AbstractChangeForm.containsValidFloat(form.getEndValueTextField())) {
						item.endValue = Float.parseFloat(form.getEndValueTextField().getText());
					}
					generateItemList();
				}
			});
			itemContainer.row();
		}
		pack();
	}


}
