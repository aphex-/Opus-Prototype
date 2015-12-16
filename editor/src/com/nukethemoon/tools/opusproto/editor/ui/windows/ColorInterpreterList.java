package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandDeleteInterpreter;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandOpenInterpreterEditor;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.EventInterpreterPoolChanged;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.NewInterpreterDialog;
import com.nukethemoon.tools.opusproto.interpreter.AbstractInterpreter;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreter;
import com.squareup.otto.Subscribe;

public class ColorInterpreterList extends ClosableWindow {

	private final Label labelTitle;
	private Table container;
	private Samplers loader;

	public ColorInterpreterList(final Skin skin, Samplers loader) {
		super("INTERPRETER LIST", skin);
		this.loader = loader;
		top().left();
		defaults().pad(2);

		container = new Table(skin);
		container.setBackground(Styles.INNER_BACKGROUND);

		labelTitle = new Label("", skin);
		add(labelTitle).left().fill().expand();

		TextButton addButton = new TextButton("create", skin);
		addButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NewInterpreterDialog dialog = new NewInterpreterDialog(skin);
				dialog.show(Editor.STAGE);
			}
		});
		add(addButton).right();
		row();

		add(container).colspan(2);
		generateList();
	}

	private void generateList() {
		container.clear();
		AbstractInterpreter[] list = loader.createInterpreterList();
		for (AbstractInterpreter interpreter : list) {
			if (interpreter instanceof ColorInterpreter) {
				addNewInterpreter((ColorInterpreter) interpreter);
			}
		}
		labelTitle.setText("Interpreter (" + loader.getInterpreterCount() + ")");
		pack();
	}

	private void addNewInterpreter(ColorInterpreter interpreter) {
		container.add(new InterpreterListItem(interpreter, getSkin())).fill();
		container.row();
		container.pack();
		pack();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void regenerateList(EventInterpreterPoolChanged event) {
		generateList();
	}


	public static class InterpreterListItem extends Table {

		public InterpreterListItem(final ColorInterpreter interpreter, Skin skin) {
			super(skin);
			top().left();
			defaults().pad(2);
			setBackground(Styles.ITEM_BACKGROUND);

			Label nameLabel = new Label(interpreter.id, skin);
			nameLabel.setColor(Styles.INTERPRETER_COLOR);
			add(nameLabel).expand().fill().left();

			TextButton editButton = new TextButton("edit", skin);
			editButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Editor.post(new CommandOpenInterpreterEditor(interpreter));
				}
			});
			add(editButton).right();

			TextButton deleteButton = new TextButton("delete", skin);
			deleteButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Editor.post(new CommandDeleteInterpreter(interpreter.id));
				}
			});
			add(deleteButton).right();
			pack();
		}

	}
}
