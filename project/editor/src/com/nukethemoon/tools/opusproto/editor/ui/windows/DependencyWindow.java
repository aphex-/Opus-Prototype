package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandOpenLayerEditor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.exceptions.SamplerRecursionException;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;

import java.util.ArrayList;
import java.util.List;

public class DependencyWindow extends ClosableWindow {

	private final Label titleLabel;
	private final Label samplerLabel;

	private Table dependencyContainer;


	public DependencyWindow(Skin skin) {
		super("DEPENDENCIES", skin);
		top().left();
		defaults().pad(4);


		titleLabel = new Label("Direct dependencies for ", getSkin());
		add(titleLabel).left();

		samplerLabel = new Label("", getSkin());
		samplerLabel.setColor(Styles.SAMPLER_COLOR);
		add(samplerLabel).left();
		row();

		dependencyContainer = new Table(skin);
		dependencyContainer.top().left().defaults().pad(2);
		add(dependencyContainer).colspan(2);

	}

	public void showDependencies(String samplerId, Samplers loader, List<Layer> layers) {
		dependencyContainer.clear();

		samplerLabel.setText(samplerId);

		List<String> dependencies = new ArrayList<String>();
		try {
			loader.doesASamplerDependOn(loader.getSampler(samplerId), dependencies);

			for (String d : dependencies) {
				final AbstractSampler sampler = loader.getSampler(d);
				dependencyContainer.add(createItem(sampler)).fill().expand();
				dependencyContainer.row();
			}

			dependencies.clear();
			loader.doesASamplerDependOn(loader.getSampler(samplerId), Editor.toAbstract(layers), dependencies, 0);
			for (String d : dependencies) {
				final AbstractSampler layer = getLayer(layers, d);
				if (layer != null) {
					dependencyContainer.add(createItem(layer)).fill().expand();
					dependencyContainer.row();
				}
			}

			if (dependencies.size() == 0) {
				dependencyContainer.add(new Label("No dependencies", getSkin()));
			}

		} catch (SamplerRecursionException e) {
			Editor.showErrorDialog("Error showing dependencies window.");
		}
		pack();
	}

	private Layer getLayer(List<Layer> layers, String id) {
		for (Layer l : layers) {
			if (l.getConfig().id.equals(id)) {
				return l;
			}
		}
		return null;
	}


	private Table createItem(final AbstractSampler sampler) {
		Table dependencyItem = new Table(getSkin());
		dependencyItem.defaults().pad(4);
		dependencyItem.setBackground(Styles.ITEM_BACKGROUND);

		if (Styles.SAMPLER_ICON.get(sampler.getClass()) != null) {
			dependencyItem.add(new Image(Styles.SAMPLER_ICON.get(sampler.getClass()))).left();
		}

		Label samplerIdLabel = new Label(sampler.getConfig().id, getSkin());
		if (sampler instanceof Layer) {
			samplerIdLabel.setColor(Styles.SAMPLER_COLOR);
		} else {
			samplerIdLabel.setColor(Styles.LAYER_COLOR);
		}

		dependencyItem.add(samplerIdLabel).left().fill().expand();

		Label samplerClassLabel = new Label(sampler.getClass().getSimpleName(), getSkin());
		dependencyItem.add(samplerClassLabel).left();

		TextButton openButton = new TextButton("open", getSkin());
		openButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (sampler instanceof Layer) {
					Editor.post(new CommandOpenLayerEditor(sampler.getConfig().id));
				} else {
					Editor.post(new CommandOpenSamplerEditor(sampler.getConfig().id));
				}

				DependencyWindow.this.setVisible(false);
			}
		});
		dependencyItem.add(openButton).right();
		return dependencyItem;
	}
}
