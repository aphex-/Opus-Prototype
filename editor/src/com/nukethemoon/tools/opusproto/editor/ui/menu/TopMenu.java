package com.nukethemoon.tools.opusproto.editor.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.Config;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandGenerateWorld;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.menu.tables.FileTable;
import com.nukethemoon.tools.opusproto.editor.ui.menu.tables.HelpTable;
import com.nukethemoon.tools.opusproto.editor.ui.menu.tables.InfoTable;
import com.nukethemoon.tools.opusproto.editor.ui.menu.tables.SnapshotTable;
import com.nukethemoon.tools.opusproto.editor.ui.menu.tables.WindowTable;
import com.nukethemoon.tools.opusproto.editor.ui.windows.LayerEditor;

public class TopMenu extends Table {

	private final InfoTable infoTable;
	private final SnapshotTable snapshotTable;

	public TopMenu(Skin skin, final LayerEditor layerEditor, final Table samplerUI, final Table worldUI, Samplers samplers) {
		super(skin);
		pad(2);
		setBackground(Styles.STANDARD_BACKGROUND);

		NinePatch patch = new NinePatch(new Texture(Gdx.files.internal(Config.IMAGE_PATH + "background.png")),
				1, 1, 1, 1);
		NinePatchDrawable background = new NinePatchDrawable(patch);
		setBackground(background);

		final Table fileTable = new FileTable(skin);
		add(fileTable).left().top();

		final Table windowTable = new WindowTable(skin);
		add(windowTable).left().top();

		final HelpTable helpTable = new HelpTable(skin);
		add(helpTable).left().top();

		final TextButton generateTable = new TextButton("refresh", skin);
		generateTable.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Editor.post(new CommandGenerateWorld(true));
			}
		});
		add(generateTable).left().top();

		infoTable = new InfoTable(skin);
		add(infoTable).left().top();

		snapshotTable = new SnapshotTable(skin, samplers);
		snapshotTable.setVisible(false);
		add(snapshotTable).left().top();

		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				toFront();
			}
		});
	}

	public InfoTable getInfoTable() {
		return infoTable;
	}

	public SnapshotTable getSnapshotTable() {
		return snapshotTable;
	}
}
