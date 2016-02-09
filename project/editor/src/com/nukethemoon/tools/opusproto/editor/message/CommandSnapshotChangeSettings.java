package com.nukethemoon.tools.opusproto.editor.message;

public class CommandSnapshotChangeSettings {

	public boolean drawBehind;
	public float opacity;

	public CommandSnapshotChangeSettings(boolean drawBehind, float opacity) {
		this.drawBehind = drawBehind;
		this.opacity = opacity;
	}
}
