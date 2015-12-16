package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.tools.opusproto.Samplers;
import com.nukethemoon.tools.opusproto.editor.Util;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandLimitWindowSizes;
import com.nukethemoon.tools.opusproto.editor.message.sampler.EventSamplerPoolChanged;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.NewSamplerDialog;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class SamplerOverview extends ClosableWindow {

	private final Label labelSamplerCount;
	private final Table samplerContainer;

	private HashMap<String, SamplerOverViewItem> samplerMap = new HashMap<String, SamplerOverViewItem>();
	private Samplers pool;

	private Comparator<AbstractSampler> currentComparator;
	private final Comparator<AbstractSampler> nameComparator;
	private final Comparator<AbstractSampler> classComparator;

	public SamplerOverview(final Skin skin, final Stage stage, Samplers pool) {
		super("SAMPLER LIST", skin);
		this.pool = pool;
		defaults().pad(2);
		setResizable(true);
		setResizeBorder(8);


		nameComparator = new Comparator<AbstractSampler>() {
			@Override
			public int compare(AbstractSampler o1, AbstractSampler o2) {
				return o1.getConfig().id.compareTo(o2.getConfig().id);
			}
		};

		classComparator = new Comparator<AbstractSampler>() {
			@Override
			public int compare(AbstractSampler o1, AbstractSampler o2) {
				return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
			}
		};

		currentComparator = nameComparator;

		labelSamplerCount = new Label("", skin);
		add(labelSamplerCount).left();

		TextButton createNewButton = new TextButton("create", skin);
		createNewButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NewSamplerDialog newSamplerDialog = new NewSamplerDialog(skin);
				newSamplerDialog.show(stage);
			}
		});
		add(createNewButton).right();
		row();

		TextButton buttonOrderByName = new TextButton("order name", getSkin());
		buttonOrderByName.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				currentComparator = nameComparator;
				generateList();
			}
		});
		add(buttonOrderByName).fill().expand();

		TextButton buttonOrderByClass = new TextButton("order class", getSkin());
		buttonOrderByClass.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				currentComparator = classComparator;
				generateList();
			}
		});
		add(buttonOrderByClass).fill().expand();
		row();

		samplerContainer = new Table();


		ScrollPane pane = new ScrollPane(samplerContainer, skin);
		Util.applyStandardSettings(pane);
		add(pane).minHeight(200).colspan(2).fill().expand();

		generateList();
		updateTitle();
		pack();
		Editor.post(new CommandLimitWindowSizes());
	}



	private void generateList() {
		samplerContainer.clear();
		samplerMap.clear();
		AbstractSampler[] samplerList = pool.createSamplerList();

		Arrays.sort(samplerList, currentComparator);


		for (int i = 0; i< samplerList.length; i++) {
			final AbstractSampler abstractSampler = samplerList[i];
			final SamplerOverViewItem samplerOverViewItem = new SamplerOverViewItem(Styles.UI_SKIN, abstractSampler);
			samplerOverViewItem.addListener(new ClickListener() {
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					samplerOverViewItem.setBackground(Styles.SELECTED_BACKGROUND);
				}
				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					samplerOverViewItem.setBackground(Styles.ITEM_BACKGROUND);
				}
			});
			samplerContainer.add(samplerOverViewItem).fill().expand();
			samplerContainer.row();
			samplerMap.put(abstractSampler.getConfig().id, samplerOverViewItem);
		}
		pack();
		Editor.post(new CommandLimitWindowSizes());
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void reload(EventSamplerPoolChanged command) {
		generateList();
	}


	public void removeFromList(String samplerId) {
		SamplerOverViewItem samplerOverViewItem = samplerMap.get(samplerId);
		samplerOverViewItem.remove();
		samplerMap.remove(samplerId);
		updateTitle();
	}

	private void updateTitle() {
		labelSamplerCount.setText("Sampler (" + samplerMap.size() + ")");
	}


}
