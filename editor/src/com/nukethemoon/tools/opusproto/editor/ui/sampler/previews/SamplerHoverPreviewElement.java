package com.nukethemoon.tools.opusproto.editor.ui.sampler.previews;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;

public class SamplerHoverPreviewElement extends Table {


	private Image hoverImage;
	private int size;
	private AbstractSampler sampler;
	private final SamplerPreviewImage previewImage;


	public SamplerHoverPreviewElement(Skin skin, int size, final Stage stage) {
		super(skin);
		this.size = size;
		previewImage = new SamplerPreviewImage();

		if (stage != null) {
			previewImage.addListener(new ClickListener() {
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					hoverImage = new Image(SamplerPreviewImage.create(sampler, 400, false));
					stage.addActor(hoverImage);
					hoverImage.setPosition(event.getStageX() - hoverImage.getWidth() - 10,
							event.getStageY() - hoverImage.getHeight() - 10);
				}

				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					if (hoverImage != null) {
						hoverImage.remove();
					}
				}

				@Override
				public boolean mouseMoved(InputEvent event, float x, float y) {
					if (hoverImage != null) {
						hoverImage.setPosition(event.getStageX() - hoverImage.getWidth() - 10,
								event.getStageY() - hoverImage.getHeight() - 10);
					}
					return false;
				}
			});
		}
		add(previewImage);
	}



	public void applySampler(AbstractSampler sampler) {
		this.sampler = sampler;
		previewImage.applySampler(sampler, size, true);
	}
}
