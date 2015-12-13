package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandDeleteSampler;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.BaseDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.YesNoDialog;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.previews.SamplerPreviewImage;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;


public class SamplerOverViewItem extends Table {



	public SamplerOverViewItem(final Skin skin, final AbstractSampler sampler) {
		super(skin);
		top().left();
		defaults().pad(2);
		pad(2);
		padRight(8); // hack for scrollbar
		padLeft(8);
		setBackground(Styles.ITEM_BACKGROUND);

		SamplerPreviewImage previewImage = new SamplerPreviewImage();
		previewImage.applySampler(sampler, 50, true);
		add(previewImage).width(50).fill();

		// content
		Table content = new Table(skin);

		if (Styles.SAMPLER_ICON.get(sampler.getClass()) != null) {
			Image samplerIcon = new Image();
			samplerIcon.setDrawable(Styles.SAMPLER_ICON.get(sampler.getClass()));
			content.add(samplerIcon).width(20).height(20).left().top();
		}

		Label samplerName = new Label(sampler.getConfig().id, skin);
		samplerName.setColor(Styles.SAMPLER_COLOR);
		content.add(samplerName).left().top();

		content.row();

		Label samplerType = new Label(sampler.getClass().getSimpleName(), skin);
		content.add(samplerType).left().width(150).fill().colspan(2);
		add(content).left();

		// buttons
		Table buttons = new Table(skin);
		TextButton editButton = new TextButton("edit", skin);
		editButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandOpenSamplerEditor(sampler.getConfig().id));
			}
		});
		buttons.add(editButton).left().fill();
		buttons.row();

		TextButton deleteButton = new TextButton("delete", skin);
		deleteButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				YesNoDialog yesNoDialog = new YesNoDialog("Dou you really want to delete?", skin);
				yesNoDialog.setResultListener(new BaseDialog.ResultListener() {
					@Override
					public void onResult(Object result) {
						Editor.post(new CommandDeleteSampler(sampler.getConfig().id));
					}
				});
				yesNoDialog.show(Editor.STAGE);
			}
		});
		buttons.add(deleteButton).left().fill();
		add(buttons);

		pack();
	}
}
