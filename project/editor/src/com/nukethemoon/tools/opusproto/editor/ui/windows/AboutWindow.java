package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.tools.opusproto.editor.Config;
import com.nukethemoon.tools.opusproto.editor.app.Editor;

public class AboutWindow extends ClosableWindow {

	public AboutWindow(Skin skin) {
		super("ABOUT", skin);



		add(new Label(Config.NAME, skin)).padTop(20);
		row();

		String versionText = "ver. " + Config.VERSION;
		Label versionLabel = new Label(versionText, skin);
		add(versionLabel);
		row();

		if (Config.DEBUG) {
			add(new Label("debug mode", skin));
			row();
		}

		Label createdByLabel = new Label("created by", skin);
		add(createdByLabel).padTop(25);
		row();

		add(new Image(new Texture("data/img/ntm_logo.png")));
		row();

		Button ntmLabel = new TextButton("nuke-the-moon.com", skin);
		ntmLabel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				Editor.openNTMPage();
			}
		});
		add(ntmLabel).padTop(5);
		row();

		pack();
	}
}
