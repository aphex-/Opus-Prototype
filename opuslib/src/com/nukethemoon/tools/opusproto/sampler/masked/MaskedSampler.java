package com.nukethemoon.tools.opusproto.sampler.masked;

import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;

public class MaskedSampler extends AbstractSampler {

	private AbstractSampler maskSampler = null;
	private AbstractSampler outSampler = null;
	private MaskedSamplerConfig maskedSamplerConfig;

	private float maskScaleMod = 1;
	private float sampleScaleMod = 1;

	public MaskedSampler(AbstractSamplerConfiguration config, double worldSeed, Algorithms noisePool, Samplers samplers) throws SamplerInvalidConfigException {
		super(config, worldSeed, noisePool, samplers);
		maskedSamplerConfig = (MaskedSamplerConfig) config;
		init();
	}

	@Override
	protected float[][] bufferedCreateValues(float x, float y, int size,
											 float scaleFactor, double seedModifier, ChunkRequestBuffer buffer) {

		float[][] valueData = new float[size][size];

		float modifiedScaleOut = scaleFactor * config.scale * sampleScaleMod;
		float modifiedScaleMask = scaleFactor * config.scale * maskScaleMod;

		double modifiedSeed = getModifiedSeed(getContainingSeed(), seedModifier);

		if (outSampler != null) {
			float[][] maskData = createMask(x, y, size, modifiedScaleMask, seedModifier, buffer);
			valueData = outSampler.createValues(x, y, size, modifiedScaleOut, modifiedSeed, buffer);
			multiply(valueData, maskData);
		}
		return valueData;
	}

	public float [][] createMask(float x, float y, int size,
								float scaleFactor, double seedModifier, ChunkRequestBuffer buffer) {

		double modifiedSeed = getModifiedSeed(getContainingSeed(), seedModifier);
		float modifiedScaleMask = scaleFactor * config.scale * maskScaleMod;

		if (buffer != null) {
			float[][] bufferedData = buffer.getSamplerData(maskSampler.getConfig().id,
					modifiedSeed, modifiedScaleMask);

			if (bufferedData != null) {
				return bufferedData;
			}
		}



		float[][] maskData = new float[size][size];
		if (maskSampler != null) {
			return maskSampler.createValues(
					x, y,
					size,
					modifiedScaleMask,
					modifiedSeed, buffer);
		}

		if (buffer != null) {
			buffer.addSamplerData(maskSampler.getConfig().id, modifiedSeed,
					modifiedScaleMask, maskData);
		}

		return maskData;
	}

	@Override
	public void loadConfig() {
		if (maskedSamplerConfig.samplerItems != null) {
			if (maskedSamplerConfig.samplerItems.length > 0) {
				maskSampler = samplerLoader.getSampler(maskedSamplerConfig.samplerItems[0].samplerReferenceId);
				maskScaleMod = maskedSamplerConfig.samplerItems[0].scaleModifier;
			}
			if (maskedSamplerConfig.samplerItems.length > 1) {
				outSampler = samplerLoader.getSampler(maskedSamplerConfig.samplerItems[1].samplerReferenceId);
				sampleScaleMod = maskedSamplerConfig.samplerItems[1].scaleModifier;
			}
		}
	}

	@Override
	protected void compute(float[][] data) {

	}

	@Override
	public float getMaxSample() {
		if (maskSampler != null && outSampler != null) {
			return maskSampler.modify(maskSampler.getMaxSample()) * outSampler.modify(outSampler.getMaxSample());
		}
		return 0;
	}

	@Override
	public float getMinSample() {
		if (maskSampler != null && outSampler != null) {
			return maskSampler.modify(maskSampler.getMinSample()) * outSampler.modify(outSampler.getMinSample());
		}
		return 0;
	}

}
