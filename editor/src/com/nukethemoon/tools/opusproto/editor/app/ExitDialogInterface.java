package com.nukethemoon.tools.opusproto.editor.app;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public interface ExitDialogInterface {
	void setExitDialog(Dialog exitDialog, Stage stage);

	void exitConfirmed();

	void exitCanceled();
}