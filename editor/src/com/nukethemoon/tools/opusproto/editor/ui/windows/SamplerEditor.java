package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.editor.Util;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandGenerateWorld;
import com.nukethemoon.tools.opusproto.editor.message.CommandRenameElement;
import com.nukethemoon.tools.opusproto.editor.message.CommandShowDependencyWindow;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotBySampler;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.EventSamplerPoolChanged;
import com.nukethemoon.tools.opusproto.editor.message.sampler.EventSamplerUpdated;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.BaseDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.RenameDialog;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.AbstractSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.CursorTextField;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.ModifierTable;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.Notifyable;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.RangeTable;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.previews.SamplerPreviewImage;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.noise.NoiseAlgorithmPool;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.tools.Log;
import com.squareup.otto.Subscribe;

import java.lang.reflect.InvocationTargetException;


public class SamplerEditor extends ClosableWindow implements AbstractChangeForm.ChangedListener {

	private static int TABLE_HEIGHT = 300;

	private final TextField scaleTextField;
	private final TextField seedModTextField;
	private final Image samplerIcon;
	private Label samplerName;
	protected SamplerPreviewImage previewImage;
	protected ModifierTable modifierTable;
	protected RangeTable rangeTable;

	private Table preview;
	private Table settings;
	private Table baseSettings;
	private Table concreteSettings;
	private Table modifier;

	private SamplerLoader samplerLoader;
	private Skin skin;
	private AbstractSampler sampler;

	private AbstractSamplerForm editor;
	private static final int PREVIEW_SIZE = 200;

	public SamplerEditor(final Skin skin, final Stage stage,
						NoiseAlgorithmPool noisePool, SamplerLoader samplerLoader) {
		super("SAMPLER EDITOR", skin);
		this.skin = skin;
		this.samplerLoader = samplerLoader;
		this.sampler = samplerLoader.getSampler(Editor.DEFAULT_SAMPLER_NAME);

		defaults().pad(4).top();

		// === Preview container ===
		preview = new Table(skin);
		preview.setBackground(Styles.INNER_BACKGROUND);
		preview.top().left();
		preview.pad(2);
		preview.defaults().pad(3);
		add(preview).height(TABLE_HEIGHT);

		Table titleTable = new Table(skin);
		samplerIcon = new Image();
		titleTable.add(samplerIcon).left();
		samplerName = new Label("", skin);
		samplerName.setColor(Styles.SAMPLER_COLOR);
		TextButton helpButton = new TextButton("?", Styles.UI_SKIN);
		helpButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.openHelp("data/docu/sampler.html");
			}
		});
		titleTable.add(helpButton);
		titleTable.add(samplerName).fill().expand().left();
		preview.add(titleTable).colspan(3).left().top().fill().expand();
		preview.row();

		TextButton renameButton = new TextButton("rename", skin);
		renameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenameDialog renameDialog = new RenameDialog(skin, sampler.getConfig().id);
				renameDialog.setResultListener(new BaseDialog.ResultListener() {
					@Override
					public void onResult(Object result) {
						Editor.post(new CommandRenameElement(sampler.getConfig().id,
								(String) result, CommandRenameElement.ElementType.Sampler));
					}
				});
				renameDialog.show(Editor.STAGE);
			}
		});
		preview.add(renameButton);

		TextButton snapshotButton = new TextButton("snapshot", skin);
		snapshotButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandSnapshotBySampler(sampler.getConfig().id, 0.75f, false));
			}
		});
		preview.add(snapshotButton).left();

		TextButton dependenciesButton = new TextButton("depend.", skin);
		dependenciesButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandShowDependencyWindow(sampler.getConfig().id));
			}
		});
		preview.add(dependenciesButton).left();


		preview.row();

		previewImage = new SamplerPreviewImage();
		preview.add(previewImage).colspan(3);
		preview.row();

		rangeTable = new RangeTable(getSkin());
		preview.add(rangeTable).colspan(3).left();
		updateIntervalLabel(sampler);

		// === Settings container ===
		settings = new Table(skin);
		settings.setBackground(Styles.INNER_BACKGROUND);
		settings.top().left();
		settings.pad(2);
		settings.defaults().pad(2);
		add(settings).height(TABLE_HEIGHT);

		// base settings
		baseSettings = new Table(skin);
		baseSettings.top().left();

		baseSettings.add(new Label("scale: ", skin));
		scaleTextField = new CursorTextField("", skin, new Notifyable() {
			@Override
			public void notifyChanges() {
				onChange();
			}
		}, 1f);
		scaleTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onChange();
			}
		});
		baseSettings.add(scaleTextField).width(40);

		baseSettings.add(new Label("seed-mod: ", skin));
		seedModTextField = new CursorTextField("", skin, new Notifyable() {
			@Override
			public void notifyChanges() {
				onChange();
			}
		}, 1f);
		seedModTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onChange();
			}
		});
		baseSettings.add(seedModTextField).left();

		settings.add(baseSettings).fill();
		settings.row();


		// concrete settings
		concreteSettings = new Table(skin);
		concreteSettings.left().top();
		settings.add(concreteSettings).left().top().expand().fill();

		// === Modifier container ===
		modifier = new Table(skin);
		modifier.top().left();
		modifier.setBackground(Styles.INNER_BACKGROUND);
		modifier.pad(2);
		modifier.defaults().pad(2);
		add(modifier).height(TABLE_HEIGHT).fill();

		modifierTable = new ModifierTable(skin, sampler);
		modifier.add(modifierTable).left().top();
		modifierTable.addChangedListener(this);

		onSelectionChange(samplerLoader.getSampler(Editor.DEFAULT_SAMPLER_NAME));
		pack();
	}

	private void onSelectionChange(AbstractSampler pSampler) {
		sampler = pSampler;
		applyToSettings(pSampler);
		applyToPreview(pSampler);
		applyToModifier(pSampler);

		SpriteDrawable spriteDrawable = Styles.SAMPLER_ICON.get(pSampler.getClass());
		if (spriteDrawable != null) {
			samplerIcon.setDrawable(spriteDrawable);
		}
		scaleTextField.setText(pSampler.getConfig().scale + "");
		seedModTextField.setText(pSampler.getConfig().worldSeedModifier + "");
		updateIntervalLabel(sampler);
		pack();
	}

	private void applyToPreview(AbstractSampler pSampler) {
		samplerName.setText(pSampler.getConfig().id);
		previewImage.applySampler(pSampler, PREVIEW_SIZE, false);
	}

	private void applyToSettings(AbstractSampler sampler) {
		concreteSettings.clear();
		Class<? extends AbstractSamplerForm> aClass = Editor.KNOWN_EDITORS.get(sampler.getClass());

		editor = null;
		try {
			editor = (AbstractSamplerForm)
					aClass.getConstructors()[0].newInstance(this.skin, sampler, samplerLoader);
		} catch (InvocationTargetException e) {
			Log.e(SamplerEditor.class, e.getClass().getSimpleName() + " Cause: " + e.getCause().getMessage());
			e.getCause().printStackTrace();
		} catch (InstantiationException e) {
			Log.e(SamplerEditor.class, e.getClass().getSimpleName() + " " + e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e(SamplerEditor.class, e.getClass().getSimpleName() + " " + e.getMessage());
		}

		if (editor == null) {
			Gdx.app.log(SamplerEditor.class.getSimpleName(), "No known editor for " + sampler.getClass().getSimpleName());
			pack();
			return;
		}
		ScrollPane scrollPane = new ScrollPane(editor, skin);
		Util.applyStandardSettings(scrollPane);
		concreteSettings.add(scrollPane).top().left().expand().fill();
		editor.loadFromConfig(sampler.getConfig());
		editor.top().left();
		editor.pack();
		editor.addChangedListener(this);
		scrollPane.pack();
	}

	public void applyToModifier(AbstractSampler pSampler) {
		modifierTable.clearModifier();
		modifierTable.update(pSampler);
	}

	public void updateIntervalLabel(AbstractSampler pSampler) {
		rangeTable.setRange(pSampler.modify(pSampler.getMinSample()), pSampler.modify(pSampler.getMaxSample()));
	}

	@Override
	public void onChange() {
		pack();
		//Editor.post(new CommandRefreshLayout());

		AbstractSamplerConfiguration config = sampler.getConfig();
		if (AbstractChangeForm.containsValidFloat(scaleTextField)) {
			float scale = Float.parseFloat(scaleTextField.getText());

			if (AbstractChangeForm.containsValidDouble(seedModTextField)) {
				double seedMod = Double.parseDouble(seedModTextField.getText());

				if (modifierTable.containsValidModifiers()) {

					if (editor.applyToConfig(config)) {
						config.scale = scale;
						config.worldSeedModifier = seedMod;

						modifierTable.applyModifiers(config);
						editor.applyToConfig(config);

						try {
							sampler.init();
						} catch (SamplerInvalidConfigException e) {
							Editor.showErrorDialog(e.getMessage());
							e.printStackTrace();
						}

						applyToPreview(sampler);
						updateIntervalLabel(sampler);
						Editor.post(new EventSamplerUpdated(sampler.getConfig().id));
						Editor.post(new CommandGenerateWorld());
					}
				}
			}
		}
	}

	public String getSelectedSamplerId() {
		if (sampler != null) {
			return sampler.getConfig().id;
		}
		return null;
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void selectSampler(CommandOpenSamplerEditor command) {
		if (!this.isVisible()) {
			this.setVisible(true);
		}
		AbstractSampler sampler = samplerLoader.getSampler(command.samplerId);
		if (sampler == null) {
			sampler = samplerLoader.getSampler(Editor.DEFAULT_SAMPLER_NAME);
		}
		onSelectionChange(sampler);
		toFront();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void reloadSamplers(EventSamplerPoolChanged command) {
		// if the sampler was deleted
		if (samplerLoader.getSampler(sampler.getConfig().id) == null) {
			this.setVisible(false);
		} else {
			onChange();
		}
	}

}
