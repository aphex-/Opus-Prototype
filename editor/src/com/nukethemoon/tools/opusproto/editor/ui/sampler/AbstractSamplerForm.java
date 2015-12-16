package com.nukethemoon.tools.opusproto.editor.ui.sampler;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.ui.AbstractChangeForm;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.ErrorDialog;
import com.nukethemoon.tools.opusproto.exceptions.SamplerRecursionException;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.Samplers;
import com.nukethemoon.tools.opusproto.tools.Log;

public abstract class AbstractSamplerForm extends AbstractChangeForm {


	protected final Table contentTable;
	protected Samplers samplers;
	protected AbstractSampler sampler;


	public AbstractSamplerForm(final Skin skin, final AbstractSampler sampler, Samplers pool) {
		super(skin);
		this.samplers = pool;
		this.sampler = sampler;
		left().top();
		contentTable = new Table(skin);
		contentTable.top().left();
		add(contentTable).top().left();
		row();
		pack();
	}

	public boolean canAddSamplerToSampler(AbstractSampler parent, AbstractSampler child) {

		if (child.getConfig().id.equals(parent.getConfig().id)) {
			ErrorDialog dialog = new ErrorDialog("You can not select this sampler. Parent and child sampler must not be the same.", Styles.UI_SKIN);
			dialog.show(Editor.STAGE);
			return false;
		}
		try {
			if (samplers.doesSecondDependOnFirst(parent, child)) {
				ErrorDialog dialog = new ErrorDialog("You can not select this sampler. It would create a circular dependency.", Styles.UI_SKIN);
				dialog.show(Editor.STAGE);
				return false;
			}
		} catch (SamplerRecursionException e) {
			Log.e(AbstractSamplerForm.class, e.getMessage());
			return false;
		}

		return true;
	}

	public abstract boolean applyToConfig(AbstractSamplerConfiguration config);

	public abstract void loadFromConfig(AbstractSamplerConfiguration config);
}
