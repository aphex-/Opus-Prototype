package com.nukethemoon.tools.opusproto.editor.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public class EditorApplication extends LwjglApplication implements ExitDialogInterface {

	private Stage stage;
	private Dialog exitDialog;
	private boolean showingConfirmDialog = false;

	public EditorApplication(ApplicationListener listener, LwjglApplicationConfiguration config) {
		super(listener, config);
	}

	@Override
	public void exit() {
		if (exitDialog == null || stage == null) {
			super.exit();
		} else if (!showingConfirmDialog) {
			showingConfirmDialog = true;
			postRunnable(new Runnable()	{
				@Override
				public void run() {
					exitDialog.show(stage);
				}
			});
		}
	}

	@Override
	public void setExitDialog(Dialog exitDialog, Stage stage) {
		this.exitDialog = exitDialog;
		this.stage = stage;
	}

	@Override
	public void exitConfirmed() {
		super.exit();
	}

	@Override
	public void exitCanceled() {
		showingConfirmDialog = false;
	}

}
