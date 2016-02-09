package com.nukethemoon.tools.opusproto.editor.message;

public class CommandRenameElement {

	public String oldName;
	public String newName;
	public ElementType type;

	public CommandRenameElement(String oldName, String newName, ElementType type) {
		this.oldName = oldName;
		this.newName = newName;
		this.type = type;
	}

	public enum ElementType {
		Sampler,
		Layer,
		World,
		Interpreter
	}

}
