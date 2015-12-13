package com.nukethemoon.tools.opusproto.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.nukethemoon.tools.opusproto.editor.Config;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.sampler.acontinent.AContinent;
import com.nukethemoon.tools.opusproto.sampler.combined.Combined;
import com.nukethemoon.tools.opusproto.sampler.flat.FlatSampler;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSampler;
import com.nukethemoon.tools.opusproto.sampler.noise.NoiseSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;

import java.util.HashMap;

public class Styles {

	public static SpriteDrawable ICON_NOISE;
	public static SpriteDrawable ICON_COMBINED;
	public static SpriteDrawable ICON_FLAT;
	public static SpriteDrawable ICON_ISLANDS;
	public static SpriteDrawable ICON_CONTINENT;

	public static SpriteDrawable ICON_LAYER;
	public static SpriteDrawable ICON_MASK;

	public static Drawable ICON_UP;
	public static Drawable ICON_DOWN;

	public static final Color GRID_COLOR_DARK = 	new Color(0.35f, 0.35f, 0.35f, 0.1f);
	public static final Color GRID_COLOR_LIGHT = 	new Color(1.f, 1.f, 1.f, 0.1f);

	public static Color PROJECT_COLOR = 			new Color(234 / 255f, 205 / 255f, 120 / 255f, 1f);
	public static Color LAYER_COLOR = 				new Color(144 / 255f, 171 / 255f, 128/ 255f, 1f);
	public static Color SAMPLER_COLOR = 			new Color(117f / 255f, 167f / 255f, 199f / 255f, 1);
	public static Color INTERPRETER_COLOR = 		new Color(191f / 255f, 192f / 255f, 54f / 255f, 1);

	public static NinePatchDrawable STANDARD_BACKGROUND;
	public static NinePatchDrawable INNER_BACKGROUND;
	public static NinePatchDrawable ITEM_BACKGROUND;
	public static NinePatchDrawable SELECTED_BACKGROUND;
	public static NinePatchDrawable TRANSPARENT_BACKGROUND;

	public static java.util.Map<Class<? extends AbstractSampler>, SpriteDrawable>
			SAMPLER_ICON = new HashMap<Class<? extends AbstractSampler>, SpriteDrawable>();

	public static Skin UI_SKIN;

	public static Skin SAMPLER_SELECT_BOX_SKIN;
	public static Skin INTERPRETER_SELECT_BOX_SKIN;

	public static TextField.TextFieldStyle TEXT_FIELD_STYLE_FAIL;
	public static TextField.TextFieldStyle TEXT_FIELD_STYLE;

	public static void load() {

		ICON_UP = new SpriteDrawable(			new Sprite(new Texture(Gdx.files.internal(Config.IMAGE_PATH + "upIcon.png"))));
		ICON_DOWN = new SpriteDrawable(			new Sprite(new Texture(Gdx.files.internal(Config.IMAGE_PATH + "downIcon.png"))));

		ICON_LAYER = new SpriteDrawable(		new Sprite(new Texture(Gdx.files.local(Config.IMAGE_PATH + "icon_layer.png"))));
		ICON_MASK = new SpriteDrawable(			new Sprite(new Texture(Gdx.files.local(Config.IMAGE_PATH + "icon_mask.png"))));
		ICON_NOISE = new SpriteDrawable(		new Sprite(new Texture(Gdx.files.local(Config.IMAGE_PATH + "icon_noise.png"))));
		ICON_COMBINED = new SpriteDrawable(		new Sprite(new Texture(Gdx.files.local(Config.IMAGE_PATH + "icon_combined.png"))));
		ICON_FLAT = new SpriteDrawable(			new Sprite(new Texture(Gdx.files.local(Config.IMAGE_PATH + "icon_flat.png"))));
		ICON_ISLANDS = new SpriteDrawable(		new Sprite(new Texture(Gdx.files.local(Config.IMAGE_PATH + "icon_islands.png"))));
		ICON_CONTINENT = new SpriteDrawable(	new Sprite(new Texture(Gdx.files.local(Config.IMAGE_PATH + "icon_continent.png"))));

		SAMPLER_ICON.put(Layer.class, 			ICON_LAYER);
		SAMPLER_ICON.put(MaskedSampler.class, 	ICON_MASK);
		SAMPLER_ICON.put(NoiseSampler.class, 	ICON_NOISE);
		SAMPLER_ICON.put(Combined.class,		ICON_COMBINED);
		SAMPLER_ICON.put(FlatSampler.class,		ICON_FLAT);
		SAMPLER_ICON.put(AContinent.class,		ICON_CONTINENT);

		NinePatch patch = new NinePatch(new Texture(Gdx.files.internal(Config.IMAGE_PATH + "background.png")),
				1, 1, 1, 1);
		NinePatch patch2 = new NinePatch(new Texture(Gdx.files.internal(Config.IMAGE_PATH + "background2.png")),
				1, 1, 1, 1);
		NinePatch patch3 = new NinePatch(new Texture(Gdx.files.internal(Config.IMAGE_PATH + "background3.png")),
				1, 1, 1, 1);
		NinePatch patch4 = new NinePatch(new Texture(Gdx.files.internal(Config.IMAGE_PATH + "selectedBG.png")),
				0, 0, 0, 0);
		NinePatch patch5 = new NinePatch(new Texture(Gdx.files.internal(Config.IMAGE_PATH + "transparentBG.png")),
				0, 0, 0, 0);
		STANDARD_BACKGROUND = new NinePatchDrawable(patch2);
		INNER_BACKGROUND = new NinePatchDrawable(patch);
		ITEM_BACKGROUND = new NinePatchDrawable(patch3);
		SELECTED_BACKGROUND = new NinePatchDrawable(patch4);
		TRANSPARENT_BACKGROUND = new NinePatchDrawable(patch5);



		UI_SKIN = new Skin(Gdx.files.internal(Config.SKIN_PATH));

		SAMPLER_SELECT_BOX_SKIN = new Skin(Gdx.files.internal(Config.SKIN_PATH));
		SelectBox.SelectBoxStyle selectBoxStyleSampler = SAMPLER_SELECT_BOX_SKIN.get(SelectBox.SelectBoxStyle.class);
		selectBoxStyleSampler.fontColor = SAMPLER_COLOR;

		INTERPRETER_SELECT_BOX_SKIN = new Skin(Gdx.files.internal(Config.SKIN_PATH));
		SelectBox.SelectBoxStyle selectBoxStyleInterpreter = INTERPRETER_SELECT_BOX_SKIN.get(SelectBox.SelectBoxStyle.class);
		selectBoxStyleInterpreter.fontColor = INTERPRETER_COLOR;

		TEXT_FIELD_STYLE_FAIL = new TextField.TextFieldStyle(
				UI_SKIN.getFont("default-font"),
				Color.RED,
				UI_SKIN.getDrawable("cursor"),
				UI_SKIN.getDrawable("selection"),
				UI_SKIN.getDrawable("textfield"));

		TEXT_FIELD_STYLE = new TextField.TextFieldStyle(
				UI_SKIN.getFont("default-font"),
				Color.WHITE,
				UI_SKIN.getDrawable("cursor"),
				UI_SKIN.getDrawable("selection"),
				UI_SKIN.getDrawable("textfield"));
	}
}
