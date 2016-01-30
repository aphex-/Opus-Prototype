package com.nukethemoon.tools.opusproto.layer;

import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.interpreter.TypeInterpreter;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSampler;

public class Layer extends AbstractSampler {

	private MaskedSampler[] maskSampler;

	private LayerConfig layerConfig;
	private Samplers samplers;

	private TypeInterpreter interpreter;

	public boolean active = true; // for debug

	/**
	 * Crates a new layer.
	 * @param pConfig The configuration of the layer.
	 * @param seed The world seed.
	 * @param samplers The sampler loader
	 * @throws SamplerInvalidConfigException
	 */
	public Layer(LayerConfig pConfig, double seed, Samplers samplers) throws SamplerInvalidConfigException {
		super(pConfig, seed, null, samplers);
		this.layerConfig = pConfig;
		this.samplers = samplers;
		init();
	}

	@Override
	protected float[][] bufferedCreateValues(float x, float y, int size, float scaleFactor,
											 float resolution,
											 double seedModifier, ChunkRequestBuffer buffer) {
		int resSize = (int) Math.ceil(size / resolution);
		float[][] values = new float[resSize][resSize];
		if (maskSampler == null) {
			return values;
		}

		float[][] tmpValues;
		float[][] tmpMask;

		for (int i = 0; i < maskSampler.length; i++) {
			MaskedSampler sampler = maskSampler[i];
			if (layerConfig.samplerItems[i].active) {

				float scaleMod = layerConfig.samplerItems[i].scaleModifier;
				double seedMod = getModifiedSeed(layerConfig.samplerItems[i].seedModifier, sampler.getContainingSeed());

				tmpMask = sampler.createMask(x, y, size, scaleMod, resolution, seedMod, buffer);
				tmpValues = sampler.createValues(x, y, size, scaleMod, resolution, seedMod, buffer);

				combine(values, tmpValues, tmpMask);
			}
		}


		return values;
	}

	private void combine(float[][] out, float[][] values, float[][] mask) {
		for (int x = 0; x < out.length; x++) {
			for (int y = 0; y < out[x].length; y++) {
				float combinedFactor = Math.max(0, Math.min(1f - mask[x][y], 1));
				out[x][y] = (out[x][y] * combinedFactor) + Math.max(
						0, Math.min(values[x][y] * mask[x][y], 1));
			}
		}
	}

	@Override
	public void loadConfig() throws SamplerInvalidConfigException {
		this.interpreter = samplers.getInterpreter(layerConfig.interpreterId);

		if (layerConfig.getChildSamplerConfigs() == null) {
			layerConfig.samplerItems = new ChildSamplerConfig[0];
		}
		this.maskSampler = new MaskedSampler[layerConfig.samplerItems.length];
		for (int i = 0; i < layerConfig.samplerItems.length; i++) {
			String maskSamplerId = layerConfig.samplerItems[i].samplerReferenceId;
			AbstractSampler sampler = samplers.getSampler(maskSamplerId);

			if (sampler == null) {
				throw new SamplerInvalidConfigException("The '"+ layerConfig.id + "' "
						+ Layer.class.getSimpleName() + " contains the child " +
						"sampler-reference '" + maskSamplerId + "' that does not refer to a sampler.", this);
			}

			if (!(sampler instanceof MaskedSampler)) {
				throw new SamplerInvalidConfigException("The '"+ layerConfig.id + "' "
						+ Layer.class.getSimpleName() + " contains "
						+ "the sampler '" + sampler.getConfig().id+ "' that is not a "
						+ MaskedSampler.class.getSimpleName() + ".", this);
			}
			maskSampler[i] = (MaskedSampler) sampler;
		}
	}

	@Override
	protected void compute(float[][] data) {

	}

	@Override
	public float getMaxSample() {
		Float v = null;
		for (int i = 0; i < maskSampler.length; i++) {
			MaskedSampler sampler = maskSampler[i];
			if (layerConfig.samplerItems[i].active) {
				float samplerValue = sampler.getMaxSample();
				if (v == null || samplerValue > v) {
					v = samplerValue;
				}
			}
		}
		if (v == null) {
			v = 0f;
		}
		return v;
	}

	@Override
	public float getMinSample() {
		Float v = null;
		for (int i = 0; i < maskSampler.length; i++) {
			MaskedSampler sampler = maskSampler[i];
			float samplerValue = sampler.getMinSample();
			if (layerConfig.samplerItems[i].active) {
				if (v == null || samplerValue < v) {
					v = samplerValue;
				}
			}
		}
		if (v == null) {
			v = 0f;
		}
		return v;
	}


	public TypeInterpreter getInterpreter() {
		return interpreter;
	}


}
