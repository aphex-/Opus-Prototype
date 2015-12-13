package com.nukethemoon.tools.opusproto.editor.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public abstract class BaseDialog extends Dialog {

	public String resultValue;
	protected ResultListener listener;

	public BaseDialog(String title, Skin skin) {
		super(title, skin);

	}

	public void setResultListener(ResultListener listener) {
		this.listener = listener;
	}

	@Override
	protected void result(Object object) {
		super.result(object);
		if (listener != null) {
			Boolean success = (Boolean)object;
			if (success) {
				listener.onResult(resultValue);
			}
		}
	}

	public interface ResultListener {
		void onResult(Object result);
	}

}
