package com.nukethemoon.tools.opusproto.editor.message;

public class CommandGenerateWorld {

	public boolean force;

	public CommandGenerateWorld() {
		this(false);
	}

	public CommandGenerateWorld(boolean force) {
		this.force = force;
	}
}
