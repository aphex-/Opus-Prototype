package com.nukethemoon.tools.opusproto.editor.ui.sampler.previews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;

public class SamplerPreviewImage extends Image {

	public void applySampler(AbstractSampler sampler, int size, boolean forSmallPreview) {
		setDrawable(create(sampler, size, forSmallPreview));
	}

	public static SpriteDrawable create(AbstractSampler sampler, int size, boolean forSmallPreview) {
		return new SpriteDrawable(new Sprite(new Texture(create(sampler, size, size, forSmallPreview ? 10 : 1, 0, 0))));
	}

	public static Pixmap create(AbstractSampler sampler, int width, int height, float resolution, int offsetX, int offsetY) {
		if (sampler == null) {
			return null;
		}

		float[][] values = sampler.createValues(offsetX, offsetY, width, resolution, sampler.getSamplerSeed(), null);

		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {


				float noise = values[x][pixmap.getHeight() - y - 1];
				int color = Color.rgba8888(noise, noise, noise, 1);
				if (noise > 1) {
					color = Color.rgba8888(1f, 0f, 0f, 1f);
				}
				if (noise < 0) {
					color = Color.rgba8888(0f, 1f, 0f, 1f);
				}
				pixmap.drawPixel(x, y, color);
			}
		}
		return pixmap;
	}


	/*public static Pixmap create(AbstractSampler sampler, int width, int height, float resolution, int offsetX, int offsetY) {
		if (sampler == null) {
			return null;
		}

		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {

				int xx = x;
				int yy = y;

				if (resolution != 1) {
					xx = (int) (x * resolution);
					yy = (int) (y * resolution);
				}
				float noise = sampler.getValueAt(xx + offsetX, offsetY + pixmap.getHeight() - yy, sampler.getConfig().worldSeedModifier);
				int color = Color.rgba8888(noise, noise, noise, 1);
				if (noise > 1) {
					color = Color.rgba8888(1f, 0f, 0f, 1f);
				}
				if (noise < 0) {
					color = Color.rgba8888(0f, 1f, 0f, 1f);
				}
				pixmap.drawPixel(x, y, color);
			}
		}
		return pixmap;
	}*/

}
