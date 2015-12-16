package com.nukethemoon.tools.opusproto.editor.ui.sampler;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.Util;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandRefreshLayout;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.previews.SamplerHoverPreviewElement;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;

public class SamplerSelectBoxElement extends AbstractChangeForm {

	private final SelectBox<AbstractSampler> samplerSelect;
	private final Image samplerIcon;
	private SamplerHoverPreviewElement preview;
	private Samplers samplers;

	public SamplerSelectBoxElement(Skin skin, Stage stage, Samplers samplers,
								   boolean showPreview) {
		this(skin, stage, samplers, showPreview, null);
	}

	public SamplerSelectBoxElement(Skin skin, Stage stage, Samplers samplers,
			boolean showPreview, Class<? extends AbstractSampler> filterClass) {
		super(skin);
		this.samplers = samplers;

		AbstractSampler[] samplerList = Util.filter(samplers.createSamplerList(), filterClass);

		samplerIcon = new Image();
		add(samplerIcon).width(20).height(20);

		samplerSelect = new SelectBox<AbstractSampler>(Styles.SAMPLER_SELECT_BOX_SKIN);
		samplerSelect.setItems(samplerList);
		samplerSelect.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				select(samplerSelect.getSelected());
			}
		});

		preview = new SamplerHoverPreviewElement(skin, 35, stage);
		if (samplerList.length > 0) {
			samplerSelect.setSelected(samplers.getSampler(Editor.DEFAULT_SAMPLER_NAME));
			preview.applySampler(samplers.getSampler(Editor.DEFAULT_SAMPLER_NAME));
		}

		add(samplerSelect);
		if (showPreview) {
			add(preview).width(35).height(35);
		}
	}

	private void select(AbstractSampler sampler) {
		updateIcon(sampler);
		preview.applySampler(sampler);
		notifyChanges();
		Editor.post(new CommandRefreshLayout());
	}

	private void updateIcon(AbstractSampler sampler) {
		if (Styles.SAMPLER_ICON.get(sampler.getClass()) != null) {
			samplerIcon.setDrawable(Styles.SAMPLER_ICON.get(sampler.getClass()));
		}
	}

	public void updatePreview(AbstractSampler sampler) {
		preview.applySampler(sampler);
		updateIcon(sampler);
	}

	public AbstractSampler getSelected() {
		return samplerSelect.getSelected();
	}

	public void setSelected(AbstractSampler abstractSampler) {
		samplerSelect.setSelected(abstractSampler);
	}

	public void reloadSamplers() {
		AbstractSampler[] samplerList = samplers.createSamplerList();
		samplerSelect.setItems(samplerList);
	}
}
