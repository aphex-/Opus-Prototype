package com.nukethemoon.tools.opusproto.editor.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.editor.Util;
import com.nukethemoon.tools.opusproto.editor.app.Editor;
import com.nukethemoon.tools.opusproto.editor.message.CommandLimitWindowSizes;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandOpenLayerEditor;
import com.nukethemoon.tools.opusproto.editor.message.layer.EventLayersChanged;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.EventSamplerPoolChanged;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.TreeExpansionListener;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.NewLayerDialog;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.layer.LayerConfig;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerContainerConfig;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class ProjectWindow extends ClosableWindow implements TreeExpansionListener {

	private static Map<String, Boolean> expansionMemory = new HashMap<String, Boolean>();

	private final Tree tree;
	private Skin skin;
	private Opus generator;
	private Samplers loader;
	private final Label worldName;

	private Tree.Node selectedNode = null;

	public ProjectWindow(Skin skin, final Opus generator, final Samplers loader) {
		super("Project", skin);
		this.skin = skin;
		this.generator = generator;
		this.loader = loader;
		defaults().pad(2);
		setResizable(true);
		setResizeBorder(8);

		worldName = new Label(generator.getConfig().name, skin);
		worldName.setColor(Styles.PROJECT_COLOR);
		add(worldName).left().fill();

		TextButton addLayerButton = new TextButton("create layer", skin);
		addLayerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NewLayerDialog newLayerDialog = new NewLayerDialog(Styles.UI_SKIN);
				newLayerDialog.show(Editor.STAGE);
			}
		});
		add(addLayerButton).right();
		row();

		Table treeTable = new Table(skin);
		treeTable.setBackground(Styles.STANDARD_BACKGROUND);
		treeTable.left().top().pad(8);
		tree = new Tree(skin);

		tree.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (selectedNode != tree.getSelection().first()) {
					if (tree.getSelection().first() != null) {
						SamplerNode node = (SamplerNode) tree.getSelection().first();
						if (node.aClass == Layer.class) {
							Editor.post(new CommandOpenLayerEditor(node.id));
						} else {
							Editor.post(new CommandOpenSamplerEditor(node.id));
						}
					}
				}
			}
		});

		tree.getSelection();

		loadTree(generator, loader);
		tree.expandAll();
		Editor.post(new CommandLimitWindowSizes());
		treeTable.add(tree).left().top();
		ScrollPane scrollPane = new ScrollPane(treeTable);
		Util.applyStandardSettings(scrollPane);
		add(scrollPane).fill().expand().colspan(2);
		pack();
	}

	public void refresh() {
		loadTree(generator, loader);
	}

	public void memory(Tree.Node node) {
		if (node instanceof SamplerNode) {
			SamplerNode sn = (SamplerNode) node;
			if (expansionMemory.containsKey(sn.nodeId)) {
				expansionMemory.remove(sn.nodeId);
			}
			expansionMemory.put(sn.nodeId, sn.isExpanded());
		}

		for (Tree.Node n : node.getChildren()) {
			memory(n);
		}
	}

	private void loadTree(Opus opus, Samplers loader) {
		if (tree.getNodes() != null) {
			for (Tree.Node n : tree.getNodes()) {
				memory(n);
			}
		}
		tree.clearChildren();
		for (Layer l : opus.getLayers()) {
			addLayer(l);
		}
		tree.pack();
		expansionMemory.clear();
		pack();
		Editor.post(new CommandLimitWindowSizes());
	}

	private void addLayer(Layer l) {
		Label label = new Label(l.getConfig().id, skin);
		SamplerNode node = new SamplerNode(l.getClass(), label, l.getConfig().id, this);
		tree.add(node);

		if (expansionMemory.containsKey(node.nodeId)) {
			node.setExpanded(expansionMemory.get(node.nodeId));
		}

		LayerConfig config = (LayerConfig) l.getConfig();
		if (config.samplerItems != null) {
			for (ChildSamplerConfig c : config.samplerItems) {
				fillNode(node, c.samplerReferenceId);
			}
		}
	}

	private void fillNode(Tree.Node node, String samplerId) {
		AbstractSampler sampler = loader.getSampler(samplerId);
		SamplerNode subNode = createNode(samplerId);
		if (subNode != null) {
			node.add(subNode);

			if (expansionMemory.containsKey(subNode.nodeId)) {
				subNode.setExpanded(expansionMemory.get(subNode.nodeId));
			}

			if (sampler.getConfig() instanceof AbstractSamplerContainerConfig) {
				AbstractSamplerContainerConfig c = (AbstractSamplerContainerConfig) sampler.getConfig();
				for (ChildSamplerConfig child : c.getChildSamplerConfigs()) {
					fillNode(subNode, child.samplerReferenceId);
				}
			}
		}

	}

	private SamplerNode createNode(String samplerId) {
		Label childLabel = new Label(samplerId, skin);
		if (loader.getSampler(samplerId) != null) {
			SamplerNode node = new SamplerNode(loader.getSampler(samplerId).getClass(), childLabel, samplerId, this);
			return node;
		}
		return null;
	}

	@Override
	public void onExpansion(boolean expanded) {
		pack();
		Editor.post(new CommandLimitWindowSizes());
	}



	private static class SamplerNode extends Tree.Node {
		public String id;
		public Class aClass;
		public String nodeId;
		private TreeExpansionListener listener;

		public SamplerNode(Class aClass, Actor actor, final String id, TreeExpansionListener listener) {
			super(actor);
			this.id = id;
			this.aClass = aClass;
			this.listener = listener;
			nodeId = aClass.getCanonicalName() + id;
			SpriteDrawable spriteDrawable = Styles.SAMPLER_ICON.get(aClass);
			if (spriteDrawable != null) {
				setIcon(spriteDrawable);
			}
		}

		@Override
		public void setExpanded(boolean expanded) {
			super.setExpanded(expanded);
			if (expansionMemory.get(nodeId) != null) {
				expansionMemory.remove(nodeId);
			}
			expansionMemory.put(nodeId, isExpanded());
			listener.onExpansion(isExpanded());
		}

	}



	public void setWorldName(String name) {
		worldName.setText(name);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void onSamplerPoolChange(EventSamplerPoolChanged command) {
		loadTree(generator, loader);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void onReloadLayer(EventLayersChanged command) {
		loadTree(generator, loader);
	}
}
