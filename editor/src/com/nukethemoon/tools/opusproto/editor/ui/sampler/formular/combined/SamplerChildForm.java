package com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.combined;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.UpDownElement;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.CursorTextField;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.SamplerSelectBoxElement;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;

public class SamplerChildForm extends AbstractChangeForm {


	private final SamplerSelectBoxElement samplerSelectBoxElement;
	private final TextButton removeButton;
	private final TextButton editButton;
	private final CheckBox activeCheckBox;
	private final SelectBox<ChildSamplerConfig.Operator> operationSelectBox;
	private final UpDownElement upDownElement;

	private final CheckBox invertCB;

	private final CursorTextField multiplyTF;

	private TextField scaleModTextField;
	private TextField seedModTextField;

	private AbstractSampler sampler;
	private SamplerModListener samplerModListener;

	public SamplerChildForm(Skin skin, final AbstractSampler sampler, Stage stage, SamplerLoader pool,
							ChildSamplerConfig childSamplerConfig) {
		this(skin, sampler, stage, pool, childSamplerConfig, null);
	}

	public SamplerChildForm(Skin skin, final AbstractSampler sampler, Stage stage, SamplerLoader pool,
							ChildSamplerConfig childSamplerConfig,
							Class<? extends AbstractSampler> selectionFilter) {
		super(skin);
		this.sampler = sampler;
		defaults().pad(2);
		setBackground(Styles.ITEM_BACKGROUND);

		Table operationTable = new Table(skin);
		add(operationTable).top().left();

		if (childSamplerConfig != null) {
			operationSelectBox = new SelectBox<ChildSamplerConfig.Operator>(skin);
			operationSelectBox.setItems(ChildSamplerConfig.Operator.values());
			operationSelectBox.setSelected(childSamplerConfig.operator);
			operationTable.add(operationSelectBox).top().left();
		} else {
			operationSelectBox = null;
		}

		Table mainContentTable = new Table(skin);
		mainContentTable.defaults().pad(3);
		mainContentTable.setBackground(Styles.ITEM_BACKGROUND);
		add(mainContentTable);


		Table firstRow = new Table(skin);
		firstRow.left().top();
		firstRow.defaults().left().top().pad(1);
		mainContentTable.add(firstRow).left().top();

		upDownElement = new UpDownElement(skin);
		operationTable.add(upDownElement);

		samplerSelectBoxElement = new SamplerSelectBoxElement(skin, stage, pool, true, selectionFilter);
		samplerSelectBoxElement.addChangedListener(new AbstractChangeForm.ChangedListener() {
			@Override
			public void onChange() {
				SamplerChildForm.this.sampler = samplerSelectBoxElement.getSelected();
				samplerSelectBoxElement.setSelected(samplerSelectBoxElement.getSelected());
				notifyChanges();
			}
		});
		firstRow.add(samplerSelectBoxElement).left().top();
		samplerSelectBoxElement.setSelected(sampler);

		editButton = new TextButton("edit", skin);
		editButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Editor.post(new CommandOpenSamplerEditor(samplerSelectBoxElement.getSelected().getConfig().id));
			}
		});
		firstRow.add(editButton).height(35).left().top();

		removeButton = new TextButton("X", skin);
		firstRow.add(removeButton).height(35).left().top();

		activeCheckBox = new CheckBox("active", skin);
		activeCheckBox.setChecked(true);

		firstRow.add(activeCheckBox).left().center();


		mainContentTable.row();

		Table secondRow = new Table(skin);
		secondRow.left().top();
		secondRow.defaults().left().top().pad(1);
		mainContentTable.add(secondRow).left().top().expand().fill();

		// scale modifier
		if (childSamplerConfig != null) {
			secondRow.add(new Label("scale", skin)).left();
			scaleModTextField = new CursorTextField(childSamplerConfig.scaleModifier + "", skin, this, 0.1f);
			scaleModTextField.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (samplerModListener != null) {
						samplerModListener.onScaleModChange(scaleModTextField);
					}
				}
			});
			secondRow.add(scaleModTextField).width(35).left();
		}

		// seed modifier
		if (childSamplerConfig != null) {
			secondRow.add(new Label("seed", skin)).left();
			seedModTextField = new CursorTextField(childSamplerConfig.seedModifier + "", skin, this, 1f);
			seedModTextField.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (samplerModListener != null) {
						samplerModListener.onSeedModChange(seedModTextField);
					}
				}
			});
			secondRow.add(seedModTextField).width(35).left();

			secondRow.add(new Label("mult.", skin));

			multiplyTF = new CursorTextField(childSamplerConfig.multiply + "", skin, this, 0.1f);
			multiplyTF.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (samplerModListener != null) {
						samplerModListener.onMultiplyChange(multiplyTF);
					}
				}
			});
			secondRow.add(multiplyTF).width(35).left();

			invertCB = new CheckBox("invert", skin);
			invertCB.setChecked(childSamplerConfig.invert);
			invertCB.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (samplerModListener != null) {
						samplerModListener.onInvertChange(invertCB.isChecked());
					}
				}
			});
			secondRow.add(invertCB);
		} else {
			invertCB = null;
			multiplyTF = null;
		}

		pack();
	}

	public void setSelected(AbstractSampler sampler) {
		samplerSelectBoxElement.setSelected(sampler);
	}

	public void addSamplerSelectionListener(ChangeListener changeListener) {
		samplerSelectBoxElement.addListener(changeListener);
	}

	public void addRemoveListener(ChangeListener removeListener) {
		removeButton.addListener(removeListener);
	}

	public void addActiveListener(ChangeListener activeListener) {
		activeCheckBox.addListener(activeListener);
	}

	public void addOperationListener(ChangeListener operationListener) {
		if (operationSelectBox != null) {
			operationSelectBox.addListener(operationListener);
		}
	}

	public AbstractSampler getSelected() {
		return samplerSelectBoxElement.getSelected();
	}

	public void updatePreview() {
		samplerSelectBoxElement.updatePreview(this.sampler);
	}

	public void reloadSamplers() {
		samplerSelectBoxElement.reloadSamplers();
	}

	public boolean isActivated() {
		return activeCheckBox.isChecked();
	}

	public void setChecked(boolean checked) {
		activeCheckBox.setChecked(checked);
	}

	public ChildSamplerConfig.Operator getOperator() {
		if (operationSelectBox == null) {
			return null;
		}
		return operationSelectBox.getSelected();
	}


	public UpDownElement getUpDownElement() {
		return upDownElement;
	}

	public AbstractSampler getSampler() {
		return sampler;
	}

	public void setSamplerModListener(SamplerModListener samplerModListener) {
		this.samplerModListener = samplerModListener;
	}

	public interface SamplerModListener {
		void onScaleModChange(TextField textField);
		void onSeedModChange(TextField textField);
		void onInvertChange(boolean invert);
		void onMultiplyChange(TextField textField);

	}

}
